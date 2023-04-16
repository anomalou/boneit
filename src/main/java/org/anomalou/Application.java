package org.anomalou;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatTableCellBorder;
import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.Project;
import org.anomalou.model.tools.ToolPanel;
import org.anomalou.view.UIManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class Application {
    @Getter //TODO set it for TEST only!
    private Project project;
    private ToolPanel toolPanel;
    @Getter
    private UIManager uiManager;

    @Getter
    private final PropertiesController propertiesController;


    //TODO all controller should be temp classes, that created only for UI!
    @Getter
    private ProjectController projectController;
    @Getter
    private CanvasController canvasController;
    @Getter
    private ToolPanelController toolPanelController;


    //TODO here will be view, shortcut controller and etc

    public Application() {
        propertiesController = new PropertiesController();
    }

    public Project createProject() {
        project = new Project();
        openProject(project);
        return project;
    }

    public void openProject(Project project) {
        this.project = project;
        toolPanel = new ToolPanel(project.getCanvas());
        projectController = new ProjectController(project);
        canvasController = new CanvasController(project.getCanvas());
        toolPanelController = new ToolPanelController(toolPanel);
        uiManager = new UIManager(propertiesController, canvasController, toolPanelController);
    }

    public void start() {
        try {
            uiManager.openInterface();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
