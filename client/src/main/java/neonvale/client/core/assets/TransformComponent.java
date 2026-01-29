package neonvale.client.core.assets;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent {
    public static final int NONE_INDEX = -1;

    public Vector3f position;
    public Quaternionf rotation;
    public Vector3f scale;
    public Matrix4f worldtransform;
    int parentID = NONE_INDEX;
    int firstChildID = NONE_INDEX;
    int nextSiblingID = NONE_INDEX;

    public TransformComponent(Vector3f position, Quaternionf rotation, Vector3f scale, Matrix4f worldtransform, int parentID, int firstChildID, int nextSiblingID) {}
}