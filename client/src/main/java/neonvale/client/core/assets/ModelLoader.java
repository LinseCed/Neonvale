package neonvale.client.core.assets;

import org.joml.Vector4f;
import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memAllocInt;

public class ModelLoader {

    public static Model load(String path) {
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
            AIString albedoTexPath = AIString.calloc();
            result = Assimp.aiGetMaterialTexture(mat, aiTextureType_BASE_COLOR, 0, albedoTexPath, (IntBuffer) null, (IntBuffer) null, (FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null);
            if (result == aiReturn_SUCCESS) {
                material.hasAlbedoTexture = true;
                material.albedoTex = new Texture(albedoTexPath.data(), TextureColorSpace.SRGB).getId();
            }
            albedoTexPath.free();

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
            result = Assimp.aiGetMaterialTexture(mat, aiTextureType_UNKNOWN, 0, metallicRoughnessTexPath, (IntBuffer) null, (IntBuffer) null, (FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null);
            if (result == aiReturn_SUCCESS) {
                material.hasMetallicRoughnessTexture = true;
                material.metallicRoughnessMap = new Texture(metallicRoughnessTexPath.data(), TextureColorSpace.LINEAR).getId();
            }
            metallicRoughnessTexPath.free();

            materials.add(material);
        }

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
                    IntBuffer indices = face.mIndices();
                    indexBuffer.put(indices.get(0)).put(indices.get(1)).put(indices.get(2));
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
                    for (int j = 0; j < vertexCount; j++) {
                        AIVector3D texCoord = texCoords.get(j);
                        texCoordBuffer.put(texCoord.x()).put(texCoord.y());
                    }
                    texCoordBuffer.flip();
                } else {
                    neonvaleMesh.hasTextureCoordinates(false);
                }

                if (mesh.mTangents() != null) {
                    neonvaleMesh.hasTangents(true);
                    AIVector3D.Buffer tangents = mesh.mTangents();
                    FloatBuffer tangentsBuffer = memAllocFloat(vertexCount * 3);
                    for (int j = 0; j < tangentsBuffer.limit(); j++) {
                        AIVector3D tangent = tangents.get(j);
                        tangentsBuffer.put(tangent.x()).put(tangent.y()).put(tangent.z());
                    }
                    tangentsBuffer.flip();
                } else {
                    neonvaleMesh.hasTangents(false);
                }

                neonvaleMesh.setMaterialIndex(mesh.mMaterialIndex());
                meshes.add(neonvaleMesh);

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return new Model(meshes, materials);
    }
}
