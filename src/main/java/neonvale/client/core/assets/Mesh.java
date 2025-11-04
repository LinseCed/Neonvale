package neonvale.client.core.assets;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Mesh {

    private final FloatBuffer vertices;
    private final FloatBuffer normals;
    private final IntBuffer indices;

    public Mesh(FloatBuffer vertices, FloatBuffer normals, IntBuffer indices) {
        this.vertices = vertices;
        this.normals = normals;
        this.indices = indices;
    }
}
