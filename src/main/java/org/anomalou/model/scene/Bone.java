package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class Bone extends TransformObject implements Groupable<SceneObject>{
    /**
     * Visibility of the bone rig.
     */
    @Editable(name = "Set bone visibility", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    private boolean isBoneVisible;
    @Editable(name = "Set child at end", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    private boolean childSetAtEnd;
    private SceneObject parent;
    /**
     * Children of the layer, follows for its parent
     */
    private final ArrayList<SceneObject> children;

    public Bone(){
        super();

        name = "NewBone";
        rootVectorOrigin = new Point(0, 0);
        rootVectorDirection = new Point(0, 0);
        rotationAngle = 0d;
        parentRotationAngle = 0d;
        isBoneVisible = false;
        childSetAtEnd = false;
        parent = null;
        children = new ArrayList<>();
    }

    @Override
    public void applyTransformation(){

    }

    @Override
    public void addObject(SceneObject object) {

    }

    @Override
    public void removeObject(SceneObject object) {

    }

    @Override
    public void setParent(SceneObject object) {

    }

    @Override
    public ArrayList<SceneObject> getChildren() {
        return null;
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Override
    public String toString(){
        return String.format("%s(%.2f deg)", name, Math.toDegrees(rotationAngle));
    }
}
