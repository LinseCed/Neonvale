package neonvale.shared.net;

import java.util.List;

public class StatePacket {
    public final String type = "STATE";
    public List<PlayerEntry> players;

    public StatePacket(List<PlayerEntry> players) {
        this.players = players;
    }

    public static class PlayerEntry {
        public String id;
        public float x, y, z;

        public PlayerEntry(String id, float x, float y, float z) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
