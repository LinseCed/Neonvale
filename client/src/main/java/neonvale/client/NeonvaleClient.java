package neonvale.client;

import neonvale.client.core.*;
import neonvale.client.core.components.PointLightComponent;
import neonvale.client.core.components.TransformComponent;
import neonvale.client.core.assets.ModelLoader;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Shader;
import neonvale.client.graphics.Window;
import neonvale.client.input.KeyCallback;
import neonvale.client.resources.ShaderManager;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class NeonvaleClient {

    private Window window;
    private GameLoop gameLoop;
    private Camera camera;
    private Renderer renderer;
    private KeyCallback keyCallback;
    private World world;
    private Shader shader;

    public static void main(String[] args) {
        new NeonvaleClient().run();
    }

    private void init() {
        this.window = new Window();
        this.camera = Camera.getInstance();
        ShaderManager.getInstance();
        this.gameLoop = new GameLoop();
        this.keyCallback = KeyCallback.getInstance();

        this.world = new World();
        ModelLoader.load("../assets/MetalRoughSpheres.glb", world);
        ModelLoader.load("../assets/Scene.gltf", world, new Matrix4f().translate(1, 1, 2).scale(0.01f));

        Entity lightEntity = new Entity();
        lightEntity.addComponent(new TransformComponent(new Vector3f(1, 1, 2), new Quaternionf(), new Vector3f(1)));
        lightEntity.addComponent(new PointLightComponent(new Vector3f(10, 10, 10)));
        world.addEntity(lightEntity);

        shader = new Shader("../shaders/pbrshader.vert", "../shaders/pbrshader.frag");
        renderer = new Renderer(shader);

        if (Config.enableWireframe) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    public void run() {
        init();
        gameLoop.run(this::update, this::render);
        cleanup();
    }

    private void update(float delta) {
        if (window.shouldClose()) {
            gameLoop.stop();
        }
        keyCallback.pollInputs(window.getWindow(), delta);
    }

    private void render(float delta) {
        window.clear();
        renderer.draw(world);
        window.update();
    }

    private void cleanup() {
        window.cleanup();
    }
}
