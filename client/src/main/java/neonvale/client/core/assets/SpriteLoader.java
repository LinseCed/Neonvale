package neonvale.client.core.assets;

import com.google.gson.Gson;
import neonvale.client.core.Sprite;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpriteLoader {

    private static Logger logger = Logger.getLogger(SpriteLoader.class.getName());

    public static Sprite load(String spriteFile) {
        Gson gson = new Gson();
        try {
            String json = Files.readString(Paths.get(spriteFile));
            SpriteConfig config =  gson.fromJson(json, SpriteConfig.class);
            return new Sprite(config);
        } catch (Exception _) {
            logger.log(Level.WARNING, "Failed to load sprite: {0} ", spriteFile);
        }
        throw new RuntimeException("Failed to load sprite file: " + spriteFile);
    }
}
