package neonvale.client.core.assets;

import neonvale.client.core.Entity;
import neonvale.client.core.Util;
import neonvale.client.core.World;
import neonvale.client.core.components.MeshRendererComponent;
import neonvale.client.core.components.TransformComponent;
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

    public static void load(String path, World world) {
        load(path, world, new Matrix4f());
    }

    public static void load(String path, World world, Matrix4f rootTransform) {
        File jarDir;
        AIScene assimpScene;
        File modelFile;
        try {
            jarDir = new File(ModelLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            modelFile = new File(jarDir, path);
            assimpScene = aiImportFile(modelFile.getAbsolutePath(),
                    aiProcess_Triangulate | aiProcess_CalcTangentSpace | aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (assimpScene == null || assimpScene.mRootNode() == null) {
            throw new RuntimeException("Failed to load model: " + path);
        }

        List<MaterialData> materials = loadMaterials(assimpScene, modelFile);
        Map<Integer, MeshData> meshCache = new HashMap<>();
        processNode(world, assimpScene, assimpScene.mRootNode(), rootTransform, meshCache, materials);
    }

    private static void processNode(World world, AIScene aiScene, AINode node, Matrix4f parentWorldTransform,
                                    Map<Integer, MeshData> meshCache, List<MaterialData> materials) {
        Matrix4f localTransform = Util.toMatrix4f(node.mTransformation());
        Matrix4f worldTransform = new Matrix4f(parentWorldTransform).mul(localTransform);

        Vector3f position = worldTransform.getTranslation(new Vector3f());
        Quaternionf rotation = worldTransform.getUnnormalizedRotation(new Quaternionf()).normalize();
        Vector3f scale = worldTransform.getScale(new Vector3f());

        Entity entity = new Entity();
        entity.addComponent(new TransformComponent(position, rotation, scale, worldTransform));

        IntBuffer meshIndices = node.mMeshes();
        if (meshIndices != null && node.mNumMeshes() > 0) {
            MeshRendererComponent meshRenderer = new MeshRendererComponent();
            for (int i = 0; i < node.mNumMeshes(); i++) {
                int meshIndex = meshIndices.get(i);
                AIMesh aiMesh = AIMesh.create(aiScene.mMeshes().get(meshIndex));
                MeshData meshData = meshCache.computeIfAbsent(meshIndex, idx -> buildMeshData(aiMesh));
                meshRenderer.addMesh(meshData, materials.get(aiMesh.mMaterialIndex()));
            }
            entity.addComponent(meshRenderer);
        }

        world.addEntity(entity);

        PointerBuffer children = node.mChildren();
        if (children != null) {
            for (int i = 0; i < node.mNumChildren(); i++) {
                AINode child = AINode.create(children.get(i));
                processNode(world, aiScene, child, worldTransform, meshCache, materials);
            }
        }
    }

    private static MeshData buildMeshData(AIMesh mesh) {
        AIVector3D.Buffer aiVertices = mesh.mVertices();
        AIVector3D.Buffer aiNormals = mesh.mNormals();
        AIVector3D.Buffer aiUVs = mesh.mTextureCoords(0);
        AIVector3D.Buffer aiTangents = mesh.mTangents();
        AIVector3D.Buffer aiBitangents = mesh.mBitangents();

        int vertexCount = mesh.mNumVertices();
        Vertex[] vertices = new Vertex[vertexCount];

        for (int v = 0; v < vertexCount; v++) {
            vertices[v] = new Vertex();
            AIVector3D pos = aiVertices.get(v);
            vertices[v].pos = new Vector3f(pos.x(), pos.y(), pos.z());

            if (aiNormals != null) {
                AIVector3D normal = aiNormals.get(v);
                vertices[v].normal = new Vector3f(normal.x(), normal.y(), normal.z()).normalize();
            } else {
                vertices[v].normal = new Vector3f(0.0f, 0.0f, 1f);
            }

            if (aiUVs != null) {
                AIVector3D uv = aiUVs.get(v);
                vertices[v].uv = new Vector2f(uv.x(), uv.y());
            } else {
                vertices[v].uv = new Vector2f(0.0f, 0.0f);
            }

            if (aiTangents != null && aiBitangents != null && aiNormals != null) {
                AIVector3D tangent = aiTangents.get(v);
                AIVector3D bitangent = aiBitangents.get(v);
                AIVector3D normal = aiNormals.get(v);

                float cx = normal.y() * tangent.z() - normal.z() * tangent.y();
                float cy = normal.z() * tangent.x() - normal.x() * tangent.z();
                float cz = normal.x() * tangent.y() - normal.y() * tangent.x();

                float dot = cx * bitangent.x() + cy * bitangent.y() + cz * bitangent.z();
                float sign = dot < 0.0f ? -1.0f : 1.0f;

                vertices[v].tangent = new Vector4f(tangent.x(), tangent.y(), tangent.z(), sign);
            } else {
                vertices[v].tangent = new Vector4f(1.0f, 0.0f, 0.0f, 1.0f);
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

    private static List<MaterialData> loadMaterials(AIScene scene, File modelFile) {
        List<MaterialData> materials = new ArrayList<>();
        for (int i = 0; i < scene.mNumMaterials(); i++) {
            MaterialData material = new MaterialData();
            AIMaterial mat = AIMaterial.create(Objects.requireNonNull(scene.mMaterials()).get(i));

            AIString name = AIString.calloc();
            Assimp.aiGetMaterialString(mat, Assimp.AI_MATKEY_NAME, aiTextureType_NONE, 0, name);
            material.name = name.dataString();
            name.free();

            AIColor4D color = AIColor4D.create();
            if (Assimp.aiGetMaterialColor(mat, AI_MATKEY_BASE_COLOR, aiTextureType_NONE, 0, color) == aiReturn_SUCCESS) {
                material.baseColorFactor = new Vector4f(color.r(), color.g(), color.b(), color.a());
            }

            AIString albedoTexPath = AIString.calloc();
            if (Assimp.aiGetMaterialTexture(mat, aiTextureType_BASE_COLOR, 0, albedoTexPath,
                    (IntBuffer) null, null, null, null, null, null) == aiReturn_SUCCESS) {
                ByteBuffer data = loadTextureData(scene, modelFile, albedoTexPath.dataString());
                material.albedoTex = new Texture(data, TextureColorSpace.SRGB).getId();
                memFree(data);
            }
            albedoTexPath.free();

            AIString normalMapPath = AIString.calloc();
            if (Assimp.aiGetMaterialTexture(mat, aiTextureType_NORMALS, 0, normalMapPath,
                    (IntBuffer) null, null, null, null, null, null) == aiReturn_SUCCESS) {
                ByteBuffer data = loadTextureData(scene, modelFile, normalMapPath.dataString());
                material.normalMap = new Texture(data, TextureColorSpace.LINEAR).getId();
                memFree(data);
            }
            normalMapPath.free();

            float[] tmp = new float[1];
            if (Assimp.aiGetMaterialFloatArray(mat, AI_MATKEY_METALLIC_FACTOR, aiTextureType_NONE, 0, tmp, null) == aiReturn_SUCCESS) {
                material.metallicFactor = tmp[0];
            }
            if (Assimp.aiGetMaterialFloatArray(mat, AI_MATKEY_ROUGHNESS_FACTOR, aiTextureType_NONE, 0, tmp, null) == aiReturn_SUCCESS) {
                material.roughness = tmp[0];
            }

            AIString metallicRoughnessTexPath = AIString.calloc();
            if (Assimp.aiGetMaterialTexture(mat, AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLICROUGHNESS_TEXTURE, 0,
                    metallicRoughnessTexPath, (IntBuffer) null, null, null, null, null, null) == aiReturn_SUCCESS) {
                ByteBuffer data = loadTextureData(scene, modelFile, metallicRoughnessTexPath.dataString());
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
                throw new UnsupportedOperationException("Uncompressed embedded textures not supported");
            }
            ByteBuffer src = tex.pcDataCompressed();
            ByteBuffer copy = memAlloc(tex.mWidth());
            copy.put(src.slice(0, tex.mWidth())).flip();
            return copy;
        } else {
            return Util.readFileToBuffer(new File(modelFile.getParentFile(), texPath));
        }
    }
}
