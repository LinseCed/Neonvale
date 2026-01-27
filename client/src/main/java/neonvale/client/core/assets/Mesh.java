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
    private int materialIndex;
    private boolean hasNormals;
    private boolean hasTextureCoordinates;

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices) {
        this.indicesCount = indices.limit();
        this.vao = new VAO(vertices, normals, indices);
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(normals);
        MemoryUtil.memFree(indices);
    }

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices, Texture texture, FloatBuffer texCoords) {
        this.indicesCount = indices.limit();
        this.vao = new VAO(vertices, normals, indices, texCoords);
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(normals);
        MemoryUtil.memFree(indices);
        MemoryUtil.memFree(texCoords);
    }

    public Mesh() {
        this.vao = null;
    }

    public boolean hasNormals() {
        return hasNormals;
    }

    public boolean hasTextureCoordinates() {
        return this.hasTextureCoordinates;
    }

    public void hasNormals(boolean hasNormals) {
        this.hasNormals = hasNormals;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public int getMaterialIndex() {
        return this.materialIndex;
    }

    public void hasTextureCoordinates(boolean hasTextureCoordinates) {
        this.hasTextureCoordinates = hasTextureCoordinates;
    }

    public void cleanup() {
        vao.cleanup();
    }

}
