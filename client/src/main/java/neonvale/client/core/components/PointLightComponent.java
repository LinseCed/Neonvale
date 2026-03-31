package neonvale.client.core.components;

import org.joml.Vector3f;

public class PointLightComponent implements IComponent {

    public Vector3f radiance;

    public PointLightComponent(Vector3f radiance) {
        this.radiance = radiance;
    }
}
