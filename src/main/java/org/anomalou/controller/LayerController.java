package org.anomalou.controller;

import org.anomalou.model.Bone;
import org.anomalou.model.FPoint;
import org.anomalou.model.Layer;
import org.anomalou.model.ObjectCache;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.UUID;

public class LayerController extends Controller{
    private ObjectCache objectCache;

    public LayerController(ObjectCache objectCache) {
        this.objectCache = objectCache;
    }

    public Bone extrudeBone(Bone bone){
        Bone newBone = new Bone();
        objectCache.registerObject(newBone.getUuid(), newBone);
        //TODO make here check for exception if something won't registered, if so invoke dialog window with description
        bone.getChildren().add(newBone.getUuid());
        logger.fine(String.format("Bone %s created! Now it parent is %s!", newBone.getUuid(), bone.getUuid()));
        return newBone;
    }

    public Bone extrudeBone(UUID uuid){
        if(objectCache.getLayers().get(uuid) instanceof Bone){
            Bone bone = (Bone) objectCache.getLayers().get(uuid);
            return extrudeBone(bone);
        }else{
            logger.warning(String.format("Object with uuid %s is not a bone!", uuid.toString()));
            return null;
        }
    }

    public void applySkeletonPosition(Bone bone){
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
}