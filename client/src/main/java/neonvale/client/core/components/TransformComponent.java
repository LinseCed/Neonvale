package neonvale.client.core.components;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent implements IComponent {

    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;
    public Matrix4f worldTransform;

    public TransformComponent(Vector3f position, Quaternionf rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.worldTransform = new Matrix4f().translate(position).rotate(rotation).scale(scale);
    }

    public TransformComponent(Vector3f position, Quaternionf rotation, Vector3f scale, Matrix4f worldTransform) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.worldTransform = worldTransform;
    }

    public Matrix4f getWorldTransform() {
        return this.worldTransform;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
        this.worldTransform = new Matrix4f().translate(position).rotate(rotation).scale(scale);
    }
}
