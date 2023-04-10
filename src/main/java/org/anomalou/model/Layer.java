package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class Layer implements Serializable, Comparable<Layer> { //a base class for layers or bones
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

    @Editable(name = "Test field!", editorType = EditorType.TEXT_FIELD)
    protected String test;
    /**
     * Just image of the layer, its shape MUST be 1x1 or more! <br>
     * Do not try to transform this image, its just raw pixel information for future transformation!
     */
    @Getter
    @Setter
    protected transient BufferedImage baseBitmap;
    /**
     * Offset on canvas. Bones will ignore this parameter.
     */
    @Editable(name = "Position", editorType = EditorType.VECTOR_EDITOR)
    @Getter
    @Setter
    protected Point position;
    /**
     * Priority of the layer in render queue. <br>
     * Big number of priority, means early render, also work oppositely
     */
    @Editable(name = "Priority", editorType = EditorType.TEXT_FIELD)
    @Getter
    @Setter
    protected Integer priority;
    /**
     * Visibility og the bitmap.
     */
    @Editable(name = "Set visibility", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    protected boolean isVisible;
    @Getter
    @Setter
    private UUID parent;
    /**
     * Children of the layer, follows for its parent
     */
    @Getter
    private final ArrayList<UUID> children;

    public Layer(){
        name = "NewLayer";
        test = "test";
        position = new Point(0, 0);
        baseBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        logger.fine(String.format("Entity (%s) created!", getUuid().toString()));
        priority = 0;
        isVisible = true;
        parent = null;
        children = new ArrayList<>();
    }

    public boolean isRoot(){
        return parent == null;
    }

    public boolean isLeaf(){
        return children.isEmpty();
    }

    //------ OVERRIDES TO STRING METHOD
    @Override
    public String toString(){
        return name;
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        ImageIO.write(baseBitmap, "png", out);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        baseBitmap = ImageIO.read(in);
    }

    @Override
    public int compareTo(Layer o) {
        return this.priority.compareTo(o.priority);
    }
}
