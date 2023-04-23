package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.*;
import org.anomalou.model.ProjectsManager;
import org.anomalou.view.UIManager;

public class Application {
    @Getter
    private ProjectsManager projectsManager;
    private UIManager uiManager;

    private final PropertiesController propertiesController;
    private final ProjectsManagerController projectsManagerController;


    //TODO here will be view, shortcut controller and etc

    public Application() {
        propertiesController = new PropertiesController();

        projectsManager = new ProjectsManager(propertiesController);
        projectsManagerController = new ProjectsManagerController(projectsManager);


        uiManager = new UIManager(propertiesController, projectsManagerController);
    }

    public void start() {
        try {
            uiManager.openStartup();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
