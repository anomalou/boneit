package org.anomalou.controller;

import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;
import org.anomalou.model.Project;

import java.util.UUID;
import java.util.logging.Logger;

public class ProjectController {
    private final Logger logger = Logger.getLogger(ProjectController.class.getName());

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
        return layer;
    }

    public Bone createSkeleton(){
        Bone bone = new Bone();
//        project.getNameCache().registerName(bone.getUuid(), "NewSkeleton");
        project.getObjectCache().registerObject(bone.getUuid(), bone);
        //TODO make here check for exception if something won't registered, if so invoke dialog window with description
        project.getCanvas().getLayersHierarchy().add(bone.getUuid());
        return bone;
    }

    public Bone extrudeBone(Bone bone){
        Bone newBone = new Bone();
//        project.getNameCache().registerName(newBone.getUuid(), "NewBone");
        project.getObjectCache().registerObject(newBone.getUuid(), newBone);
        //TODO make here check for exception if something won't registered, if so invoke dialog window with description
        bone.getChildren().add(newBone.getUuid());
        return bone;
    }

    public Bone extrudeBone(UUID uuid){
        if(project.getObjectCache().getLayers().get(uuid) instanceof Bone){
            Bone bone = (Bone) project.getObjectCache().getLayers().get(uuid);
            return extrudeBone(bone);
        }else{
            logger.warning(String.format("Object with uuid %s is not a bone!", uuid.toString()));
            return null;
        }
    }
}
