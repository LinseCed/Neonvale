package neonvale.client.core;

import neonvale.client.core.assets.Material;
import neonvale.client.core.assets.Mesh;
import neonvale.client.core.assets.SubMesh;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Shader;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Shader shader;

    public Renderer(Shader shader) {
        this.shader = shader;
    }

    public void draw(Renderable renderable, Camera camera) {
        shader.bind();

        shader.uniformMat4(camera.getViewMatrix(), "uView");
        shader.uniformMat4(camera.getProjectionMatrix(), "uProj");

        for (SubMesh sm : renderable.model.getSubMeshes()) {
            Mesh mesh = renderable.model.getMeshes().get(sm.meshIndex);
            Material material = renderable.model.getMaterials().get(sm.materialIndex);

            material.applyToShader(shader);

            Matrix4f submeshTransform = new Matrix4f(renderable.transform).mul(sm.localTransform);

            shader.uniformMat4(submeshTransform, "uModel");
            Matrix3f normalMatirx = new Matrix3f();
            submeshTransform.normal(normalMatirx);
            shader.uniformMat3(normalMatirx, "uNormalMatrix");
            mesh.bind();
            shader.uniform1b(mesh.hasNormals(), "uHasNormals");
            shader.uniform1b(mesh.hasTangents(), "uHasTangents");
            shader.uniform1b(mesh.hasUVs(), "uHasUVs");
            glDrawElements(GL_TRIANGLES, mesh.getIndexCount(), GL_UNSIGNED_INT, 0);
        }
        shader.unbind();
    }
}
