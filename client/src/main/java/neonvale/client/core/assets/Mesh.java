package neonvale.client.core.assets;

import neonvale.client.graphics.VAO;
import neonvale.client.resources.ShaderManager;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Mesh {

    private int indicesCount;
    private final VAO vao;
    private final Texture texture;


    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices) {
        this.indicesCount = indices.limit();
        this.vao = new VAO(vertices, normals, indices);
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(normals);
        MemoryUtil.memFree(indices);
        texture = null;
    }

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices, Texture texture, FloatBuffer texCoords) {
        this.indicesCount = indices.limit();
        this.vao = new VAO(vertices, normals, indices, texCoords);
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(normals);
        MemoryUtil.memFree(indices);
        MemoryUtil.memFree(texCoords);
        this.texture = texture;
    }

    public boolean meshHasTexture() {
        return this.texture != null;
    }

    public void cleanup() {
        vao.cleanup();
    }

}
