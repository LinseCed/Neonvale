package neonvale.server;

import neonvale.shared.net.StatePacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NeonvaleServer {

    private static final int PORT = 7777;
    private static final int BROADCAST_RATE_MS = 50; // 20 Hz

    private final GameState gameState = new GameState();
    private final List<ClientHandler> handlers = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        new NeonvaleServer().start();
    }

    public void start() throws IOException {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
            this::broadcastState, BROADCAST_RATE_MS, BROADCAST_RATE_MS, TimeUnit.MILLISECONDS
        );

        System.out.println("Server listening on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                String playerId = UUID.randomUUID().toString();
                System.out.println("Player connected: " + playerId);
                ClientHandler handler = new ClientHandler(socket, playerId, gameState);
                handlers.add(handler);
                Thread.ofVirtual().start(() -> {
                    handler.run();
                    handlers.remove(handler);
                });
            }
        }
    }

    private void broadcastState() {
        if (handlers.isEmpty()) return;
        StatePacket state = gameState.buildStatePacket();
        for (ClientHandler handler : handlers) {
            handler.sendState(state);
        }
    }
}
