package neonvale.client;

import neonvale.client.core.Config;
import neonvale.client.core.GameLoop;
import neonvale.client.core.assets.Model;
import neonvale.client.core.assets.ModelLoader;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Window;
import neonvale.client.input.KeyCallback;
import neonvale.client.resources.ShaderManager;
import org.joml.Matrix4f;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;


public class NeonvaleClient {
    Logger logger = Logger.getLogger(NeonvaleClient.class.getName());
    private float time = 0f;

    private Window window;
    private GameLoop gameLoop;
    private ShaderManager shaderManager;
    private Camera camera;
    Model shape;
    Model shape2;
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
        if (Config.enableWireframe) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
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

    private void render(float delta) {
        this.window.clear();
        this.window.update();
    }

    private void cleanup() {
        this.window.cleanup();
    }
}
