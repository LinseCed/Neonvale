package neonvale.client;

import neonvale.client.core.GameLoop;
import neonvale.client.core.assets.Model;
import neonvale.client.core.assets.ModelLoader;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Window;
import neonvale.client.input.KeyCallback;
import neonvale.client.resources.ShaderManager;
import org.joml.Matrix4f;


public class NeonvaleClient {
    private float time = 0f;

    private Window window;
    private GameLoop gameLoop;
    private ShaderManager shaderManager;
    private Camera camera;
    Model shape;
    KeyCallback keyCallback;

    public static void main(String[] args) {
        NeonvaleClient neonvale = new NeonvaleClient();
        neonvale.run();
    }

    private void init() {
        this.window = new Window();
        this.camera = Camera.getInstance();
        this.shaderManager = ShaderManager.getInstance();
        this.gameLoop = new GameLoop();
        keyCallback = KeyCallback.getInstance();
        shape = ModelLoader.load("assets/shape.glb");
    }

    public void run() {
        init();
        this.gameLoop.run(this::update, this::render);
        cleanup();
    }
    private float accumulator = 0f;
    private int frames = 0;
    private void update(float delta) {
        if (this.window.shouldClose()) {
            this.gameLoop.stop();
        }

        keyCallback.pollInputs(window.getWindow(), delta);
    }

    private Matrix4f model = new Matrix4f();
    private void render(float delta) {
        this.window.clear();
        this.shaderManager.use("Basic");
        this.shaderManager.uniformMat4("Basic", camera.getViewMatrix(), "uView");
        this.shaderManager.uniformMat4("Basic", camera.getProjectionMatrix(), "uProj");
        this.shaderManager.uniformMat4("Basic", model, "uModel");
        model.rotate(delta, 1, 1, 1);
        shape.draw();
        this.window.update();
    }

    private void cleanup() {
        this.window.cleanup();
    }
}
