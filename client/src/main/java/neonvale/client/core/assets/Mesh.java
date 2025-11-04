package neonvale.client.core.assets;

import neonvale.client.graphics.VAO;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Mesh {

    private final FloatBuffer vertices;
    private final FloatBuffer normals;
    private final IntBuffer indices;
    private final VAO vao;

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.vao = new VAO(vertices, normals, indices);
    }

    public void draw() {
        vao.bind();
        glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        vao.unbind();
    }

}
