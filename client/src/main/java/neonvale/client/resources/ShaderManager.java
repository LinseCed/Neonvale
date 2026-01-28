package neonvale.client.resources;


import neonvale.client.graphics.Shader;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static ShaderManager instance = new ShaderManager();
    private final Map<String, Shader> shaders;

    String basicVertexShader = "basic.vert";
    String basicFragmentShader = "basic.frag";
    String basicTextureVertexShader = "basicTexture.vert";
    String basicTextureFragmentShader = "basicTexture.frag";

    private ShaderManager() {
        this.shaders = new HashMap<>();
        loadShaders();
    }

    public static ShaderManager getInstance() {
        if (instance == null) {
            instance = new ShaderManager();
        }
        return instance;
    }

    private void loadShaders() {
        Shader basicShader = new Shader(basicVertexShader, basicFragmentShader);
        shaders.put("Basic", basicShader);
        Shader basicTextureShader = new Shader(basicTextureVertexShader, basicTextureFragmentShader);
        shaders.put("BasicTexture", basicTextureShader);
    }

    public void use(String shaderName) {
        if (shaders.containsKey(shaderName)) {
            shaders.get(shaderName).bind();
        } else {
            throw new RuntimeException("Shader " + shaderName + " not found.");
        }
    }

    public void uniformMat4(String shaderName, Matrix4f mat, String uniform) {
        if (shaders.containsKey(shaderName)) {
            Shader shader = shaders.get(shaderName);
            shader.bind();
            shader.uniformMat4(mat, uniform);
        } else {
            throw new RuntimeException("Shader " + shaderName + " not found.");
        }
    }

    public void uniform1i(String shaderName, int i, String uniform) {
        if (shaders.containsKey(shaderName)) {
            Shader shader = shaders.get(shaderName);
            shader.bind();
            shader.uniform1i(i, uniform);
        } else {
            throw new RuntimeException("Shader " + shaderName + " not found.");
        }
    }

    public void uniform1f(String shaderName, float f, String uniform) {
        if (shaders.containsKey(shaderName)) {
            Shader shader = shaders.get(shaderName);
            shader.bind();
            shader.uniform1f(f, uniform);
        } else {
            throw new RuntimeException("Shader " + shaderName + " not found.");
        }
    }
}
