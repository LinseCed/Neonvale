package neonvale.client.resources;


import neonvale.client.graphics.Shader;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static ShaderManager instance = new ShaderManager();
    private final Map<String, Shader> shaders;

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
        shaders.put("Basic", new Shader("basic.vert", "basic.frag"));
        shaders.put("BasicTexture", new Shader("basicTexture.vert", "basicTexture.frag"));
        shaders.put("PBR", new Shader("pbrshader.vert", "pbrshader.frag"));
    }

    public Shader get(String shaderName) {
        Shader shader = shaders.get(shaderName);
        if (shader == null) {
            throw new RuntimeException("Shader " + shaderName + " not found.");
        }
        return shader;
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
