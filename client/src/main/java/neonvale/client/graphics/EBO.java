package neonvale.client.graphics;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;

public class EBO {

    private int id;

    public EBO(int size) {
        this.id = glGenBuffers();
    }

    public void bufferData(IntBuffer buffer) {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getId() {
        return id;
    }

    public void cleanup() {
        glDeleteBuffers(this.id);
        this.id = 0;
    }
}
