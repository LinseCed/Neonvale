package neonvale.client.core;

import neonvale.client.core.assets.Model;
import org.joml.Matrix4f;

import java.util.List;

public class Renderable {
    public final Model model;
    public final Matrix4f transform;
    private List<RenderCommand> renderCommands;

    public Renderable(Model model, Matrix4f transform) {
        this.model = model;
        this.transform = transform;
    }
}
