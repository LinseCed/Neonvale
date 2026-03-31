package neonvale.shared.net;

public class MovePacket {
    public final String type = "MOVE";
    public float x, y, z;

    public MovePacket(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
