package neonvale.client.core.assets;

import neonvale.client.core.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssetManager {
    private final Map<UUID, AssetEntry<MeshData>> meshes = new HashMap<>();
    private final Map<UUID, AssetEntry<MaterialData>> materials = new HashMap<>();

    public void loadScene(String path, World world) {
        neonvale.client.core.assets.ModelLoader.load(path, world);
    }
}
