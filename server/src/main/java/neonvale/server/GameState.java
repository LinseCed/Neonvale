package neonvale.server;

import neonvale.shared.net.StatePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameState {
    private final ConcurrentHashMap<String, float[]> positions = new ConcurrentHashMap<>();

    public void updatePosition(String playerId, float x, float y, float z) {
        positions.put(playerId, new float[]{x, y, z});
    }

    public void removePlayer(String playerId) {
        positions.remove(playerId);
    }

    public StatePacket buildStatePacket() {
        List<StatePacket.PlayerEntry> entries = new ArrayList<>();
        positions.forEach((id, pos) -> entries.add(new StatePacket.PlayerEntry(id, pos[0], pos[1], pos[2])));
        return new StatePacket(entries);
    }
}
