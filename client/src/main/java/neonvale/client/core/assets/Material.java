package neonvale.client.core.assets;

import neonvale.client.graphics.Shader;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class Material {
    public int albedoTex;
    public int normalMap;
    public int metallicRoughnessMap;
    public String name;

    public Vector4f baseColorFactor = new Vector4f(1, 1, 1, 1);
    public float metallicFactor = 1.0f;
    public float roughness = 1.0f;

    public boolean hasNormalMap;
    public boolean hasAlbedoTexture;
    public boolean hasMetallicRoughnessTexture;

    public Material() {
    }

    public void bindMaterial(Shader shader) {
        shader.use();

        if (hasAlbedoTexture) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, albedoTex);
        }

        if (hasNormalMap) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, normalMap);
        }

        if (hasMetallicRoughnessTexture) {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, metallicRoughnessMap);
        }

        shader.uniform4f(baseColorFactor, "uBaseColorFactor");
        shader.uniform1f(metallicFactor, "uMetallicFactor");
        shader.uniform1f(roughness, "uRoughness");
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Material: \n");
        sb.append("Name: ").append(name).append("\n");
        return sb.toString();
    }
}
