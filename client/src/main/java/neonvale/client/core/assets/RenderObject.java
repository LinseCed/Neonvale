package neonvale.client.core.assets;

import neonvale.client.core.components.TransformComponent;

public class RenderObject {
    public MeshData meshData;
    public TransformComponent transformComponent;
    public MaterialData material;

    public RenderObject(MeshData meshData, TransformComponent transformComponent, MaterialData material) {
        this.meshData = meshData;
        this.transformComponent = transformComponent;
        this.material = material;
    }
}
