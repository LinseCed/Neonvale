package neonvale.client.core;

public class Timer {

    private double lastTime;

    public Timer() {
        lastTime = getTime();
    }

    public float getDelta() {
        double current = getTime();
        float delta = (float) (current - lastTime);
        lastTime = current;
        return delta;
    }

    private double getTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }
}
