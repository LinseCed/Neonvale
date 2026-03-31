package neonvale.shared.net;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketCodec {
    private static final Gson GSON = new Gson();

    public static String encode(Object packet) {
        return GSON.toJson(packet);
    }

    public static Object decode(String json) {
        JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
        String type = obj.get("type").getAsString();
        return switch (type) {
            case "HELLO" -> GSON.fromJson(obj, HelloPacket.class);
            case "MOVE"  -> GSON.fromJson(obj, MovePacket.class);
            case "STATE" -> GSON.fromJson(obj, StatePacket.class);
            default -> throw new IllegalArgumentException("Unknown packet type: " + type);
        };
    }
}
