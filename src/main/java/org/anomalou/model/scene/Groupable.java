package org.anomalou.model.scene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public interface Groupable<T extends SceneObject> extends Serializable {
    void addObject(T object);
    void removeObject(T object);
    void setParent(T object);
    ArrayList<T> getChildren();
}
