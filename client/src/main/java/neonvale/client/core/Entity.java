package neonvale.client.core;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Entity {
    private final UUID id;
    private final Map<Class<? extends Component>, Component> components;
    private final List<Entity> children;

    public Entity() {
        id = UUID.randomUUID();
        this.components = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public Map<Class<? extends Component>, Component> getComponents() {
        return this.components;
    }
}
