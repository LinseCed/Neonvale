package neonvale.client.core.assets;

import neonvale.client.graphics.Shader;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class Material {
    public int albedoTex;
    public int normalTex;
    public int metallicRoughnessMap;
    public int occlusionTex;
    public int emissiveTex;
    public String name;

    public Vector4f baseColorFactor = new Vector4f(1, 1, 1, 1);
    public float metallicFactor = 1.0f;
    public float roughness = 1.0f;
    public Vector3f emissiveFactor = new Vector3f(0, 0, 0);

    public boolean hasNormalMap;
    public boolean hasEmissive;

    public Material() {
    }

    public void bindMaterial(Shader shader) {
        shader.use();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, albedoTex);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, normalTex);

        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, metallicRoughnessMap);

        shader.uniform4f(baseColorFactor, "uBaseColorFactor");
        shader.uniform1f(metallicFactor, "uMetallicFactor");
        shader.uniform1f(roughness, "uRoughness");
        shader.uniform3f(emissiveFactor, "uEmissiveFactor");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Material: \n");
        sb.append("Name: ").append(name).append("\n");
        return sb.toString();
    }
}
