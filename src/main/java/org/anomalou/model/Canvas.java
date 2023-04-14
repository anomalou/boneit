package org.anomalou.model;

import jdk.jshell.spi.ExecutionControl;
import lombok.Getter;
import lombok.Setter;
import org.anomalou.model.scene.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

public class Canvas implements Serializable {
    private final Logger logger = Logger.getLogger(Canvas.class.getName());

    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
    @Getter
    private final ArrayList<SceneObject> sceneObjects;
    /**
     * Cache that stores every object on scene. This allows get needed object faster.
     */
    @Getter
    private final ObjectCache objectCache;
    @Setter
    private UUID selection; //TODO not safe, invent something else

    public Canvas(){
        this(1, 1);
    }

    public Canvas(int width, int height){
        this.width = width;
        this.height = height;
        sceneObjects = new ArrayList<>();
        objectCache = new ObjectCache();
        selection = null;

        logger.fine("Workspace is created!");
    }

    public void reshape(int width, int height){
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public SceneObject getSelection(){
        return objectCache.getObjects().get(selection);
    }

    public void registerObject(SceneObject object){
        try{
            getObjectCache().registerObject(object.getUuid(), object);

            logger.fine(String.format("Object %s created!", object.getUuid()));
        }catch (NullPointerException ex){
            logger.warning(ex.getMessage());
        }
    }

    public void unregisterObject(SceneObject object){
        try{
            getObjectCache().unregister(object.getUuid());
            if(object instanceof Groupable<?>){
                if(!((Groupable<SceneObject>) object).isRoot()){
                    SceneObject parent = ((Groupable<?>) object).getParent();
                    if(parent instanceof Groupable<?>){
//                        ((Groupable<?>) parent).removeObject(object); //TODO need work
                    }
                }
            }
        }catch (NullPointerException ex){
            logger.warning(ex.getMessage());
        }
    }

    public void addObject(SceneObject object){
        sceneObjects.add(object);
    }

    public SceneObject getObject(UUID uuid){
        return objectCache.getObjects().get(uuid);
    }

    /**
     * Get all object on scene sorted by draw priority as list
     * @return ArrayList
     */
    public ArrayList<SceneObject> sort(){
        return _sort(sceneObjects);
    }
    private ArrayList<SceneObject> _sort(ArrayList<SceneObject> iArray){
        ArrayList<SceneObject> oArray = new ArrayList<>();
        ArrayList<SceneObject> tempArray = new ArrayList<>(iArray);

        Collections.sort(tempArray);

        tempArray.forEach(element -> {
            oArray.add(element);
            if(element instanceof Groupable)
                oArray.addAll(_sort(((Groupable) element).getChildren())); //TODO may exception here
        });

        return oArray;
    }

//    public void applyTransform(Bone object){ //TODO also for layers
//        applyRotation(object, object.getParentRotationAngle() + object.getRotationAngle());
//        applyPosition(object);
//
//        logger.fine(String.format("Bone %s position applied!", object.getUuid()));
//    }

//    public void applyTransform(TransformObject object){
//        try {
//            object.applyTransformation();
//        }catch (ExecutionControl.NotImplementedException ex){
//            logger.warning(ex.getMessage());
//        }
//    }

//    public void applyPosition(Bone object){
//        FPoint rotatedVector;
//        rotatedVector = calculateFullRotationVector(object);
//
//        FPoint childrenPosition;
//        if(!object.isChildSetAtEnd())
//            childrenPosition = new FPoint(object.getPosition().x + rotatedVector.x, object.getPosition().y + rotatedVector.y);
//        else
//            childrenPosition = new FPoint(object.getPosition().x, object.getPosition().y);
//
//        object.getChildren().forEach(uuid -> {
//            Layer l = objectCache.getObjects().get(uuid);
//            if(l.getClass().equals(Bone.class)){
//                Bone b = (Bone) l;
//                b.setPosition(new Point((int)Math.round(childrenPosition.x), (int)Math.round(childrenPosition.y)));
//
//                applyPosition(b);
//            }
//        });
//    }

//    public void applyRotation(Bone object, Double additionalAngle){
//        applyBoneRotation(object, additionalAngle);
//
//        object.getChildren().forEach(uuid -> {
//            Layer l = objectCache.getObjects().get(uuid);
//            if(l.getClass().equals(Bone.class)){
//                Bone b = (Bone) l;
//                b.setParentRotationAngle(additionalAngle);
//
//                Double parentAngle = additionalAngle;
//
//                applyRotation(b, parentAngle + b.getRotationAngle());
//            }
//        });
//    }

    //TODO maybe, in future, if i have time, make transformBitmap adaptation to rotation of the image
//    public void applyBoneRotation(Bone bone, Double angle){
//        bone.setResultBitmap(new BufferedImage(bone.getSourceBitmap().getWidth(), bone.getSourceBitmap().getHeight(), BufferedImage.TYPE_INT_ARGB));
//        Graphics2D g2d = bone.getResultBitmap().createGraphics();
//        angle *= -1;
//        g2d.rotate(angle, bone.getRootVectorOrigin().x, bone.getRootVectorOrigin().y);
//        g2d.drawImage(bone.getSourceBitmap(), null, 0, 0);
//        g2d.dispose();
//
//        logger.fine(String.format("Bone %s rotated to %f angle!", bone.getUuid().toString(), angle));
//    }



    public void updateObjects(){
        getSceneObjects().forEach(object -> {
            if(object instanceof TransformObject)
                try {
                    ((TransformObject) object).applyTransformation();
                }catch (ExecutionControl.NotImplementedException ex){
                    logger.warning(ex.getMessage());
                }
        });
    }
}
