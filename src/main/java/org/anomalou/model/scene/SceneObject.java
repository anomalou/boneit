package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;

import java.awt.*;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Base class for all scene objects. Extends from it for compability with other objects.
 */
public class SceneObject implements Serializable, Comparable<SceneObject> {
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
     * Offset on canvas. Bones will ignore this parameter.
     */
    @Editable(name = "Local position", editorType = EditorType.VECTOR_EDITOR)
    @Getter
    @Setter
    protected Point localPosition;
    @Getter
    @Setter
    protected Point parentPosition;

    public SceneObject(){
        name = "Object";
        priority = 0;
        localPosition = new Point();
        parentPosition = new Point();
    }

    public Rectangle getBounds(){
        return new Rectangle();
    }

    public Point getGlobalPosition(){
        return new Point(parentPosition.x + localPosition.x, parentPosition.y + localPosition.y);
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(SceneObject o) {
        return this.priority.compareTo(o.priority);
    }
}
