package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.LayerController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.Project;

public class Application {

    @Getter //TODO set it for TEST only!
    private Project project;

    @Getter
    private PropertiesController propertiesController;

    //TODO all controller should be temp classes, that created only for UI!
    @Getter
    private ProjectController projectController;
    @Getter
    private LayerController layerController;


    //TODO here will be view, shortcut controller and etc

    public Application(){
        propertiesController = new PropertiesController();
    }

    public Project createProject(){
        project = new Project();
        projectController = new ProjectController(project);
        layerController = new LayerController(project.getObjectCache());
        return project;
    }

    public void openProject(Project project){
        this.project = project;
        projectController = new ProjectController(project);
    }
}
