package neonvale.client.core;

public class GameLoop {

    private boolean running = true;
    private final float TARGET_UPS = 60.0f;
    private final float UPDATE_TIME = 1.0f / TARGET_UPS;

    public void run(UpdateCallback update, RenderCallback render) {
        Timer timer = new Timer();
        float accumulator = 0.0f;

        while (running) {
            float deltaTime = timer.getDelta();
            accumulator += deltaTime;

            while (accumulator >= UPDATE_TIME) {
                update.update(UPDATE_TIME);
                accumulator -= UPDATE_TIME;
            }

            render.render();
        }
    }

    public void stop() {
        this.running = false;
    }
}
