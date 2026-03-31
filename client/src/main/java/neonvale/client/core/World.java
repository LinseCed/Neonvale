package neonvale.client.core;

import neonvale.client.core.components.IComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class World {

    private final List<Entity> entities = new ArrayList<>();

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> query(Class<? extends IComponent> c1) {
        return entities.stream()
                .filter(e -> e.hasComponent(c1))
                .collect(Collectors.toList());
    }

    public List<Entity> query(Class<? extends IComponent> c1, Class<? extends IComponent> c2) {
        return entities.stream()
                .filter(e -> e.hasComponent(c1) && e.hasComponent(c2))
                .collect(Collectors.toList());
    }

    public void update(float dt) {
    }
}
