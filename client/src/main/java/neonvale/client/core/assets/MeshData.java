package neonvale.client.core.assets;

public class MeshData {
    Vertex[] vertices;
    int[] indices;

    public MeshData(Vertex[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }
}
