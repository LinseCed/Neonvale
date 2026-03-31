package neonvale.server;

import neonvale.shared.net.MovePacket;
import neonvale.shared.net.PacketCodec;
import neonvale.shared.net.StatePacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final String playerId;
    private final GameState gameState;
    private PrintWriter out;

    public ClientHandler(Socket socket, String playerId, GameState gameState) {
        this.socket = socket;
        this.playerId = playerId;
        this.gameState = gameState;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void sendState(StatePacket state) {
        if (out != null) {
            out.println(PacketCodec.encode(state));
        }
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = writer;
            out.println(PacketCodec.encode(new neonvale.shared.net.HelloPacket(playerId)));

            String line;
            while ((line = in.readLine()) != null) {
                Object packet = PacketCodec.decode(line);
                if (packet instanceof MovePacket move) {
                    gameState.updatePosition(playerId, move.x, move.y, move.z);
                }
            }
        } catch (IOException e) {
            System.out.println("Player disconnected: " + playerId);
        } finally {
            gameState.removePlayer(playerId);
            this.out = null;
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
