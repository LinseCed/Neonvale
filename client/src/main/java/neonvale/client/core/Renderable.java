package neonvale.client.core;

import neonvale.client.core.assets.Scene;
import org.joml.Matrix4f;

import java.util.List;

public class Renderable {
    public final Scene scene;
    public final Matrix4f transform;
    private List<RenderCommand> renderCommands;

    public Renderable(Scene scene, Matrix4f transform) {
        this.scene = scene;
        this.transform = transform;
    }
}
