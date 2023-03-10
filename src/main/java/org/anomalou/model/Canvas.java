package org.anomalou.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.function.BinaryOperator;
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
    private final ArrayList<UUID> layersHierarchy;
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
        layersHierarchy = new ArrayList<>();
        objectCache = new ObjectCache();
        selection = null;

        logger.fine("Workspace is created!");
    }

    public void reshape(int width, int height){
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public Layer getSelection(){
        return objectCache.getObjects().get(selection);
    }

    public void registerObject(Layer parent, @NonNull Layer object){
        getObjectCache().registerObject(object.getUuid(), object);

        if(parent == null){
            layersHierarchy.add(object.getUuid());
        }else{
            object.setParent(parent.uuid);
            parent.getChildren().add(object.uuid);
        }
        logger.fine(String.format("Object %s created!", object.getUuid()));
    }

    public void unregisterObject(Layer layer){
        if(layer.isRoot())
            layersHierarchy.remove(layer.getUuid());
        else{
            getObjectCache().getObjects().get(layer.getParent()).getChildren().remove(layer.getUuid());
        }

        getObjectCache().unregister(layer.getUuid());
    }

    public void registerObject(UUID parent, @NonNull Layer object){
        registerObject(objectCache.getObjects().get(parent), object);
    }

    public Layer getObject(UUID uuid){
        return objectCache.getObjects().get(uuid);
    }

    /**
     * Get all object on scene sorted by draw priority as list
     * @return ArrayList
     */
    public ArrayList<Layer> sort(){
        return _sort(layersHierarchy);
    }
    private ArrayList<Layer> _sort(ArrayList<UUID> iArray){
        ArrayList<Layer> oArray = new ArrayList<>();
        ArrayList<Layer> tempArray = new ArrayList<>();

        iArray.forEach(uuid -> {
            tempArray.add(objectCache.getObjects().get(uuid));
        });

        Collections.sort(tempArray);

        tempArray.forEach(element -> {
            oArray.add(element);
            oArray.addAll(_sort(element.getChildren()));
        });

        return oArray;
    }

    public void applyBoneTransform(Bone bone, Double additionalAngle){ //TODO also for layers
        FPoint rotatedVector = calculateFullRotationVector(bone);

        FPoint childrenPosition = new FPoint(bone.getPosition().x + rotatedVector.x, bone.getPosition().y + rotatedVector.y);

        applyBoneRotation(bone, additionalAngle);

        bone.getChildren().forEach(uuid -> {
            Layer l = objectCache.getObjects().get(uuid);
            if(l.getClass().equals(Bone.class)){
                Bone b = (Bone) l;
                b.setPosition(new Point((int)Math.round(childrenPosition.x), (int)Math.round(childrenPosition.y)));
                b.setParentRotationAngle(additionalAngle);
                applyBoneRotation(b, additionalAngle + b.getRotationAngle());
                applyBoneTransform(b, additionalAngle + b.getRotationAngle());
            }
        });

        logger.fine(String.format("Bone %s position applied!", bone.getUuid()));
    }

    public void applyBoneTransform(UUID uuid, Double additionalAngle){
        Layer l = objectCache.getObjects().get(uuid);
        if(l.getClass().equals(Bone.class)){
            applyBoneTransform((Bone) l, additionalAngle);
        }
    }

    //TODO maybe, in future, if i have time, make transformBitmap adaptation to rotation of the image
    public void applyBoneRotation(Bone bone, Double angle){
        bone.setTransformBitmap(new BufferedImage(bone.getBaseBitmap().getWidth(), bone.getBaseBitmap().getHeight(), BufferedImage.TYPE_INT_ARGB));
        Graphics2D g2d = bone.getTransformBitmap().createGraphics();
        angle *= -1;
        g2d.rotate(angle, bone.getRootVectorOrigin().x, bone.getRootVectorOrigin().y);
        g2d.drawImage(bone.getBaseBitmap(), null, 0, 0);
        g2d.dispose();

        logger.fine(String.format("Bone %s rotated to %f angle!", bone.getUuid().toString(), angle));
    }

    /**
     * get angle between direction and rootDirectionPosition vectors, as it began in (0, 0)
     * @return double
     */
    public double calculateRotationAngleFor(Bone bone, FPoint directionVector){
        FPoint normalizedRootDirectionVector = normalizeSourceVector(bone);

        double side = (directionVector.x) * (normalizedRootDirectionVector.y) - (directionVector.y) * (normalizedRootDirectionVector.x);

        side = side <= 0 ? 1 : -1;

        double cos = (directionVector.x * normalizedRootDirectionVector.x + directionVector.y * normalizedRootDirectionVector.y) /
                (Math.sqrt(Math.pow(directionVector.x, 2) + Math.pow(directionVector.y, 2)) * Math.sqrt(Math.pow(normalizedRootDirectionVector.x, 2) + Math.pow(normalizedRootDirectionVector.y, 2)));
        cos = Math.abs(cos) > 1d ? 1d : cos;

        double resultAngle =  Math.acos(cos) * side;
        if(Double.isNaN(resultAngle))
            resultAngle = 0d;

        bone.setRotationAngle(resultAngle);

        return resultAngle;
    }

    /**
     * Normalize rootDirection vector. Move it to (0; 0) coordinates.
     * @return FPoint normalized vector
     */
    public FPoint normalizeSourceVector(Bone bone){
        return new FPoint(bone.getRootVectorDirection().x - bone.getRootVectorOrigin().x, (bone.getRootVectorDirection().y - bone.getRootVectorOrigin().y) * -1);
    }

    /**
     * New vector, result of rotation rootDirection vector. Returns vector that start from (0; 0) coordinates.
     * @return FPoint vector
     */
    public FPoint calculateParentRotationVector(Bone bone){
        return calculateRotatedVector(normalizeSourceVector(bone), bone.getParentRotationAngle());
    }

    public FPoint calculateFullRotationVector(Bone bone){
        return calculateRotatedVector(normalizeSourceVector(bone), bone.getRotationAngle() + bone.getParentRotationAngle());
    }

    /**
     * Calculate new vector like if zeroVector (begins in (0, 0)) would be rotated to some angle
     * @param zeroVector vector to rotate (source in (0,0))
     * @param angle angle to rotate
     * @return FPoint
     */
    public FPoint calculateRotatedVector(FPoint zeroVector, Double angle){
        FPoint rotatedVector = new FPoint(zeroVector.x * Math.cos(angle) - zeroVector.y * Math.sin(angle),
                zeroVector.x * Math.sin(angle) + zeroVector.y * Math.cos(angle));

        rotatedVector.y *= -1;

        return rotatedVector;
    }

    public void updateObjects(){
        getLayersHierarchy().forEach(uuid -> {
            Layer object = getObject(uuid);
            if(object.getClass().equals(Bone.class))
                applyBoneTransform((Bone) object, ((Bone) object).getRotationAngle() + ((Bone) object).getParentRotationAngle());
        });
    }
}
