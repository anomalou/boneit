package org.anomalou.controller;

import org.anomalou.Main;
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

    public Layer createLayer(){
        Layer layer = new Layer();
//        project.getNameCache().registerName(layer.getUuid(), "NewLayer");
        project.getObjectCache().registerObject(layer.getUuid(), layer);
        //TODO make here check for exception if something won't registered, if so invoke dialog window with description
        project.getCanvas().getLayersHierarchy().add(layer.getUuid());
        logger.info(String.format("Layer %s created!", layer.getUuid()));
        return layer;
    }

    public Bone createSkeleton(){
        Bone bone = new Bone();
//        project.getNameCache().registerName(bone.getUuid(), "NewSkeleton");
        project.getObjectCache().registerObject(bone.getUuid(), bone);
        //TODO make here check for exception if something won't registered, if so invoke dialog window with description
        project.getCanvas().getLayersHierarchy().add(bone.getUuid());
        logger.info(String.format("Skeleton %s created!", bone.getUuid()));
        return bone;
    }
}
