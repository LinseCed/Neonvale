package neonvale.client.core.assets;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private List<MeshData> meshData;
    public List<TransformComponent> transforms;
    private List<PointLightComponent> pointLights;

    private List<RenderObject> renderObjects;
    private List<PointLightObject> pointLightObjects;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model: \n");
        sb.append("Meshcount: ").append(meshData.size()).append("\n");
        return sb.toString();
    }
}
