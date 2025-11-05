package neonvale.client;

import neonvale.client.core.GameLoop;
import neonvale.client.core.assets.Model;
import neonvale.client.core.assets.ModelLoader;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Window;
import neonvale.client.input.KeyCallback;
import neonvale.client.resources.ShaderManager;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class NeonvaleClient {

    private Window window;
    private GameLoop gameLoop;
    private ShaderManager shaderManager;
    private Camera camera;
    private boolean initialized = false;
    private int vao, vbo;

    public static void main(String[] args) {
        NeonvaleClient neonvale = new NeonvaleClient();
        neonvale.run();
    }

    private void init() {
        this.window = new Window();
        this.camera = Camera.getInstance();
        this.shaderManager = ShaderManager.getInstance();
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
        if (!initialized) {
            // --- Setup a simple triangle ---
            float[] vertices = {
                    // positions       // colors
                    0.0f,  0.5f, 0f,  1f, 0f, 0f, // top vertex (red)
                    -0.5f, -0.5f, 0f,  0f, 1f, 0f, // bottom left (green)
                    0.5f, -0.5f, 0f,  0f, 0f, 1f  // bottom right (blue)
            };

            vao = glGenVertexArrays();
            vbo = glGenBuffers();

            glBindVertexArray(vao);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            // position attribute
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
            glEnableVertexAttribArray(0);

            // color attribute
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
            glEnableVertexAttribArray(1);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            initialized = true;
        }
        this.window.clear();
        this.shaderManager.use("Basic");
        this.shaderManager.uniformMat4("Basic", camera.getViewMatrix(), "uView");
        this.shaderManager.uniformMat4("Basic", camera.getProjectionMatrix(), "uProj");
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0);
        this.window.update();
    }

    private void cleanup() {
        this.window.cleanup();
    }
}
