package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
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
    private CanvasController canvasController;

    @Getter
    private ToolPanelController toolPanelController;


    //TODO here will be view, shortcut controller and etc

    public Application(){
        propertiesController = new PropertiesController();
    }

    public Project createProject(){
        project = new Project();
        projectController = new ProjectController(project);
        canvasController = new CanvasController(project.getCanvas());
        toolPanelController = new ToolPanelController(project.getCanvas(), project.getCanvas().getObjectCache());
        return project;
    }

    public void openProject(Project project){
        this.project = project;
        projectController = new ProjectController(project);
    }
}
