package neonvale.client.core.assets;

import java.util.ArrayList;
import java.util.List;

public class Scene {

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model: \n");
        sb.append("Meshcount: ").append(meshData.size()).append("\n");
        return sb.toString();
    }
}
