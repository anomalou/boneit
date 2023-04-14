package org.anomalou.model.scene;

import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.model.FPoint;

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
        FPoint rotatedVector;
        rotatedVector = calculateFullRotationVector();

        FPoint childrenPosition;
        if(!isChildSetAtEnd())
            childrenPosition = new FPoint(getPosition().x + rotatedVector.x, getPosition().y + rotatedVector.y);
        else
            childrenPosition = new FPoint(getPosition().x, getPosition().y);

        getChildren().forEach(object -> {
            if(object instanceof TransformObject){
                object.setPosition(new Point((int)Math.round(childrenPosition.x), (int)Math.round(childrenPosition.y)));
                ((TransformObject) object).setParentRotationAngle(parentRotationAngle + rotationAngle);
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

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Override
    public String toString(){
        return String.format("%s(%.2f deg)", name, Math.toDegrees(rotationAngle));
    }
}
