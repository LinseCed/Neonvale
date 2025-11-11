package neonvale.client.core.assets;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class Texture {

    private int id;

    Texture(ByteBuffer encodedImage) {
        STBImage.stbi_set_flip_vertically_on_load(true);
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer c = BufferUtils.createIntBuffer(1);

        ByteBuffer decoded = STBImage.stbi_load_from_memory(encodedImage, w, h, c, 4);
        if (decoded == null) {
            throw new RuntimeException("Failed to load texture");
        }

        int texId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texId);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_SRGB8_ALPHA8, w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, decoded);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        STBImage.stbi_image_free(decoded);
        id = texId;
    }

    public int getId() {
        return id;
    }
}
