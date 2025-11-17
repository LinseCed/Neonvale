package neonvale.client.core.assets;

import neonvale.client.graphics.VAO;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Mesh {

    private final FloatBuffer vertices;
    private final FloatBuffer normals;
    private final IntBuffer indices;
    private final VAO vao;
    private final Texture texture;
    private final FloatBuffer texCoords;

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.texCoords = null;
        this.vao = new VAO(vertices, normals, indices);
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(normals);
        MemoryUtil.memFree(indices);
        texture = null;
    }

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices, Texture texture, FloatBuffer texCoords) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
        this.texCoords = texCoords;
        this.vao = new VAO(vertices, normals, indices, texCoords);
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(normals);
        MemoryUtil.memFree(indices);
        MemoryUtil.memFree(texCoords);
        this.texture = texture;
    }

    public void draw() {
        vao.bind();
        glActiveTexture(GL_TEXTURE0);
        if (texture != null) {
            glBindTexture(GL_TEXTURE_2D, texture.getId());
        }
        glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        vao.unbind();
    }

    public void cleanup() {
        vao.cleanup();
    }

}
