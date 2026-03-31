package neonvale.client.core;

import neonvale.client.core.components.IComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Entity {

    private final UUID id;
    private final Map<Class<? extends IComponent>, IComponent> components;
    private final List<Entity> children;

    public Entity() {
        this.id = UUID.randomUUID();
        this.components = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public <T extends IComponent> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    @SuppressWarnings("unchecked")
    public <T extends IComponent> T getComponent(Class<T> type) {
        return (T) components.get(type);
    }

    public boolean hasComponent(Class<? extends IComponent> type) {
        return components.containsKey(type);
    }

    public void addChild(Entity child) {
        children.add(child);
    }

    public List<Entity> getChildren() {
        return children;
    }

    public UUID getId() {
        return id;
    }
}
