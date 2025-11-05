package neonvale.client.input;

import neonvale.client.graphics.Camera;
import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyCallback extends GLFWKeyCallback {

    private static KeyCallback instance;
    private Camera camera;

    private KeyCallback(){
        this.camera = Camera.getInstance();
    }

    public static KeyCallback getInstance(){
        if (instance == null) {
            instance = new KeyCallback();
        }
        return instance;
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public void pollInputs(long window) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.moveForward();
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.moveLeft();
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.moveBackward();
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.moveRight();
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.moveUp();
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
            camera.moveDown();
        }
    }
}
