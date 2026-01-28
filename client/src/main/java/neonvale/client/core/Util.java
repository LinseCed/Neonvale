package neonvale.client.core;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.assimp.AIMatrix4x4;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.memAlloc;

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

    public static ByteBuffer readFileToBuffer(File file) {
        try {
            byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
            ByteBuffer buffer = memAlloc(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            return buffer;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load texture: " + file, e);
        }
    }

    public static Matrix4f toMatrix4f(AIMatrix4x4 ai) {
        return new Matrix4f(
                ai.a1(), ai.b1(), ai.c1(), ai.d1(),
                ai.a2(), ai.b2(), ai.c2(), ai.d2(),
                ai.a3(), ai.b3(), ai.c3(), ai.d3(),
                ai.a4(), ai.b4(), ai.c4(), ai.d4()
        );
    }

}
