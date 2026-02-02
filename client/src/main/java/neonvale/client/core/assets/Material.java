package neonvale.client.core.assets;

import neonvale.client.core.Util;
import org.joml.Vector4f;


public class Material {
    public int albedoTex = Util.create1x1Texture(255,255, 255, 255, TextureColorSpace.SRGB);
    public int normalMap = Util.create1x1Texture(128, 128, 255, 255, TextureColorSpace.LINEAR);
    public int metallicRoughnessMap = Util.create1x1Texture(255, 255, 255, 255, TextureColorSpace.LINEAR);
    public String name;

    public Vector4f baseColorFactor = new Vector4f(1, 1, 1, 1);
    public float metallicFactor = 1.0f;
    public float roughness = 1.0f;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Material: \n");
        sb.append("Name: ").append(name).append("\n");
        return sb.toString();
    }
}
