package neonvale.client.core;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Util {
    public static int create1x1Texture(int r, int g, int b, int a) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);

        ByteBuffer buffer = BufferUtils.createByteBuffer(4);
        buffer.put((byte) r).put((byte) g).put((byte) b).put((byte) a);
        buffer.flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        return tex;
    }
}
