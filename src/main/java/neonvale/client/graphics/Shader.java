package neonvale.client.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int id;

    public Shader(String vertexShaderPath, String fragmentShaderPath) {
        String vertexShaderSource = "";
        String fragmentShaderSource = "";
        ClassLoader classLoader = Shader.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("shaders/" + vertexShaderPath)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + vertexShaderPath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                vertexShaderSource = reader.lines().collect(Collectors.joining("\n"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (InputStream inputStream = classLoader.getResourceAsStream("shaders/" + fragmentShaderPath)) {
            if (inputStream == null) {
                throw new RuntimeException("File not found: " + fragmentShaderPath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                fragmentShaderSource = reader.lines().collect(Collectors.joining("\n"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        this.id = glCreateProgram();
        glAttachShader(this.id, vertexShader);
        glAttachShader(this.id, fragmentShader);
        glLinkProgram(this.id);

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void use() {
        glUseProgram(this.id);
    }
}
