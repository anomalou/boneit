package org.anomalou.model.scene;

public interface Node<T extends SceneObject> {
    void setParent(T object);
    T getParent();
    boolean isRoot();
    boolean isLeaf();
}
