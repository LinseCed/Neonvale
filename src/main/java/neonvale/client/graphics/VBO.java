package neonvale.client.graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VBO {

    private int size;
    private int id;

    VBO (int size, int usage) {
        this.size = size;
        this.id = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
        glBufferData(GL_ARRAY_BUFFER, size, usage);
    }

    public void bufferData(IntBuffer data) {

    }

    public void bufferData(FloatBuffer data) {

    }

    public void bufferData(ByteBuffer data) {

    }

    public void cleanup() {
        glDeleteBuffers(this.id);
        this.id = 0;
    }

    public int getId() {
        return this.id;
    }
}
