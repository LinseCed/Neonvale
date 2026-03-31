package neonvale.client.core;

import neonvale.client.core.assets.MaterialData;
import neonvale.client.core.assets.MeshData;
import neonvale.client.core.assets.Vertex;
import neonvale.client.core.components.MeshRendererComponent;
import neonvale.client.core.components.PointLightComponent;
import neonvale.client.core.components.TransformComponent;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.MeshGPU;
import neonvale.client.graphics.Shader;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {

    private final Shader shader;
    private final Map<MeshData, MeshGPU> meshCache = new HashMap<>();

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public void draw(World world) {
        beginFrame();

        List<Entity> lights = world.query(TransformComponent.class, PointLightComponent.class);
        int lightCount = Math.min(lights.size(), 8);
        for (int i = 0; i < lightCount; i++) {
            Entity light = lights.get(i);
            Vector3f pos = light.getComponent(TransformComponent.class).getWorldTransform().getTranslation(new Vector3f());
            Vector3f radiance = light.getComponent(PointLightComponent.class).radiance;
            shader.uniform3f(pos, "uLightPositions[" + i + "]");
            shader.uniform3f(radiance, "uLightRadiances[" + i + "]");
        }
        shader.uniform1i(lightCount, "uLightCount");

        for (Entity entity : world.query(TransformComponent.class, MeshRendererComponent.class)) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            MeshRendererComponent meshRenderer = entity.getComponent(MeshRendererComponent.class);

            Matrix4f modelMatrix = transform.getWorldTransform();
            Matrix3f normalMatrix = new Matrix3f();
            modelMatrix.normal(normalMatrix);
            shader.uniformMat4(modelMatrix, "uModel");
            shader.uniformMat3(normalMatrix, "uNormalMatrix");

            for (MeshRendererComponent.MeshEntry entry : meshRenderer.meshes) {
                MeshGPU gpu = getOrCreateGPUMesh(entry.meshData());
                MaterialData material = entry.material();

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, material.albedoTex);
                shader.uniform1i(0, "albedoMap");

                glActiveTexture(GL_TEXTURE1);
                glBindTexture(GL_TEXTURE_2D, material.normalMap);
                shader.uniform1i(1, "normalMap");

                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, material.metallicRoughnessMap);
                shader.uniform1i(2, "metallicRoughnessMap");

                shader.uniform4f(material.baseColorFactor, "uBaseColorFactor");
                shader.uniform1f(material.metallicFactor, "uMetallicFactor");
                shader.uniform1f(material.roughness, "uRoughness");

                glBindVertexArray(gpu.vao);
                glDrawElements(GL_TRIANGLES, gpu.indexCount, GL_UNSIGNED_INT, 0);
            }
        }

        endFrame();
    }

    private void beginFrame() {
        shader.bind();
        Camera camera = Camera.getInstance();
        shader.uniformMat4(camera.getViewMatrix(), "uView");
        shader.uniformMat4(camera.getProjectionMatrix(), "uProj");
        shader.uniform3f(camera.getPosition(), "camPos");
    }

    private void endFrame() {
        glBindVertexArray(0);
        shader.unbind();
    }

    private MeshGPU getOrCreateGPUMesh(MeshData md) {
        return meshCache.computeIfAbsent(md, this::createGPUMesh);
    }

    private MeshGPU createGPUMesh(MeshData md) {
        MeshGPU gpuMesh = new MeshGPU();
        gpuMesh.indexCount = md.indices.length;
        gpuMesh.vao = glGenVertexArrays();
        gpuMesh.vbo = glGenBuffers();
        gpuMesh.ebo = glGenBuffers();

        glBindVertexArray(gpuMesh.vao);
        glBindBuffer(GL_ARRAY_BUFFER, gpuMesh.vbo);

        int verticesNum = md.vertices.length;
        FloatBuffer data = memAlloc(Float.BYTES * Vertex.size * verticesNum).asFloatBuffer();
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
        data.flip();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        memFree(data);

        int stride = 12 * Float.BYTES;
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, stride, 8 * Float.BYTES);
        glEnableVertexAttribArray(3);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, gpuMesh.ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, md.indices, GL_STATIC_DRAW);
        glBindVertexArray(0);
        return gpuMesh;
    }
}
