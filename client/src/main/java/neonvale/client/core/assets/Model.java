package neonvale.client.core.assets;

import neonvale.client.resources.ShaderManager;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final List<Mesh> meshes;
    private final List<Material> materials;
    private List<SubMesh> subMeshes;

    Model(List<Mesh> meshes, List<Material> materials) {
        this.meshes = meshes;
        this.materials = materials;
    }
}
