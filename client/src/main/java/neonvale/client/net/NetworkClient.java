package neonvale.client.net;

import neonvale.shared.net.HelloPacket;
import neonvale.shared.net.MovePacket;
import neonvale.shared.net.PacketCodec;
import neonvale.shared.net.StatePacket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkClient {

    private Socket socket;
    private PrintWriter out;
    private String localPlayerId;
    private final AtomicReference<StatePacket> latestState = new AtomicReference<>();

    public void connect() {
        Properties props = new Properties();
        try (InputStream is = NetworkClient.class.getResourceAsStream("/network.properties")) {
            if (is != null) props.load(is);
        } catch (IOException ignored) {}

        String host = props.getProperty("serverHost", "localhost");
        int port = Integer.parseInt(props.getProperty("serverPort", "7777"));
        connect(host, port);
    }

    private void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            Thread.ofVirtual().start(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        Object packet = PacketCodec.decode(line);
                        if (packet instanceof HelloPacket hello) {
                            localPlayerId = hello.id;
                            System.out.println("Connected to server as " + localPlayerId);
                        } else if (packet instanceof StatePacket state) {
                            latestState.set(state);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
        } catch (IOException e) {
            System.out.println("Could not connect to server at " + host + ":" + port + " — " + e.getMessage());
        }
    }

    public void sendMove(float x, float y, float z) {
        if (out != null) {
            out.println(PacketCodec.encode(new MovePacket(x, y, z)));
        }
    }

    public String getLocalPlayerId() {
        return localPlayerId;
    }

    /** Returns the latest world state from the server and clears it, or null if none available. */
    public StatePacket pollState() {
        return latestState.getAndSet(null);
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
