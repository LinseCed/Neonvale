package neonvale.client.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class VAO {

    private final VBO vertexVbo;
    private final VBO normalVbo;
    private final VBO texCoordsVbo;
    private final EBO indicesEBO;
    private int vao;

    public VAO (FloatBuffer vertices, FloatBuffer normals, IntBuffer indices, FloatBuffer texCoords) {
        this.vao = glGenVertexArrays();
        this.vertexVbo = new VBO(vertices.limit(), GL_DYNAMIC_DRAW);
        this.vertexVbo.bufferData(vertices);
        this.normalVbo = new VBO(normals.limit(), GL_DYNAMIC_DRAW);
        this.normalVbo.bufferData(normals);
        this.texCoordsVbo = new VBO(texCoords.limit(), GL_DYNAMIC_DRAW);
        this.texCoordsVbo.bufferData(texCoords);
        this.indicesEBO = new EBO(indices.limit());
        this.indicesEBO.bufferData(indices);
        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, vertexVbo.getId());
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, normalVbo.getId());
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, texCoordsVbo.getId());
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesEBO.getId());

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public VAO (FloatBuffer vertices, FloatBuffer normals, IntBuffer indices) {
        this.vao = glGenVertexArrays();
        this.vertexVbo = new VBO(vertices.limit(), GL_DYNAMIC_DRAW);
        this.vertexVbo.bufferData(vertices);
        this.normalVbo = new VBO(normals.limit(), GL_DYNAMIC_DRAW);
        this.normalVbo.bufferData(normals);
        this.indicesEBO = new EBO(indices.limit());
        this.indicesEBO.bufferData(indices);
        this.texCoordsVbo = null;
        glBindVertexArray(this.vao);

        glBindBuffer(GL_ARRAY_BUFFER, vertexVbo.getId());
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, normalVbo.getId());
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesEBO.getId());

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void bind() {
        glBindVertexArray(this.vao);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void cleanup() {
        vertexVbo.cleanup();
        normalVbo.cleanup();
        indicesEBO.cleanup();
        glDeleteVertexArrays(this.vao);
        this.vao = 0;
    }

}
