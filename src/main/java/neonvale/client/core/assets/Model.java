package neonvale.client.core.assets;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private List<Mesh> meshes;


    public Model(List<Mesh> meshes) {
        this.meshes = new ArrayList<>(meshes);
    }

    public void draw() {
        for (Mesh mesh : meshes) {
            mesh.draw();
        }
    }
}
