package neonvale.client.input;

import neonvale.client.core.Config;
import neonvale.client.graphics.Camera;
import org.lwjgl.glfw.GLFWCursorPosCallback;

public class MouseCallback extends GLFWCursorPosCallback {

    private static MouseCallback instance;
    private Camera camera;

    private double lastX, lastY;
    private boolean firstMouse = true;

    private MouseCallback() {
        this.camera = Camera.getInstance();
    }

    public static MouseCallback getInstance() {
        if (instance == null) {
            instance = new MouseCallback();
        }
        return instance;
    }

    @Override
    public void invoke(long window, double xPos, double yPos) {
        if (firstMouse) {
            lastX = xPos;
            lastY = yPos;
            firstMouse = false;
        }
        double offsetX = xPos - lastX;
        double offsetY = lastY - yPos;

        lastX = xPos;
        lastY = yPos;

        camera.addYaw((float) offsetX * Config.mouseSensitivity);
        camera.addPitch((float) offsetY * -Config.mouseSensitivity);
    }

}
