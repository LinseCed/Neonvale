package neonvale.client.core;

import neonvale.client.core.assets.Material;
import neonvale.client.core.assets.Mesh;
import neonvale.client.core.assets.Model;
import org.joml.Matrix4f;

import java.util.HashMap;

public class RenderCommand {
    public Mesh mesh;
    public Material material;
    public Matrix4f transform;

    public RenderCommand(Mesh mesh, Material material, Matrix4f transform) {
        this.mesh = mesh;
        this.material = material;
        this.transform = transform;
    }

}
