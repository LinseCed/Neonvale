package neonvale.client.core;

import neonvale.client.core.assets.Material;
import neonvale.client.core.assets.Mesh;
import neonvale.client.core.assets.Model;
import org.joml.Matrix4f;

public class Renderable {
    public final Model model;
    public final Matrix4f transform;
    public Renderable(Model model) {
        this.model = model;
        this.transform = new Matrix4f();
    }

    public Renderable(Model model, Matrix4f transform) {
        this.model = model;
        this.transform = transform;
    }
}
