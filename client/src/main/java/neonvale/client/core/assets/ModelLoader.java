package neonvale.client.core.assets;

import neonvale.client.core.Util;
import neonvale.client.graphics.VAO;
import org.joml.Vector4f;
import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ModelLoader {

    public static Model load(String path) {
        File jarDir;
        AIScene scene;
        File shapeFile;
        try {
            jarDir = new File(ModelLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            shapeFile = new File(jarDir, path);
            scene = aiImportFile(shapeFile.getAbsolutePath(), aiProcess_Triangulate | aiProcess_CalcTangentSpace);
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
                String texPath = albedoTexPath.dataString();
                ByteBuffer data = loadTextureData(scene, shapeFile, texPath);
                material.albedoTex = new Texture(data, TextureColorSpace.SRGB).getId();
                material.hasAlbedoTexture = true;
                memFree(data);
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
            result = Assimp.aiGetMaterialTexture(mat, AI_MATKEY_GLTF_PBRMETALLICROUGHNESS_METALLICROUGHNESS_TEXTURE, 0, metallicRoughnessTexPath, (IntBuffer) null, (IntBuffer) null, (FloatBuffer) null, (IntBuffer) null, (IntBuffer) null, (IntBuffer) null);
            if (result == aiReturn_SUCCESS) {
                String texPath = metallicRoughnessTexPath.dataString();
                ByteBuffer data = loadTextureData(scene, shapeFile, texPath);
                material.metallicRoughnessMap = new Texture(data, TextureColorSpace.LINEAR).getId();
                material.hasMetallicRoughnessTexture = true;
                memFree(data);

            }
            metallicRoughnessTexPath.free();

            materials.add(material);
        }

        for (int i = 0; i < scene.mNumMeshes(); i++) {
            try (AIMesh mesh = AIMesh.create(Objects.requireNonNull(scene.mMeshes()).get(i))) {
                boolean hasNormals;
                boolean hasUVs;
                boolean hasTangents;
                VAO vao = new VAO();

                int vertexCount = mesh.mNumVertices();
                AIVector3D.Buffer vertices = mesh.mVertices();
                FloatBuffer vertexBuffer = memAllocFloat(vertexCount * 3);
                for (int j = 0; j < vertexCount; j++) {
                    AIVector3D v = vertices.get(j);
                    vertexBuffer.put(v.x()).put(v.y()).put(v.z());
                }
                vertexBuffer.flip();
                vao.addAttribute(0, 3, vertexBuffer);
                memFree(vertexBuffer);

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
                    hasNormals = true;
                    AIVector3D.Buffer normals = mesh.mNormals();
                    FloatBuffer normalBuffer = memAllocFloat(vertexCount * 3);
                    for (int j = 0; j < vertexCount; j++) {
                        AIVector3D normal = normals.get(j);
                        normalBuffer.put(normal.x()).put(normal.y()).put(normal.z());
                    }
                    normalBuffer.flip();
                    vao.addAttribute(1, 3, normalBuffer);
                    memFree(normalBuffer);
                } else {
                    hasNormals = false;
                }

                if (mesh.mTextureCoords(0) != null) {
                    hasUVs = true;
                    AIVector3D.Buffer texCoords = mesh.mTextureCoords(0);
                    FloatBuffer texCoordBuffer = memAllocFloat(vertexCount * 2);
                    for (int j = 0; j < vertexCount; j++) {
                        AIVector3D texCoord = texCoords.get(j);
                        texCoordBuffer.put(texCoord.x()).put(texCoord.y());
                    }
                    texCoordBuffer.flip();
                    vao.addAttribute(2, 2, texCoordBuffer);
                    memFree(texCoordBuffer);
                } else {
                    hasUVs = false;
                }

                if (mesh.mTangents() != null && mesh.mBitangents() != null && mesh.mNormals() != null && mesh.mTextureCoords(0) != null) {
                    hasTangents = true;
                    AIVector3D.Buffer tangents = mesh.mTangents();
                    AIVector3D.Buffer bitangents = mesh.mBitangents();
                    AIVector3D.Buffer normals = mesh.mNormals();
                    FloatBuffer tangentsBuffer = memAllocFloat(vertexCount * 4);
                    for (int j = 0; j < vertexCount; j++) {
                        AIVector3D tangent = tangents.get(j);
                        AIVector3D bitangent = bitangents.get(j);
                        AIVector3D normal = normals.get(j);

                        float cx = normal.y() * tangent.z() - normal.z() * tangent.y();
                        float cy = normal.z() * tangent.x() - normal.x() * tangent.z();
                        float cz = normal.x() * tangent.y() - normal.y() * tangent.x();

                        float dot = cx * bitangent.x() + cy * bitangent.y() + cz * bitangent.z();

                        float sign = dot < 0.0f ? -1.0f : 1.0f;

                        tangentsBuffer.put(tangent.x()).put(tangent.y()).put(tangent.z()).put(sign);
                    }
                    tangentsBuffer.flip();
                    vao.addAttribute(3, 4, tangentsBuffer);
                    memFree(tangentsBuffer);
                } else {
                    hasTangents = false;
                }
                vao.setIndices(indexBuffer);
                meshes.add(new Mesh(vao, indexBuffer.limit(), mesh.mMaterialIndex(), hasNormals, hasUVs, hasTangents));
                memFree(indexBuffer);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return new Model(meshes, materials);
    }

    private static ByteBuffer loadTextureData(AIScene scene, File modelFile, String texPath) {
        if (texPath.startsWith("*")) {
            int index = Integer.parseInt(texPath.substring(1));
            AITexture tex = AITexture.create(scene.mTextures().get(index));

            if (tex.mHeight() != 0) {
                throw new UnsupportedOperationException("Uncompressed embedded textures not supported yet");
            }

            ByteBuffer src = tex.pcDataCompressed();
            ByteBuffer copy = memAlloc(tex.mWidth());
            copy.put(src.limit(tex.mWidth())).flip();
            return copy;
        } else {
            File texFile = new File(modelFile.getParentFile(), texPath);
            return Util.readFileToBuffer(texFile);
        }
    }

}
