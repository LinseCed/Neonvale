package neonvale.client;

import neonvale.client.core.GameLoop;
import neonvale.client.core.assets.Model;
import neonvale.client.core.assets.ModelLoader;
import neonvale.client.graphics.Window;
import neonvale.client.resources.ShaderManager;

public class NeonvaleClient {

    private Window window;
    private GameLoop gameLoop;
    private ShaderManager shaderManager;

    public static void main(String[] args) {
        NeonvaleClient neonvale = new NeonvaleClient();
        neonvale.run();
    }

    private void init() {
        this.window = new Window();
        this.shaderManager = ShaderManager.getInstance();
        this.shaderManager.use("Basic");
        this.gameLoop = new GameLoop();
        Model shape = ModelLoader.load("assets/shape.glb");
    }

    public void run() {
        init();
        this.gameLoop.run(this::update, this::render);
        cleanup();
    }

    private void update(float delta) {
        if (this.window.shouldClose()) {
            this.gameLoop.stop();
        }
    }

    private void render() {
        this.window.clear();
        this.window.update();
    }

    private void cleanup() {
        this.window.cleanup();
    }
}
