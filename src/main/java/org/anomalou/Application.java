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

import javax.swing.*;
import java.awt.*;

public class Application {

    private JFrame mainFrame;
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

    private JFrame makeFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setTitle("Boneto");
//        frame.setIconImage(Toolkit.getDefaultToolkit().getImage());
        return frame;
    }

    public void openInterface() {
        setUpLookAndFeel();

        mainFrame = makeFrame();
        uiManager.initInterface();
        uiManager.relocateView();

        if (mainFrame.getComponentCount() > 0)
            mainFrame.remove(uiManager);
        mainFrame.setLayout(new BorderLayout());

        mainFrame.add(uiManager, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.setVisible(true);
    }

    private void setUpLookAndFeel() {
        try {
            FlatLaf theme = new FlatLightLaf();
            System.setProperty("flatlaf.useWindowDecorations", "true");
            System.setProperty("flatlaf.menuBarEmbedded", "true");
            System.setProperty("flatlaf.animation", "true");
            System.setProperty("flatlaf.uiScale", "1.2");

            javax.swing.UIManager.put("Button.arc", 5);
            javax.swing.UIManager.put("Component.arc", 5);
            javax.swing.UIManager.put("ProgressBar.arc", 5);
            javax.swing.UIManager.put("TextComponent.arc", 5);

            javax.swing.UIManager.put("ScrollBar.trackArc", 999);
            javax.swing.UIManager.put("ScrollBar.thumbArc", 999);
            javax.swing.UIManager.put("ScrollBar.trackInsets", new Insets(2, 4, 2, 4));
            javax.swing.UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

            javax.swing.UIManager.setLookAndFeel(theme);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
