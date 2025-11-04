package neonvale.client.input;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;

public class KeyCallback extends GLFWKeyCallback {

    private static KeyCallback instance;

    private KeyCallback(){
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
}
