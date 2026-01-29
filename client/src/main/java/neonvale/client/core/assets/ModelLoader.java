package neonvale.client.core.assets;

import neonvale.client.core.Util;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ModelLoader {

    public static Scene load(String path) {
        File jarDir;
        AIScene assimpScene;
        File modelFile;
        Map<Integer, Integer> meshCache = new HashMap<>();
        try {
            jarDir = new File(ModelLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            modelFile = new File(jarDir, path);
            assimpScene = aiImportFile(modelFile.getAbsolutePath(), aiProcess_Triangulate | aiProcess_CalcTangentSpace);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (assimpScene == null || assimpScene.mRootNode() == null) {
            throw new RuntimeException("Failed to load Model: " + path);
        }

        Scene scene = new Scene();

        List<Material> materials = loadMaterials(assimpScene, modelFile);

        AINode rootNode = assimpScene.mRootNode();

        processNode(scene, assimpScene, rootNode, TransformComponent.NONE_INDEX, meshCache);

        return scene;
    }

    private static void processNode(Scene scene, AIScene aiScene, AINode node, int parentID, Map<Integer, Integer> meshCache) {
        int transformID = scene.transforms.size();
        scene.transforms.add(new TransformComponent(
                new Vector3f(0.0f),
                new Quaternionf(),
                new Vector3f(1.0f),
                new Matrix4f().identity(),
                TransformComponent.NONE_INDEX,
                TransformComponent.NONE_INDEX,
                TransformComponent.NONE_INDEX
        ));

        Matrix4f transform = Util.toMatrix4f(node.mTransformation());
        Vector3f position = new Vector3f();
        Quaternionf rotation = new Quaternionf();
        Vector3f scale = new Vector3f();
        transform.getTranslation(position);
        transform.getUnnormalizedRotation(rotation);
        rotation.normalize();
        transform.getScale(scale);

        TransformComponent t = scene.transforms.get(transformID);

        t.position = position;
        t.rotation = rotation;
        t.scale = scale;

        scene.transforms.get(transformID).parentID = parentID;

        if (parentID != TransformComponent.NONE_INDEX) {
            TransformComponent parent = scene.transforms.get(parentID);
            if (parent.firstChildID == TransformComponent.NONE_INDEX) {
                parent.firstChildID = transformID;
            } else {
                int sibling = parent.firstChildID;
                while (scene.transforms.get(sibling).nextSiblingID != TransformComponent.NONE_INDEX) {
                    sibling = scene.transforms.get(sibling).nextSiblingID;
                }
                scene.transforms.get(sibling).nextSiblingID = transformID;
            }
        }

        Matrix4f local = new Matrix4f().translate(t.position).rotate(t.rotation).scale(t.scale);

        if (t.parentID != TransformComponent.NONE_INDEX) {
            t.worldtransform = new Matrix4f(scene.transforms.get(parentID).worldtransform).mul(local);
        } else {
            t.worldtransform = local;
        }

        IntBuffer meshIndices = node.mMeshes();
        if (meshIndices != null) {
            for (int i = 0; i < node.mNumMeshes(); i++) {
                int meshIndex = meshIndices.get(i);
                AIMesh mesh = AIMesh.create(aiScene.mMeshes().get(meshIndex));
                Integer meshDataID = meshCache.get(meshIndex);
                if (meshDataID == null) {

                    meshDataID = scene.meshData.size();

                    MeshData meshData = buildMeshData(mesh);
                    scene.meshData.add(meshData);

                    meshCache.put(meshIndex, meshDataID);
                }
                int materialIndex = mesh.mMaterialIndex();
                scene.renderObjects.add(new RenderObject(meshDataID, transformID, materialIndex));
            }
        }

        PointerBuffer children = node.mChildren();
        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode child = AINode.create(children.get(i));
            processNode(scene, aiScene, child, transformID, meshCache);
        }
    }

    private static MeshData buildMeshData(AIMesh mesh) {
        AIVector3D.Buffer aiVertices = mesh.mVertices();
        AIVector3D.Buffer aiNormals = mesh.mNormals();
        AIVector3D.Buffer aiUVs = mesh.mTextureCoords(0);

        int vertexCount = mesh.mNumVertices();
        Vertex[] vertices = new Vertex[vertexCount];

        for (int v = 0; v < vertexCount; v++) {
            vertices[v] = new Vertex();
            AIVector3D pos = aiVertices.get(v);
            vertices[v].pos = new Vector3f(pos.x(), pos.y(), pos.z());

            if (aiNormals != null) {
                AIVector3D normal = aiNormals.get(v);
                vertices[v].normal = new Vector3f(normal.x(), normal.y(), normal.z());
            }

            if (aiUVs != null) {
                AIVector3D uv = aiUVs.get(v);
                vertices[v].uv = new Vector2f(uv.x(), uv.y());
            }
        }

        int faceCount = mesh.mNumFaces();
        int[] indices = new int[faceCount * 3];

        AIFace.Buffer faces = mesh.mFaces();
        for (int f = 0; f < faceCount; f++) {
            AIFace face = faces.get(f);
            indices[f * 3] = face.mIndices().get(0);
            indices[f * 3 + 1] = face.mIndices().get(1);
            indices[f * 3 + 2] = face.mIndices().get(2);
        }

        return new MeshData(vertices, indices);
    }

    private static List<Material> loadMaterials(AIScene scene, File modelFile) {
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < scene.mNumMaterials(); i++) {
            Material material = new Material();
            AIMaterial mat = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i));

            // Material Name
            AIString name = AIString.calloc();
            Assimp.aiGetMaterialString(mat, Assimp.AI_MATKEY_NAME, aiTextureType_NONE, 0, name);
            material.name = name.dataString();
            name.free();

            // Base Color Factor
            AIColor4D color = AIColor4D.create();
            int result = Assimp.aiGetMaterialColor(mat, AI_MATKEY_BASE_COLOR, aiTextureType_NONE, 0, color);
            if (result == aiReturn_SUCCESS) {
                material.baseColorFactor = new Vector4f(color.r(), color.g(), color.b(), color.a());
            }

            // Albedo Texture
            AIString albedoTexPath = AIString.calloc();
            result = Assimp.aiGetMaterialTexture(mat, aiTextureType_BASE_COLOR, 0, albedoTexPath, (IntBuffer) null, null, null, null, null, null);
            if (result == aiReturn_SUCCESS) {
                String texPath = albedoTexPath.dataString();
                ByteBuffer data = loadTextureData(scene, modelFile, texPath);
                material.albedoTex = new Texture(data, TextureColorSpace.SRGB).getId();
                memFree(data);
            }
            albedoTexPath.free();

            // Normal Map
            AIString normalMapPath = AIString.calloc();
            result = Assimp.aiGetMaterialTexture(mat, aiTextureType_NORMALS, 0, normalMapPath, (IntBuffer) null, null, null, null, null, null);
            if (result == aiReturn_SUCCESS) {
                String texPath = normalMapPath.dataString();
                ByteBuffer data = loadTextureData(scene, modelFile, texPath);
                material.normalMap = new Texture(data, TextureColorSpace.LINEAR).getId();
                memFree(data);
            }
            normalMapPath.free();

            float[] tmp = new float[1];

            // Metallic Factor
            result = Assimp.aiGetMaterialFloatArray(mat, AI_MATKEY_METALLIC_FACTOR, aiTextureType_NONE, 0, tmp,null);
            if (result == aiReturn_SUCCESS) {
                material.metallicFactor = tmp[0];
            }

            // Roughness Factor
            result = Assimp.aiGetMaterialFloatArray(mat, AI_MATKEY_ROUGHNESS_FACTOR, aiTextureType_NONE, 0, tmp,null);
            if (result == aiReturn_SUCCESS) {
                material.roughness = tmp[0];
            }

            // Metallic Roughness Texture
            AIString metallicRoughnessTexPath = AIString.calloc();
            result = Assimp.aiGetMaterialTexture(mat, AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLICROUGHNESS_TEXTURE, 0, metallicRoughnessTexPath, (IntBuffer) null, null, null, null, null, null);
            if (result == aiReturn_SUCCESS) {
                String texPath = metallicRoughnessTexPath.dataString();
                ByteBuffer data = loadTextureData(scene, modelFile, texPath);
                material.metallicRoughnessMap = new Texture(data, TextureColorSpace.LINEAR).getId();
                memFree(data);

            }
            metallicRoughnessTexPath.free();

            materials.add(material);
        }
        return materials;
    }

    private static ByteBuffer loadTextureData(AIScene scene, File modelFile, String texPath) {
        if (texPath.startsWith("*")) {
            int index = Integer.parseInt(texPath.substring(1));
            AITexture tex = AITexture.create(scene.mTextures().get(index));

            if (tex.mHeight() != 0) {
                throw new UnsupportedOperationException("Uncompressed embedded textures not supported yet");
            }

            ByteBuffer src = tex.pcDataCompressed();
            ByteBuffer slice = src.slice(0, tex.mWidth());
            ByteBuffer copy = memAlloc(tex.mWidth());
            copy.put(slice).flip();
            return copy;
        } else {
            File texFile = new File(modelFile.getParentFile(), texPath);
            return Util.readFileToBuffer(texFile);
        }
    }

}
