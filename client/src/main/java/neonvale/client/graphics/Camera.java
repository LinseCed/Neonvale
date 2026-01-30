package neonvale.client.graphics;

import neonvale.client.core.Config;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private static Camera instance;
    private Vector3f position;
    private float pitch;
    private float yaw;
    private float fov = (float) Math.toRadians(Config.fov);
    private float near = Config.near;
    private float far = Config.far;
    private float aspect = Config.winAspect;

    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f();

    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera(new Vector3f(0, 1.7f, 5.0f));
        }
        return instance;
    }

    public static Camera getInstance(Vector3f position) {
        if (instance == null) {
            instance = new Camera(position);
        }
        return instance;
    }

    private Camera(Vector3f position) {
        this.position = position;
        this.pitch = 0;
        this.yaw = 0;
        updateProjection();
    }

    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f();
        view.identity();
        view.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0)).rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        Vector3f negativePos = new Vector3f(position).mul(-1);
        view.translate(negativePos);
        return view;
    }

    public Matrix4f getProjectionMatrix() {
        return projection;
    }

    public void moveForwardDistance(float distance) {
        position.x += distance * (float) Math.sin(Math.toRadians(yaw));
        position.z -= distance * (float) Math.cos(Math.toRadians(yaw));
    }

    public void moveForward(float delta) {
        moveForwardDistance(Config.movementSpeed * delta);
    }

    public void moveBackward(float delta) {
        moveForwardDistance(-Config.movementSpeed * delta);
    }

    public void moveRightDistance(float distance) {
        position.x += distance * (float) Math.sin(Math.toRadians(yaw + 90));
        position.z -= distance * (float) Math.cos(Math.toRadians(yaw + 90));
    }

    public void moveRight(float delta) {
        moveRightDistance(Config.movementSpeed * delta);
    }

    public void moveLeft(float delta) {
        moveRightDistance(-Config.movementSpeed * delta);
    }

    public void moveUpDistance(float distance) {
        position.y += distance;
    }

    public void moveUp(float delta) {
        moveUpDistance(Config.movementSpeed * delta);
    }

    public void moveDown(float delta) {
        moveUpDistance(-Config.movementSpeed * delta);
    }

    public void addPitch(float pitch) {
        this.pitch += pitch;
        this.pitch = Math.max(-89, Math.min(89, this.pitch));
    }

    public void addYaw(float yaw) {
        this.yaw += yaw;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void updateProjection() {
        projection.identity().perspective(fov, aspect, near, far);
    }

    public void setAspect(float width, float height) {
        this.aspect = width / height;
        updateProjection();
    }

    public void setFov(float fov) {
        this.fov = (float) Math.toRadians(fov);
        updateProjection();
    }
}
