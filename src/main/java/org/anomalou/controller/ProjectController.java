package org.anomalou.controller;

import org.anomalou.Main;
import org.anomalou.exception.RegistrationException;
import org.anomalou.model.*;
import org.anomalou.model.Canvas;

import java.awt.*;
import java.util.UUID;
import java.util.logging.Logger;

public class ProjectController extends Controller{
    private final Project project;

    public ProjectController(Project project) {
        this.project = project;
    }
    public Canvas createCanvas(){
        Canvas canvas = new Canvas();
        project.setCanvas(canvas);
        return canvas;
    }

//    public Layer createLayer(){
//        Layer layer = new Layer();
//        try{
//            project.getCanvas().getObjectCache().registerObject(layer.getUuid(), layer);
//        }catch (RegistrationException exception){
//            logger.severe(String.format("Can't register new layer! Error: %s", exception.getMessage()));
//        }
//        project.getCanvas().getLayersHierarchy().add(layer.getUuid());
//        logger.fine(String.format("Layer %s created!", layer.getUuid()));
//        return layer;
//    }
//
//    public Bone createSkeleton(){
//        Bone bone = new Bone();
//        try{
//            project.getCanvas().getObjectCache().registerObject(bone.getUuid(), bone);
//        }catch (RegistrationException exception){
//            logger.severe(String.format("Can't register new skeleton! Error: %s", exception.getMessage()));
//        }
//        project.getCanvas().getLayersHierarchy().add(bone.getUuid());
//        logger.fine(String.format("Skeleton %s created!", bone.getUuid()));
//        return bone;
//    }

//    public Layer selectObject(UUID uuid){
//        Layer layer = null;
//        try{
//            layer = project.getObjectCache().getLayers().get(uuid);
//            project.getCanvas().setSelection(uuid);
//        }catch (Exception exception){
//            logger.severe(String.format("Object not found! Error:\n%s", exception.getMessage()));
//        }
//
//        return layer;
//    }
}
