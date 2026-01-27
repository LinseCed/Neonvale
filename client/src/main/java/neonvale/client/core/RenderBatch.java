package neonvale.client.core;

import neonvale.client.core.assets.Material;
import neonvale.client.core.assets.Mesh;
import org.joml.Matrix4f;

import java.util.List;

public class RenderBatch {
    private Mesh mesh;
    private Material material;
    private List<Matrix4f> transforms;

    public RenderBatch(Mesh mesh, Material material, List<Matrix4f> transforms) {
        this.mesh = mesh;
        this.material = material;
        this.transforms = transforms;
    }
}
