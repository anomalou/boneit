package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.Project;
import org.anomalou.model.tools.ToolPanel;
import org.anomalou.view.UIManager;

public class Application {
    @Getter
    private UIManager uiManager;

    private final PropertiesController propertiesController;


    //TODO here will be view, shortcut controller and etc

    public Application() {
        propertiesController = new PropertiesController();
    }

    public void start() {
        try {
            uiManager.openSession();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
