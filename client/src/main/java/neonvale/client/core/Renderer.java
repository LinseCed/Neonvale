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

    public void draw(Renderable renderable, Camera camera) {
        shader.bind();

        shader.uniformMat4(camera.getViewMatrix(), "uView");
        shader.uniformMat4(camera.getProjectionMatrix(), "uProj");

        for (SubMesh sm : renderable.model.getSubMeshes()) {
            Mesh mesh = renderable.model.getMeshes().get(sm.meshIndex);
            Material material = renderable.model.getMaterials().get(sm.materialIndex);

            bindMaterial(material);
            Matrix4f submeshTransform = new Matrix4f(renderable.transform).mul(sm.localTransform);

            shader.uniformMat4(submeshTransform, "uModel");
            Matrix3f normalMatrix = new Matrix3f();
            submeshTransform.normal(normalMatrix);
            shader.uniformMat3(normalMatrix, "uNormalMatrix");
            mesh.bind();
            shader.uniform1b(mesh.hasNormals(), "uHasNormals");
            shader.uniform1b(mesh.hasTangents(), "uHasTangents");
            shader.uniform1b(mesh.hasUVs(), "uHasUVs");
            glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
        }

        Mesh currentMesh = null;
        Material currentMaterial = null;

        for (RenderCommand c : renderQueue) {
            if (!c.material.equals(currentMaterial)) {
                bindMaterial(c.material);
                currentMaterial = c.material;
            }

            if (!c.mesh.equals(currentMesh)) {
                currentMesh = c.mesh;
            }
        }

        shader.unbind();
    }

    private void bindMaterial(Material material) {
        shader.bind();

        shader.uniform1b(material.hasAlbedoTexture, "uHasAlbedoTexture");
        if (material.hasAlbedoTexture) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.albedoTex);
            shader.uniform1i(0, "albedoMap");
        } else {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        shader.uniform1b(material.hasNormalMap, "uHasNormalMap");
        if (material.hasNormalMap) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.normalMap);
            shader.uniform1i(1, "normalMap");
        } else {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, 0);
        }

        shader.uniform1b(material.hasMetallicRoughnessTexture, "uHasMetallicRoughnessTexture");
        if (material.hasMetallicRoughnessTexture) {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, material.metallicRoughnessMap);
            shader.uniform1i(2, "metallicRoughnessMap");
        } else {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 2);
        }

        shader.uniform4f(material.baseColorFactor, "uBaseColorFactor");
        shader.uniform1f(material.metallicFactor, "uMetallicFactor");
        shader.uniform1f(material.roughness, "uRoughness");
    }

    public void addToRenderQueue(RenderCommand command) {
        renderQueue.add(command);
    }

    public void sortRenderCommands() {

    }
}
