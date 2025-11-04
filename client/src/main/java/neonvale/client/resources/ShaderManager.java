package neonvale.client.resources;


import neonvale.client.graphics.Shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    private static ShaderManager instance = new ShaderManager();
    private final Map<String, Shader> shaders;

    String basicVertexShader = "basic.vert";
    String basicFragmentShader = "basic.frag";

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
    }

    public void use(String shaderName) {
        if (shaders.containsKey(shaderName)) {
            shaders.get(shaderName).use();
        } else {
            throw new RuntimeException("Shader " + shaderName + " not found.");
        }
    }
}
