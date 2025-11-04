package neonvale.client.graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VBO {

    private int size;
    private int id;
    private int usage;

    VBO (int size, int usage) {
        this.size = size;
        this.id = glGenBuffers();
        this.usage = usage;
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
        glBufferData(GL_ARRAY_BUFFER, size, usage);
    }

    public void bufferData(IntBuffer data) {
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferData(FloatBuffer data) {
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void bufferData(ByteBuffer data) {
        glBindBuffer(GL_ARRAY_BUFFER, this.id);
        glBufferData(GL_ARRAY_BUFFER, data, usage);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void cleanup() {
        glDeleteBuffers(this.id);
        this.id = 0;
    }

    public int getId() {
        return this.id;
    }
}
