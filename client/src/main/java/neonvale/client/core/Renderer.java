package neonvale.client.core;

import neonvale.client.core.assets.*;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.MeshGPU;
import neonvale.client.graphics.Shader;
import org.joml.Matrix3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryUtil.memAlloc;

public class Renderer {

    private final Shader shader;

    Map<Integer, MeshGPU> meshes;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public void addScene(Scene scene) {
        for (int i = 0; i < scene.materials.size(); i++) {
            MeshData md = scene.meshData.get(i);
            meshes.put(i, createGPUMesh(md));
        }
    }

    private MeshGPU createGPUMesh(MeshData md) {
        MeshGPU gpuMesh = new MeshGPU();
        gpuMesh.vao = glGenVertexArrays();
        gpuMesh.vbo = glGenBuffers();
        gpuMesh.ebo = glGenBuffers();
        glBindVertexArray(gpuMesh.vao);
        glBindBuffer(GL_ARRAY_BUFFER, gpuMesh.vbo);
        int verticesNum = md.vertices.length;
        FloatBuffer data = memAlloc(Vertex.size * verticesNum).asFloatBuffer();
        for (int i = 0; i < verticesNum; i++) {
            data.put(md.vertices[i].pos.x);
            data.put(md.vertices[i].pos.y);
            data.put(md.vertices[i].pos.z);
            data.put(md.vertices[i].normal.x);
            data.put(md.vertices[i].normal.y);
            data.put(md.vertices[i].normal.z);
            data.put(md.vertices[i].uv.x);
            data.put(md.vertices[i].uv.y);
            data.put(md.vertices[i].tangent.x);
            data.put(md.vertices[i].tangent.y);
            data.put(md.vertices[i].tangent.z);
            data.put(md.vertices[i].tangent.w);
        }
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, gpuMesh.ebo);
        glBindVertexArray(0);
        return gpuMesh;
    }

    public void draw(Camera camera) {
        shader.bind();

        shader.uniformMat4(camera.getViewMatrix(), "uView");
        shader.uniformMat4(camera.getProjectionMatrix(), "uProj");



        shader.unbind();
    }
}
