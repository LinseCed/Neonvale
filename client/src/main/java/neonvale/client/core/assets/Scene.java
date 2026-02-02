package neonvale.client.core.assets;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    public Matrix4f sceneWorldTransform = new Matrix4f().identity();

    public List<MeshData> meshData;
    public List<TransformComponent> transforms;
    public List<PointLightComponent> pointLights;

    public List<RenderObject> renderObjects;
    public List<PointLightObject> pointLightObjects;
    public List<Material> materials;

    public Scene() {
        this.meshData = new ArrayList<>();
        this.transforms = new ArrayList<>();
        this.pointLights = new ArrayList<>();
        this.renderObjects = new ArrayList<>();
        this.pointLightObjects = new ArrayList<>();
    }

    public void transform(Matrix4f t) {
        this.sceneWorldTransform.mul(t);
    }

    public void setTransform(Matrix4f t) {
        this.sceneWorldTransform = t;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model: \n");
        sb.append("Meshcount: ").append(meshData.size()).append("\n");
        return sb.toString();
    }
}
