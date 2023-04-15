package org.anomalou.model.scene;

import java.io.Serializable;
import java.util.ArrayList;

public interface Group<T extends SceneObject> extends Serializable {
    void addObject(T object);
    void removeObject(T object);
    ArrayList<T> getChildren();
}
