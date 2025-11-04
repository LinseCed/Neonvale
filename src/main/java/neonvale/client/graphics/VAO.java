package neonvale.client.graphics;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VAO {

    static final int VERT_SIZE = 12;

    private final VBO vbo;
    private int vao;

    public VAO () {
        this.vbo = new VBO(VERT_SIZE, GL_DYNAMIC_DRAW);
        this.vao = glGenVertexArrays();
        glBindVertexArray(this.vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo.getId());

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, VERT_SIZE, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void cleanup() {
        vbo.cleanup();
        glDeleteVertexArrays(this.vao);
        this.vao = 0;
    }

}
