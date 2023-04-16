package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.model.FPoint;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Origin class for all scene objects. Extend from it for compatibility with other objects
 */
public class SceneObject implements Serializable, Comparable<SceneObject>, Node<SceneObject> {
    protected transient final Logger logger = Logger.getLogger(Layer.class.getName());

    /**
     * Unique ID of the layer object
     */
    @Getter
    protected final UUID uuid = UUID.randomUUID();
    /**
     * Name of the layer. Human friendly
     */
    @Editable(name = "Name", editorType = EditorType.TEXT_FIELD)
    @Getter
    @Setter
    protected String name;
    /**
     * Priority of the layer in render queue. <br>
     * Big number of priority, means early render, also work oppositely
     */
    @Editable(name = "Priority", editorType = EditorType.TEXT_FIELD)
    @Getter
    @Setter
    protected Integer priority;
    /**
     * Position in local coordinates.
     */
    @Editable(name = "Local position", editorType = EditorType.VECTOR_EDITOR)
    @Getter
    @Setter
    protected Point localPosition;
    /**
     * Origin of local coordinates on scene
     */
    @Editable(name = "Origin position", editorType = EditorType.VECTOR_EDITOR)
    @Getter
    @Setter
    protected Point originPosition;

    protected SceneObject parent;

    public SceneObject() {
        name = "Object";
        priority = 0;
        localPosition = new Point();
        originPosition = new Point();
    }

    @Override
    public void setParent(SceneObject object) {
        parent = object;
    }

    @Override
    public SceneObject getParent() {
        return parent;
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public boolean isLeaf() {
        return !isRoot();
    }

    public Rectangle getBounds() {
        return new Rectangle();
    }

    public FPoint getGlobalPosition() {
        return new FPoint(localPosition.x + originPosition.x, localPosition.y + originPosition.y);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(SceneObject o) {
        return this.priority.compareTo(o.priority);
    }
}
