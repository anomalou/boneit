package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.Project;
import org.anomalou.view.UIManager;

public class Application {

    @Getter //TODO set it for TEST only!
    private Project project;

    @Getter
    private final PropertiesController propertiesController;

    private UIManager uiManager;

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
        openProject(project);
        return project;
    }

    public void openProject(Project project){
        this.project = project;
        projectController = new ProjectController(project);
        canvasController = new CanvasController(project.getCanvas());
        toolPanelController = new ToolPanelController(project.getCanvas(), project.getCanvas().getObjectCache());
        uiManager = new UIManager(propertiesController, canvasController, toolPanelController);
    }
}
