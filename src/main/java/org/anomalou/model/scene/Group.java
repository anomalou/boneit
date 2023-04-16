package org.anomalou.model.scene;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Interface for group interaction
 * @param <T> limitation is SceneObject. Set the generic type with group's type, that it should contain
 */
public interface Group<T extends SceneObject> extends Serializable {
    void addObject(T object);
    void removeObject(T object);
    ArrayList<T> getChildren();
}
