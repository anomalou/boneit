package org.anomalou.model.scene;

import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.model.FPoint;

import java.awt.*;
import java.util.ArrayList;

public class Bone extends TransformObject implements Groupable<SceneObject>{
    /**
     * Visibility of the bone rig.
     */
    @Editable(name = "Set bone always visible", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    private boolean isBoneVisible;
    /**
     * Set this bone to end of a parents.
     */
    @Editable(name = "Set child at end", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    private boolean setAtEnd;
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
        setAtEnd = true;
        parent = null;
        children = new ArrayList<>();
    }

    @Override
    public void applyTransformation(){
        FPoint rotatedVector = calculateFullRotationVector(); //TODO calculate also vector localPosition with directionVector

        FPoint childrenEndPosition = new FPoint(getGlobalPosition().x + rotatedVector.x, getGlobalPosition().y + rotatedVector.y);

        getChildren().forEach(object -> {
            if(object instanceof TransformObject){
                object.setParentPosition(new Point(getGlobalPosition().x, getGlobalPosition().y));

                if(object instanceof Bone)
                    if(((Bone) object).isSetAtEnd())
                        object.setParentPosition(new Point((int)Math.round(childrenEndPosition.x), (int)Math.round(childrenEndPosition.y)));

                ((TransformObject) object).setParentRotationAngle(getFullRotationAngle());

                try{
                    ((TransformObject) object).applyTransformation();
                }catch (ExecutionControl.NotImplementedException ex){
                    logger.warning(ex.getMessage());
                }
            }
        });

        logger.fine(String.format("Bone %s position applied!", getUuid()));
    }

    @Override
    public void addObject(SceneObject object) {
        if(object instanceof Groupable<?>){
            try{
                ((Groupable<SceneObject>) object).setParent(this);
            }catch (Exception ex){
                logger.warning(ex.getMessage());
            }
        }
        children.add(object);
    }

    @Override
    public void removeObject(SceneObject object) {
        if(object instanceof Groupable<?>){
            try{
                ((Groupable<SceneObject>) object).setParent(null);
            }catch (Exception ex){
                logger.warning(ex.getMessage());
            }
        }
        children.remove(object);
    }

    @Override
    public void setParent(SceneObject object) {
        parent = object;
    }

    @Override
    public ArrayList<SceneObject> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public SceneObject getParent() {
        return parent;
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Override
    public String toString(){
        return String.format("%s(%.2f deg)", name, Math.toDegrees(rotationAngle));
    }
}
