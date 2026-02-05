package neonvale.client.core.components;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent implements IComponent {
    public static final int NONE_INDEX = -1;

    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;
    public Matrix4f worldTransform;
    public int parentID = NONE_INDEX;
    public int firstChildID = NONE_INDEX;
    public int nextSiblingID = NONE_INDEX;

    public TransformComponent(Vector3f position, Quaternionf rotation, Vector3f scale, Matrix4f worldTransform, int parentID, int firstChildID, int nextSiblingID) {
    }

    public Matrix4f getWorldTransform() {
        return this.worldTransform;
    }
}