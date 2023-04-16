package org.anomalou.model.scene;

/**
 * Interface for node object. Node is a part of group, that contain this node
 * @param <T> limitation SceneObject. Set the generic type with a type you want this node's parent should have
 */
public interface Node<T extends SceneObject> {
    void setParent(T object);
    T getParent();
    boolean isRoot();
    boolean isLeaf();
}
