package neonvale.client.core.assets;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;

public class ModelLoader {

    public static void load(String path) {
        File jarDir;
        AIScene scene;
        try {
            jarDir = new File(ModelLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            File shapeFile = new File(jarDir, path);
            scene = aiImportFile(shapeFile.getAbsolutePath(), aiProcess_Triangulate);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (scene == null || scene.mRootNode() == null) {
            throw new RuntimeException("Failed to load Model: " + path);
        }
        List<Mesh> meshes = new ArrayList<>();
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
            AITexture albedoTex;
            AIString albedoTexpath = AIString.calloc();
            result = Assimp.aiGetMaterialTexture(mat, aiTextureType_BASE_COLOR, 0, albedoTexpath, (IntBuffer) null, (IntBuffer) null, (FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null);
            if (result == aiReturn_SUCCESS) {
                material.albedoTex = new Texture(albedoTexpath.data()).getId();
            }
            albedoTexpath.free();

            float[] tmp = new float[1];

            // Metallic Factor
            result = Assimp.aiGetMaterialFloatArray(mat, AI_MATKEY_METALLIC_FACTOR, aiTextureType_NONE, 0, tmp,null);
            if (result == aiReturn_SUCCESS) {
                material.metallicFactor = tmp[0];
            }

            result = Assimp.aiGetMaterialFloatArray(mat, AI_MATKEY_ROUGHNESS_FACTOR, aiTextureType_NONE, 0, tmp,null);
            if (result == aiReturn_SUCCESS) {
                material.roughness = tmp[0];
            }

            materials.add(material);
        }

        System.out.println(materials.size());

        for (int i = 0; i < scene.mNumMeshes(); i++) {
            try (AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(i))) {
                Mesh neonvaleMesh = new Mesh();

                int vertexCount = mesh.mNumVertices();
                AIVector3D.Buffer vertices = mesh.mVertices();
                FloatBuffer vertexBuffer = memAllocFloat(vertexCount * 3);
                for (int j = 0; j < vertexCount; j++) {
                    AIVector3D v = vertices.get(j);
                    vertexBuffer.put(v.x()).put(v.y()).put(v.z());
                }
                vertexBuffer.flip();

                int faceCount = mesh.mNumFaces();
                AIFace.Buffer faces = mesh.mFaces();
                IntBuffer indexBuffer = memAllocInt(faceCount * 3);
                for (int j = 0; j < faceCount; j++) {
                    AIFace face = faces.get(j);
                    if (face.mNumIndices() != 3) {
                        throw new RuntimeException("Non triangulated face detected.");
                    }
                }
                indexBuffer.flip();

                if (mesh.mNormals() != null) {
                    neonvaleMesh.hasNormals(true);
                    AIVector3D.Buffer normals = mesh.mNormals();
                    FloatBuffer normalBuffer = memAllocFloat(vertexCount * 3);
                    for (int j = 0; j < vertexCount; j++) {
                        AIVector3D normal = normals.get(j);
                        normalBuffer.put(normal.x()).put(normal.y()).put(normal.z());
                    }
                    normalBuffer.flip();
                } else {
                    neonvaleMesh.hasNormals(false);
                }

                if (mesh.mTextureCoords(0) != null) {
                    neonvaleMesh.hasTextureCoordinates(true);
                    AIVector3D.Buffer texCoords = mesh.mTextureCoords(0);
                    FloatBuffer texCoordBuffer = memAllocFloat(vertexCount * 2);
                    for (int j = 0; j < texCoordBuffer.limit(); j++) {
                        AIVector3D texCoord = texCoords.get(j);
                        texCoordBuffer.put(texCoord.x()).put(texCoord.y());
                    }
                    texCoordBuffer.flip();
                } else {
                    neonvaleMesh.hasTextureCoordinates(false);
                }



            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
