package neonvale.client.core.assets;

public class MeshData {
    public Vertex[] vertices;
    public int[] indices;

    public MeshData(Vertex[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }
}
