package neonvale.client.core.assets;

import neonvale.client.graphics.Shader;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.*;

public class Material {
    private Shader shader;
    private int albedoTex;
    private int normalTex;
    private int metallicRoughnessMap;
    private int occlusionTex;
    private int emissiveTex;

    private Vector4f baseColorFactor = new Vector4f(1, 1, 1, 1);
    private float metallicFactor = 1.0f;
    private float roughness = 1.0f;
    private Vector3f emissiveFactor = new Vector3f(0, 0, 0);

    private boolean hasNormalMap;
    private boolean hasEmissive;

    public void bindMaterial() {
        this.shader.use();

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
}
