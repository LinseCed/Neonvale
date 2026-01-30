package neonvale.client.core.assets;

public class RenderObject {
    public MeshData meshData;
    public TransformComponent transformComponent;
    public Material material;

    public RenderObject(MeshData meshData, TransformComponent transformComponent, Material material) {
        this.meshData = meshData;
        this.transformComponent = transformComponent;
        this.material = material;
    }
}
