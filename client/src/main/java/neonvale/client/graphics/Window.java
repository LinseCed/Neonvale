package neonvale.client.graphics;

import neonvale.client.core.Config;
import neonvale.client.input.KeyCallback;
import neonvale.client.input.MouseCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private final long window;

    public Window() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(Config.winWidth, Config.winHeight, "Neonvale", NULL, NULL);

        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, KeyCallback.getInstance());
        glfwSetCursorPosCallback(window, MouseCallback.getInstance());

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);
        GL.createCapabilities();

        glfwShowWindow(window);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void update() {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void cleanup() {
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public long getWindow() {
        return window;
    }
}
