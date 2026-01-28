package neonvale.client.graphics;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int id;
    private final Map<String, Integer> uniformLocations = new HashMap<>();

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
        int status = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(vertexShader));
        }
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        status = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (status == GL_FALSE) {
            System.err.println(glGetShaderInfoLog(fragmentShader));
        }

        this.id = glCreateProgram();
        glAttachShader(this.id, vertexShader);
        glAttachShader(this.id, fragmentShader);
        glLinkProgram(this.id);
        status = glGetProgrami(this.id, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            System.err.println(glGetProgramInfoLog(this.id));
        }
        glValidateProgram(this.id);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void bind() {
        glUseProgram(this.id);
    }

    public void unbind() {
        glUseProgram(0);
    }

    private int getUniformLocation(String name) {
        if (uniformLocations.containsKey(name))
            return uniformLocations.get(name);

        int location = glGetUniformLocation(this.id, name);
        if (location == -1)
            System.err.println("Warning: uniform '" + name + "' not found.");
        uniformLocations.put(name, location);
        return location;
    }

    public void uniformMat4(Matrix4f mat, String name) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location, false, mat.get(stack.mallocFloat(16)));
        } catch (Exception e) {
            System.err.println("Exception while setting uniform mat4 " + name);
        }
    }

    public void uniformMat3(Matrix3f mat, String name) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix3fv(location, false, mat.get(stack.mallocFloat(9)));
        } catch (Exception e) {
            System.err.println("Exception while setting uniform mat4 " + name);
        }
    }

    public void uniform1i(int i, String name) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform1i(location, i);
        } catch (Exception e) {
            System.err.println("Exception while setting uniform " + name);
        }
    }

    public void uniform1f(float f, String name) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform1f(location, f);
        } catch (Exception e) {
            System.err.println("Exception while setting uniform " + name);
        }
    }

    public void uniform4f(Vector4f f, String name) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform4f(location, f.x, f.y, f.z, f.w);
        } catch (Exception e) {
            System.err.println("Exception while setting uniform " + name);
        }
    }

    public void uniform3f(Vector3f f, String name) {
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform3f(location, f.x, f.y, f.z);
        } catch (Exception e) {
            System.err.println("Exception while setting uniform " + name);
        }
    }

    public void uniform1b(boolean b, String name) {
        int i = b ? 1 : 0;
        int location = getUniformLocation(name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform1i(location, i);
        } catch (Exception e) {
            System.err.println("Exception while setting uniform " + name);
        }
    }}
