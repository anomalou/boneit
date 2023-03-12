package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.Project;
import org.anomalou.view.UIManager;

import javax.swing.*;
import java.awt.*;

public class Application {

    private JFrame mainFrame;
    @Getter //TODO set it for TEST only!
    private Project project;

    @Getter
    private final PropertiesController propertiesController;

    @Getter
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
        mainFrame = makeFrame();
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

    private JFrame makeFrame(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
//        frame.setVisible(true);
        frame.setTitle("Boneto");
        return frame;
    }

    public void openInterface(){
        uiManager.initInterface();
        uiManager.relocateView();

        if(mainFrame.getComponentCount() > 0)
            mainFrame.remove(uiManager);
        mainFrame.setLayout(new BorderLayout());

        mainFrame.add(uiManager, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.setVisible(true);
    }
}
