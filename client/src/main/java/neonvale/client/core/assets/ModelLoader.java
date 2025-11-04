package neonvale.client.core.assets;

import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.io.File;
import java.net.URISyntaxException;
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
            scene = aiImportFile(shapeFile.getAbsolutePath(), aiProcess_Triangulate | aiProcess_FlipUVs);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        if (scene == null || scene.mRootNode() == null) {
            throw  new RuntimeException("Failed to load Model: " + path);
        }
        List<Mesh> meshes = new ArrayList<>();
        for (int m = 0; m < scene.mNumMeshes(); m++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

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
                    throw  new RuntimeException("Non triangulated face detected");
                }
                indexBuffer.put(face.mIndices());
            }
            indexBuffer.flip();

            meshes.add(new Mesh(vertexBuffer, normalBuffer, indexBuffer));
        }
        aiReleaseImport(scene);

        return new Model(meshes);
    }
}
