package neonvale.client.core.assets;

import neonvale.client.core.Entity;
import neonvale.client.core.World;
import neonvale.client.core.components.MeshRendererComponent;
import neonvale.client.core.components.TransformComponent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class SceneAsset {

    public Matrix4f sceneWorldTransform = new Matrix4f().identity();

    public List<MeshData> meshData;
    public List<TransformComponent> transforms;
    public List<PointLightComponent> pointLights;

    public List<RenderObject> renderObjects;
    public List<PointLightObject> pointLightObjects;
    public List<MaterialData> materials;
    public List<NodeData> nodeData;

    public SceneAsset() {
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

    public void instantiate(World world) {
        for (MeshData md : meshData) {
            Entity e = new Entity();
            MeshRendererComponent mrc = new MeshRendererComponent();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model: \n");
        sb.append("Meshcount: ").append(meshData.size()).append("\n");
        return sb.toString();
    }
}
