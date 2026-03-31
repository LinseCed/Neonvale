package neonvale.client.core.components;

public class RemotePlayerComponent implements IComponent {
    public final String playerId;

    public RemotePlayerComponent(String playerId) {
        this.playerId = playerId;
    }
}
