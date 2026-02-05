package neonvale.client.core.assets;

public class AssetEntry<T> {
    T data;
    int refCount;

    public AssetEntry(T data) {
        this.data = data;
        this.refCount = 0;
    }
}
