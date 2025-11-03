package neonvale.client.core;

public class GameLoop {

    private boolean running = true;
    private final float TARGET_FPS = 60.0f;
    private final float FRAME_TIME = 1.0f / TARGET_FPS;

    public void run(UpdateCallback update, RenderCallback render) {
        Timer timer = new Timer();
        float accumulator = 0.0f;

        while (running) {
            float deltaTime = timer.getDelta();
            accumulator += deltaTime;

            while (accumulator >= FRAME_TIME) {
                update.update(FRAME_TIME);
                accumulator -= FRAME_TIME;
            }

            render.render();
        }
    }

    public void stop() {
        this.running = false;
    }
}
