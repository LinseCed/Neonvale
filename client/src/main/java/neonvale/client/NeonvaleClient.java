package neonvale.client;

import neonvale.client.core.*;
import neonvale.client.core.assets.ModelLoader;
import neonvale.client.core.components.PointLightComponent;
import neonvale.client.core.components.RemotePlayerComponent;
import neonvale.client.core.components.TransformComponent;
import neonvale.client.graphics.Camera;
import neonvale.client.graphics.Window;
import neonvale.client.input.KeyCallback;
import neonvale.client.net.NetworkClient;
import neonvale.client.resources.ShaderManager;
import neonvale.shared.net.StatePacket;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class NeonvaleClient {

    private Window window;
    private GameLoop gameLoop;
    private Camera camera;
    private Renderer renderer;
    private KeyCallback keyCallback;
    private World world;
    private NetworkClient networkClient;
    private final Map<String, Entity> remotePlayers = new HashMap<>();

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

        renderer = new Renderer(ShaderManager.getInstance().get("PBR"));

        networkClient = new NetworkClient();
        networkClient.connect("localhost", 7777);

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

        Vector3f pos = camera.getPosition();
        networkClient.sendMove(pos.x, pos.y, pos.z);

        StatePacket state = networkClient.pollState();
        if (state != null) {
            applyWorldState(state);
        }
    }

    private void applyWorldState(StatePacket state) {
        String localId = networkClient.getLocalPlayerId();
        for (StatePacket.PlayerEntry entry : state.players) {
            if (entry.id.equals(localId)) continue;

            Entity entity = remotePlayers.get(entry.id);
            if (entity == null) {
                entity = new Entity();
                entity.addComponent(new TransformComponent(
                    new Vector3f(entry.x, entry.y, entry.z), new Quaternionf(), new Vector3f(1)));
                entity.addComponent(new RemotePlayerComponent(entry.id));
                world.addEntity(entity);
                remotePlayers.put(entry.id, entity);
            } else {
                entity.getComponent(TransformComponent.class).setPosition(
                    new Vector3f(entry.x, entry.y, entry.z));
            }
        }

        // Remove players that are no longer in the state
        List<String> currentIds = state.players.stream().map(e -> e.id).toList();
        remotePlayers.entrySet().removeIf(entry -> {
            if (!currentIds.contains(entry.getKey())) {
                world.removeEntity(entry.getValue());
                return true;
            }
            return false;
        });
    }

    private void render(float delta) {
        window.clear();
        renderer.draw(world);
        window.update();
    }

    private void cleanup() {
        networkClient.disconnect();
        window.cleanup();
    }
}
