package neonvale.client.core.assets;

import neonvale.client.graphics.Shader;
import neonvale.client.graphics.VAO;

public class Mesh {

    private VAO vao;
    private int indexCount;
    private int materialIndex;

    private boolean hasNormals;
    private boolean hasUVs;
    private boolean hasTangents;

    public Mesh(VAO vao, int indexCount, int materialIndex, boolean hasNormals, boolean hasUVs, boolean hasTangents) {
        this.vao = vao;
        this.indexCount = indexCount;
        this.hasNormals = hasNormals;
        this.hasUVs = hasUVs;
        this.hasTangents = hasTangents;
    }

    public void bind() {
        vao.bind();
    }

    public boolean hasNormals() {
        return hasNormals;
    }

    public boolean hasUVs() {
        return this.hasUVs;
    }

    public boolean hasTangents() {
        return this.hasTangents;
    }

    public void hasNormals(boolean hasNormals) {
        this.hasNormals = hasNormals;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public void hasTangents(boolean hasTangents) {
        this.hasTangents = hasTangents;
    }

    public int getMaterialIndex() {
        return this.materialIndex;
    }

    public void hasUVs(boolean hasTextureCoordinates) {
        this.hasUVs = hasTextureCoordinates;
    }

    public void cleanup() {
        vao.cleanup();
    }

}
