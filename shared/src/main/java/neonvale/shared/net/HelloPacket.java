package neonvale.shared.net;

public class HelloPacket {
    public final String type = "HELLO";
    public String id;

    public HelloPacket(String id) {
        this.id = id;
    }
}
