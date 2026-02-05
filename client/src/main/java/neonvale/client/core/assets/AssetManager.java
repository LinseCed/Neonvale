package neonvale.client.core.assets;

import java.util.Map;
import java.util.UUID;

public class AssetManager {
    private Map<UUID, AssetEntry<SceneAsset>> loadedScenes;
    private Map<UUID, AssetEntry<MeshData>> meshes;
    private Map<UUID, AssetEntry<MaterialData>> materials;

    public UUID loadScene(String path) {
        UUID id = UUID.randomUUID();
        SceneAsset sceneAsset = ModelLoader.load(path);
        for (MeshData md : sceneAsset.meshData) {
            UUID meshId = UUID.randomUUID();
            this.meshes.put(meshId, new AssetEntry<>(md));
        }
        for (MaterialData md : sceneAsset.materials) {
            UUID materialId = UUID.randomUUID();
            this.materials.put(materialId, new AssetEntry<>(md));
        }
        this.loadedScenes.put(id, new AssetEntry<>(sceneAsset));
        return id;
    }
}
