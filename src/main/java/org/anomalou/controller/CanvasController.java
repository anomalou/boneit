package org.anomalou.controller;

import org.anomalou.exception.RegistrationException;
import org.anomalou.model.*;
import org.anomalou.model.Canvas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CanvasController extends Controller{
    private Canvas canvas;

    public CanvasController(Canvas canvas) {
        this.canvas = canvas;
    }

    public Bone extrudeBone(Bone bone){
        Bone newBone = new Bone();
        try{
            bone.getChildren().add(newBone.getUuid());
            newBone.setParent(bone.getUuid());
        }catch (NullPointerException exception){
            logger.severe(String.format("Parent bone not exist! Null pointer exception!"));
            return null; //TODO check, controller can return nulls?
        }
        try{
            canvas.getObjectCache().registerObject(newBone.getUuid(), newBone);
        }catch (RegistrationException exception){
            logger.severe(String.format("Bone %s not found! Error: %s",newBone.getUuid(), exception.getMessage()));
            return null; //TODO check, controller can return nulls?
        }
        logger.fine(String.format("Bone %s created! Now it parent is %s!", newBone.getUuid(), bone.getUuid()));
        return newBone;
    }

    public Bone extrudeBone(UUID uuid){
        if(canvas.getObjectCache().getLayers().get(uuid) instanceof Bone){
            Bone bone = (Bone) canvas.getObjectCache().getLayers().get(uuid);
            return extrudeBone(bone);
        }else{
            logger.severe(String.format("Object with uuid %s is not a bone!", uuid.toString()));
            return null; //TODO check, controller can return nulls?
        }
    }

    public ArrayList<Layer> sort(){
        return canvas.sort();
    }

    public void applyBoneTransform(Bone bone, Double additionalAngle){
        canvas.applyBoneTransform(bone, additionalAngle);
    }

    public void applyBoneTransform(UUID uuid, Double additionalAngle){
        canvas.applyBoneTransform(uuid, additionalAngle);
    }

    public void applyBoneRotation(Bone bone, Double angle){
        canvas.applyBoneRotation(bone, angle);
    }

    public double calculateRotationAngleFor(Bone bone, FPoint directionVector){
        return canvas.calculateRotationAngleFor(bone, directionVector);
    }

    public FPoint normalizeSourceVector(Bone bone){
        return canvas.normalizeSourceVector(bone);
    }

    public FPoint calculateParentRotationVector(Bone bone){
        return canvas.calculateParentRotationVector(bone);
    }

    public FPoint calculateFullRotationVector(Bone bone){
        return canvas.calculateFullRotationVector(bone);
    }

    public FPoint calculateRotatedVector(FPoint zeroVector, Double angle){
        return canvas.calculateRotatedVector(zeroVector, angle);
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
            return canvas.getObjectCache().getLayers().get(uuid);
        }catch (Exception exception){
            logger.severe(String.format("Object with UUID %s not exist! Error:\n%s", uuid, exception.getMessage()));
        }

        return null; //TODO check, controller can return nulls?
    }

    public HashMap<UUID, Layer> getObjectCache(){
        return (HashMap<UUID, Layer>) canvas.getObjectCache().getLayers().clone();
    }
}
