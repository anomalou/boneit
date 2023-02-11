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

    public void applySkeletonPosition(Bone bone){ //TODO also for layers
        double angle = bone.getAngle();
        FPoint normalizedVector = bone.getNormalizedRootVector();
        FPoint rotatedVector = new FPoint(normalizedVector.x * Math.cos(angle) - normalizedVector.y * Math.sin(angle),
                normalizedVector.x * Math.sin(angle) + normalizedVector.y * Math.cos(angle));

        FPoint childrenPosition = new FPoint(bone.getPosition().x + rotatedVector.x, bone.getPosition().y + -1 * rotatedVector.y);

        bone.getChildren().forEach(uuid -> {
            Layer l = objectCache.getLayers().get(uuid);
            if(l.getClass().equals(Bone.class)){
                Bone b = (Bone) l;
                b.setPosition(new Point((int)Math.round(childrenPosition.x), (int)Math.round(childrenPosition.y)));
                applySkeletonPosition(b);
            }
        });

        logger.fine(String.format("Bone %s position applied!", bone.getUuid()));
    }

    public void applySkeletonPosition(UUID uuid){
        Layer l = objectCache.getLayers().get(uuid);
        if(l.getClass().equals(Bone.class)){
            applySkeletonPosition((Bone) l);
        }
    }

    //TODO maybe, in future, if i have time, make transformBitmap adaptation to rotation of the image
    public void applyRotation(Bone bone){
        if(bone.getTransformBitmap().getWidth() != bone.getBaseBitmap().getWidth() || bone.getTransformBitmap().getHeight() != bone.getBaseBitmap().getHeight())
            bone.setTransformBitmap(new BufferedImage(bone.getBaseBitmap().getWidth(), bone.getBaseBitmap().getHeight(), BufferedImage.TYPE_INT_ARGB));
        Graphics2D g2d = bone.getTransformBitmap().createGraphics();
        Double angle = bone.getAngle() * -1;
        g2d.rotate(angle, bone.getRootBasePosition().x, bone.getRootBasePosition().y);
        g2d.drawImage(bone.getBaseBitmap(), null, 0, 0);
        g2d.dispose();

        logger.fine(String.format("Bone %s rotated to %f angle!", bone.getUuid().toString(), angle));
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
