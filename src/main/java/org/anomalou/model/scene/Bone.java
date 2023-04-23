package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.model.FPoint;

import java.awt.*;
import java.util.ArrayList;

/**
 * Default group object. Uses tree dependency structure. Also, can be as group, also as a node of the group
 */
public class Bone extends TransformObject implements Group<SceneObject> {
    /**
     * Visibility of the bone rig.
     */
    @Editable(name = "Set bone always visible", description = "Should the bone rig always shown?", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    private boolean isBoneVisible;
    /**
     * Set this bone to end of a parents.
     */
    @Editable(name = "Set at end", description = "Puts this bone's origin position to its of its parent", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    private boolean setAtEnd;
    /**
     * Children of the layer, follows for its parent
     */
    private final ArrayList<SceneObject> children;

    public Bone() {
        super();

        name = "NewBone";
        rootVectorOrigin = new Point(0, 0);
        rootVectorDirection = new Point(0, 0);
        rotationAngle = 0d;
        parentRotationAngle = 0d;
        isBoneVisible = false;
        setAtEnd = true;
        children = new ArrayList<>();
    }

    @Override
    public void applyTransformation() {
        FPoint rotatedVector = calculateFullRotationVector();

        FPoint childPosition = new FPoint(getGlobalPosition().x + rotatedVector.x, getGlobalPosition().y + rotatedVector.y);

        getChildren().forEach(object -> {
            if (object instanceof TransformObject) {
                object.setOriginPosition(new Point((int) getGlobalPosition().x, (int) getGlobalPosition().y));

                if (object instanceof Bone)
                    if (((Bone) object).isSetAtEnd())
                        object.setOriginPosition(new Point((int) Math.round(childPosition.x), (int) Math.round(childPosition.y)));

                ((TransformObject) object).setParentRotationAngle(getFullRotationAngle());

                ((TransformObject) object).applyTransformation();
            }
        });

//        logger.fine(String.format("Bone %s position applied!", getUuid())); //TODO
    }

    @Override
    public void addObject(SceneObject object) {
        if (object != null) {
            try {
                object.setParent(this);
            } catch (Exception ex) {
//                logger.warning(ex.getMessage()); //TODO
            }
        }
        children.add(object);
    }

    @Override
    public void removeObject(SceneObject object) {
        if (object != null) {
            try {
                object.setParent(null);
            } catch (Exception ex) {
//                logger.warning(ex.getMessage()); //TODO
            }
        }
        children.remove(object);
    }

    @Override
    public ArrayList<SceneObject> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty() && !isRoot();
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Override
    public String toString() {
        return String.format("%s(%.2f deg)", name, Math.toDegrees(rotationAngle));
    }
}
