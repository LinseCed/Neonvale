package neonvale.client.core;

import neonvale.client.core.assets.Material;
import neonvale.client.core.assets.Mesh;
import neonvale.client.core.assets.SubMesh;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Shader;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Renderer {

    private final Shader shader;

    List<RenderCommand> renderQueue;

    public Renderer(Shader shader) {
        this.shader = shader;
        this.renderQueue = new ArrayList<>();
    }

    public void draw(Camera camera) {
        shader.bind();

        shader.uniformMat4(camera.getViewMatrix(), "uView");
        shader.uniformMat4(camera.getProjectionMatrix(), "uProj");

        Mesh currentMesh = null;
        Material currentMaterial = null;

        for (RenderCommand c : renderQueue) {
            if (!c.material.equals(currentMaterial)) {
                bindMaterial(c.material);
                currentMaterial = c.material;
            }

            if (!c.mesh.equals(currentMesh)) {
                bindMesh(c.mesh);
                currentMesh = c.mesh;
            };

            shader.uniformMat4(c.transform, "uModel");
            Matrix3f normalMatrix = new Matrix3f();
            c.transform.normal(normalMatrix);
            shader.uniformMat3(normalMatrix, "uNormalMatrix");

            glDrawElements(GL_TRIANGLES, currentMesh.getIndexCount(), GL_UNSIGNED_INT, 0);
        }

        shader.unbind();
    }

    private void bindMaterial(Material material) {
        shader.bind();

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
    }

    private void bindMesh(Mesh mesh) {
        mesh.bind();
        shader.uniform1b(mesh.hasNormals(), "uHasNormals");
        shader.uniform1b(mesh.hasTangents(), "uHasTangents");
        shader.uniform1b(mesh.hasUVs(), "uHasUVs");
    }

    public void addToRenderQueue(RenderCommand command) {
        renderQueue.add(command);
    }

    public void sortRenderCommands() {

    }
}
