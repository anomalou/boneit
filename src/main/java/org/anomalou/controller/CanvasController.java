package org.anomalou.controller;

import org.anomalou.model.*;
import org.anomalou.model.Canvas;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

public class CanvasController extends Controller{
    private final Canvas canvas;

    public CanvasController(Canvas canvas) {
        this.canvas = canvas;
    }

    public Layer getSelection(){
        return canvas.getSelection();
    }

    public void setSelection(UUID objectID){
        canvas.setSelection(objectID);
    }

    public void registerObject(Layer parent, Layer object){
        canvas.registerObject(parent, object);
    }

    public void unregisterObject(Layer object){
        canvas.unregisterObject(object);
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

    public int getWidth(){
        return canvas.getWidth();
    }

    public int getHeight(){
        return canvas.getHeight();
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
            return canvas.getObject(uuid);
        }catch (Exception exception){
            logger.severe(String.format("Object with UUID %s not exist! Error:\n%s", uuid, exception.getMessage()));
        }

        return null; //TODO check, controller can return nulls?
    }

    public ArrayList<UUID> getLayersHierarchy(){
        return canvas.getLayersHierarchy();
    }

    public void updateObjects(){
        canvas.updateObjects();
    }
}
