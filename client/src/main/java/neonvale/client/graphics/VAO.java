package neonvale.client.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VAO {

    private int id;
    private List<Integer> bufferIDs = new ArrayList<>();
    private int indexCount;

    public VAO() {
        this.id = glGenVertexArrays();
    }

    public void addAttribute(int location, int size, FloatBuffer data) {
        this.bind();
        int vbo = glGenBuffers();
        this.bufferIDs.add(vbo);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glVertexAttribPointer(location, size, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(location);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        this.unbind();
    }

    public void setIndices(IntBuffer indices) {
        this.bind();
        int ebo = glGenBuffers();
        this.bufferIDs.add(ebo);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        indexCount = indices.remaining();
        this.unbind();
    }

    public void bind() {
        glBindVertexArray(this.id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public int getIndexCount() {
        return this.indexCount;
    }

    public void cleanup() {
        for (int vboID : bufferIDs) {
            glDeleteBuffers(vboID);
        }
        glDeleteVertexArrays(this.id);
        this.id = 0;
    }
}
