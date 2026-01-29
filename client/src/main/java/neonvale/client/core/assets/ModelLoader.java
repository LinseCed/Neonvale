package neonvale.client.core.assets;

import neonvale.client.core.Util;
import org.joml.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ModelLoader {

    public static Scene load(String path) {
        File jarDir;
        AIScene assimpScene;
        File modelFile;
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

        processNode(scene, assimpScene, rootNode, TransformComponent.NONE_INDEX);

        return scene;
    }

    private static void processNode(Scene scene, AIScene aiScene, AINode node, int parentID) {
        Matrix4f localTransform = Util.toMatrix4f(node.mTransformation());
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
        Matrix4f globalTransform = new Matrix4f(scene.transforms.get(parentID).worldTansform).mul(localTransform);

        PointerBuffer children = node.mChildren();
        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode child = AINode.create(children.get(i));
            processNode(scene, aiScene, child, transformID);
        }
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
