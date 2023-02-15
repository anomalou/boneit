package org.anomalou.controller;

import org.anomalou.exception.RegistrationException;
import org.anomalou.model.Bone;
import org.anomalou.model.FPoint;
import org.anomalou.model.Layer;
import org.anomalou.model.ObjectCache;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class ObjectController extends Controller{
    private ObjectCache objectCache;

    public ObjectController(ObjectCache objectCache) {
        this.objectCache = objectCache;
    }

    public Bone extrudeBone(Bone bone){
        Bone newBone = new Bone();
        try{
            bone.getChildren().add(newBone.getUuid());
        }catch (NullPointerException exception){
            logger.severe(String.format("Parent bone not exist! Null pointer exception!"));
            return null; //TODO check, controller can return nulls?
        }
        try{
            objectCache.registerObject(newBone.getUuid(), newBone);
        }catch (RegistrationException exception){
            logger.severe(String.format("Bone %s not found! Error: %s",newBone.getUuid(), exception.getMessage()));
            return null; //TODO check, controller can return nulls?
        }
        logger.fine(String.format("Bone %s created! Now it parent is %s!", newBone.getUuid(), bone.getUuid()));
        return newBone;
    }

    public Bone extrudeBone(UUID uuid){
        if(objectCache.getLayers().get(uuid) instanceof Bone){
            Bone bone = (Bone) objectCache.getLayers().get(uuid);
            return extrudeBone(bone);
        }else{
            logger.severe(String.format("Object with uuid %s is not a bone!", uuid.toString()));
            return null; //TODO check, controller can return nulls?
        }
    }

    public void applyTransform(Bone bone, Double additionalAngle){ //TODO also for layers
        FPoint rotatedVector = getFullRotationVector(bone);

        FPoint childrenPosition = new FPoint(bone.getPosition().x + rotatedVector.x, bone.getPosition().y + rotatedVector.y);

        bone.getChildren().forEach(uuid -> {
            Layer l = objectCache.getLayers().get(uuid);
            if(l.getClass().equals(Bone.class)){
                Bone b = (Bone) l;
                b.setPosition(new Point((int)Math.round(childrenPosition.x), (int)Math.round(childrenPosition.y)));
                b.setParentRotationAngle(additionalAngle);
                applyRotation(b, additionalAngle + b.getRotationAngle());
                applyTransform(b, additionalAngle + b.getRotationAngle());
            }
        });

        logger.fine(String.format("Bone %s position applied!", bone.getUuid()));
    }

    public void applyTransform(UUID uuid, Double additionalAngle){
        Layer l = objectCache.getLayers().get(uuid);
        if(l.getClass().equals(Bone.class)){
            applyTransform((Bone) l, additionalAngle);
        }
    }

    //TODO maybe, in future, if i have time, make transformBitmap adaptation to rotation of the image
    public void applyRotation(Bone bone, Double angle){
//        if(bone.getTransformBitmap().getWidth() != bone.getBaseBitmap().getWidth() || bone.getTransformBitmap().getHeight() != bone.getBaseBitmap().getHeight())
            bone.setTransformBitmap(new BufferedImage(bone.getBaseBitmap().getWidth(), bone.getBaseBitmap().getHeight(), BufferedImage.TYPE_INT_ARGB));
        Graphics2D g2d = bone.getTransformBitmap().createGraphics();
        angle *= -1;
//        g2d.setComposite(AlphaComposite.Clear);
//        g2d.fillRect(0, 0, bone.getTransformBitmap().getWidth(), bone.getTransformBitmap().getHeight());
//        g2d.setComposite(AlphaComposite.Src);
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
        FPoint normalizedRootDirectionVector = getNormalizedRootVector(bone);

        double side = (directionVector.x) * (normalizedRootDirectionVector.y) - (directionVector.y) * (normalizedRootDirectionVector.x);

        side = side <= 0 ? 1 : -1;

        double cos = (directionVector.x * normalizedRootDirectionVector.x + directionVector.y * normalizedRootDirectionVector.y) /
                (Math.sqrt(Math.pow(directionVector.x, 2) + Math.pow(directionVector.y, 2)) * Math.sqrt(Math.pow(normalizedRootDirectionVector.x, 2) + Math.pow(normalizedRootDirectionVector.y, 2)));
        cos = Math.abs(cos) > 1d ? 1d : cos;

        Double resultAngle =  Math.acos(cos) * side;
        if(resultAngle.isNaN())
            resultAngle = 0d;

        bone.setRotationAngle(resultAngle);

        return resultAngle;
    }

    /**
     * Normalize rootDirection vector. Move it to (0; 0) coordinates.
     * @return FPoint normalized vector
     */
    public FPoint getNormalizedRootVector(Bone bone){
        return new FPoint(bone.getRootVectorDirection().x - bone.getRootVectorOrigin().x, (bone.getRootVectorDirection().y - bone.getRootVectorOrigin().y) * -1);
    }

    /**
     * New vector, result of rotation rootDirection vector. Returns vector that start from (0; 0) coordinates.
     * @return FPoint vector
     */
    public FPoint getParentRotationVector(Bone bone){
        return getRotatedVector(getNormalizedRootVector(bone), bone.getParentRotationAngle());
    }

    public FPoint getFullRotationVector(Bone bone){
        return getRotatedVector(getNormalizedRootVector(bone), bone.getRotationAngle() + bone.getParentRotationAngle());
    }

    /**
     * Calculate new vector like if zeroVector (begins in (0, 0)) would be rotated to some angle
     * @param zeroVector vector to rotate (source in (0,0))
     * @param angle angle to rotate
     * @return FPoint
     */
    public FPoint getRotatedVector(FPoint zeroVector, Double angle){
        FPoint rotatedVector = new FPoint(zeroVector.x * Math.cos(angle) - zeroVector.y * Math.sin(angle),
                zeroVector.x * Math.sin(angle) + zeroVector.y * Math.cos(angle));

        rotatedVector.y *= -1;

        return rotatedVector;
    }

    public void reshape(Layer layer, int w, int h){
        if(layer.getBaseBitmap().getWidth() == 1 || layer.getBaseBitmap().getHeight() == 1)
            layer.setBaseBitmap(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        else{
            Image tmp = layer.getBaseBitmap().getSubimage(0, 0, layer.getBaseBitmap().getWidth(), layer.getBaseBitmap().getHeight());
            BufferedImage nBaseBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = nBaseBitmap.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            layer.setBaseBitmap(nBaseBitmap);
        }

        if(!layer.getClass().equals(Bone.class))
            return;

        Bone bone = (Bone) layer;

        if(bone.getTransformBitmap().getWidth() == 1 || bone.getTransformBitmap().getHeight() == 1)
            bone.setTransformBitmap(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        else{
            Image tmp = bone.getTransformBitmap().getSubimage(0, 0, bone.getTransformBitmap().getWidth(), bone.getTransformBitmap().getHeight());
            BufferedImage nBaseBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = nBaseBitmap.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            bone.setTransformBitmap(nBaseBitmap);
        }
    }

    public Layer getObject(UUID uuid){
        try{
            return objectCache.getLayers().get(uuid);
        }catch (Exception exception){
            logger.severe(String.format("Object with UUID %s not exist! Error:\n%s", uuid, exception.getMessage()));
        }

        return null; //TODO check, controller can return nulls?
    }

    public HashMap<UUID, Layer> getAll(){
        return (HashMap<UUID, Layer>) objectCache.getLayers().clone();
    }
}
