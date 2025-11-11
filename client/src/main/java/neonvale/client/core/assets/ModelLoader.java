package neonvale.client.core.assets;

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
        PointerBuffer materials = scene.mMaterials();
        for (int m = 0; m < scene.mNumMeshes(); m++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(m));
            AIMaterial material = AIMaterial.create(materials.get(mesh.mMaterialIndex()));

            int vertexCount = mesh.mNumVertices();
            AIVector3D.Buffer vertices = mesh.mVertices();
            FloatBuffer vertexBuffer = memAllocFloat(vertexCount * 3);
            for (int i = 0; i < vertexCount; i++) {
                AIVector3D v = vertices.get(i);
                vertexBuffer.put(v.x()).put(v.y()).put(v.z());
            }
            vertexBuffer.flip();

            FloatBuffer normalBuffer = null;
            if (mesh.mNormals() != null) {
                AIVector3D.Buffer normals = mesh.mNormals();
                normalBuffer = memAllocFloat(vertexCount * 3);
                for (int i = 0; i < vertexCount; i++) {
                    AIVector3D v = normals.get(i);
                    normalBuffer.put(v.x()).put(v.y()).put(v.z());
                }
                normalBuffer.flip();
            }

            int faceCount = mesh.mNumFaces();
            AIFace.Buffer faces = mesh.mFaces();
            IntBuffer indexBuffer = memAllocInt(faceCount * 3);
            for (int i = 0; i < faceCount; i++) {
                AIFace face = faces.get(i);
                if (face.mNumIndices() != 3) {
                    throw new RuntimeException("Non triangulated face detected");
                }
                indexBuffer.put(face.mIndices());
            }
            indexBuffer.flip();

            FloatBuffer texCoordsBuffer = null;
            if (mesh.mTextureCoords(0) != null) {
                AIVector3D.Buffer texCoords = mesh.mTextureCoords(0);
                texCoordsBuffer = memAllocFloat(vertexCount * 2);
                for (int i = 0; i < texCoords.limit(); i++) {
                    AIVector3D texCoord = texCoords.get(i);
                    texCoordsBuffer.put(texCoord.x()).put(texCoord.y());
                }
                texCoordsBuffer.flip();
            }


            AIString materialPath = AIString.calloc();
            if (aiGetMaterialTexture(material, aiTextureType_BASE_COLOR, 0, materialPath, (IntBuffer) null, null, null, null, null, null) == aiReturn_SUCCESS) {
                if (materialPath.dataString().startsWith("*")) {
                    int textureId = Integer.parseInt(materialPath.dataString().substring(1));
                    AITexture embedded = AITexture.create(scene.mTextures().get(textureId));
                    if (embedded.mHeight() == 0 && texCoordsBuffer != null) {
                        ByteBuffer encodedData = embedded.pcDataCompressed();
                        Texture texture = new Texture(encodedData);
                        meshes.add(new Mesh(vertexBuffer, normalBuffer, indexBuffer, texture, texCoordsBuffer));
                    } else {
                        AITexel.Buffer raw = embedded.pcData();
                    }
                }
            } else {
                meshes.add(new Mesh(vertexBuffer, normalBuffer, indexBuffer));
            }
            materialPath.free();
        }
        aiReleaseImport(scene);

        return new Model(meshes);
    }
}
