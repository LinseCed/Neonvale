package neonvale.client;

import neonvale.client.core.GameLoop;
import neonvale.client.graphics.Window;

public class Neonvale {

    private Window window;
    private GameLoop gameLoop;

    public static void main(String[] args) {
        Neonvale neonvale = new Neonvale();
        neonvale.run();
    }

    private void init() {
        this.window = new Window();
        this.gameLoop = new GameLoop();
    }

    public void run() {
        init();
        this.gameLoop.run(this::update, this::render);
        cleanup();
    }

    private void update(float delta) {
        if (this.window.shouldClose()) {
            this.gameLoop.stop();
        }
    }

    private void render() {
        this.window.clear();
        this.window.update();
    }

    private void cleanup() {
        this.window.cleanup();
    }
}
