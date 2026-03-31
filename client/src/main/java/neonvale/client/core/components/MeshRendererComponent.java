package neonvale.client.core.components;

import neonvale.client.core.assets.MaterialData;
import neonvale.client.core.assets.MeshData;

import java.util.ArrayList;
import java.util.List;

public class MeshRendererComponent implements IComponent {

    public record MeshEntry(MeshData meshData, MaterialData material) {}

    public final List<MeshEntry> meshes = new ArrayList<>();

    public void addMesh(MeshData meshData, MaterialData material) {
        meshes.add(new MeshEntry(meshData, material));
    }
}
