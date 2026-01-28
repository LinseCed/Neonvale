package neonvale.client.core.assets;

import neonvale.client.resources.ShaderManager;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final List<Mesh> meshes;
    private final List<Material> materials;
    private final List<SubMesh> subMeshes;

    Model(List<Mesh> meshes, List<Material> materials, List<SubMesh> subMeshes) {
        this.meshes = meshes;
        this.materials = materials;
        this.subMeshes = subMeshes;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public List<Mesh> getMeshes() {
        return this.meshes;
    }

    public List<SubMesh> getSubMeshes() {
        return this.subMeshes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Model: \n");
        sb.append("Meshcount: ").append(meshes.size()).append("\n");
        sb.append("Materialcount: ").append(materials.size()).append("\n");
        return sb.toString();
    }
}
