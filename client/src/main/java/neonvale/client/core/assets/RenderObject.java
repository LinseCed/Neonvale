package neonvale.client.core.assets;

public class RenderObject {
    public int meshID;
    public int transformID;
    public int materialID;

    public RenderObject(int meshID, int transformID, int materialID) {
        this.meshID = meshID;
        this.transformID = transformID;
    }
}
