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
            AIMaterial mat = AIMaterial.create(scene.mMaterials().get(i));

            AIString name = AIString.calloc();
            Assimp.aiGetMaterialString(mat, Assimp.AI_MATKEY_NAME, aiTextureType_NONE, 0, name);
            material.name = name.dataString();

            AIColor4D color = AIColor4D.create();
            int result = Assimp.aiGetMaterialColor(mat, AI_MATKEY_BASE_COLOR, aiTextureType_NONE, 0, color);
        }
    }
}
