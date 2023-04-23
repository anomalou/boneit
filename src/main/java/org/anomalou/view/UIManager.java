package org.anomalou.view;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectsManagerController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolsManagerController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UIManager {
    private JFrame startupFrame;
    private JFrame sessionFrame;
    @Getter
    private PropertiesController propertiesController;
    @Getter
    private ProjectsManagerController projectsManagerController;
    @Getter
    private CanvasController canvasController;
    @Getter
    private ToolsManagerController toolsManagerController;


    private ProjectsListPanel projectsListPanel;

    private CanvasPanel canvasPanel;
    private InspectorPanel inspectorPanel;
    private ObjectTreePanel objectTreePanel;
    private Toolbar toolbar;

    public UIManager(PropertiesController propertiesController, ProjectsManagerController projectsManagerController) {
        setUpLookAndFeel();

        this.propertiesController = propertiesController;
        this.projectsManagerController = projectsManagerController;
    }

    private void setUpLookAndFeel() {
        try {
            FlatLaf theme = new FlatLightLaf();
            System.setProperty("flatlaf.useWindowDecorations", "true");
            System.setProperty("flatlaf.menuBarEmbedded", "true");
            System.setProperty("flatlaf.animation", "true");
            System.setProperty("flatlaf.uiScale", "1");

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

    public void openStartup(){
        startupFrame = createStartupFrame();

        startupFrame.setVisible(true);
    }

    public void openSession(){
        createControllers();

        sessionFrame = createSessionFrame();
        sessionFrame.setVisible(true);

        startupFrame.setVisible(false);
        startupFrame.dispose();
    }

    private JFrame createFrame(int width, int height, String name) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setTitle(name);
        try {
            frame.setIconImage(ImageIO.read(this.getClass().getResource("icon.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return frame;
    }

    private JFrame createStartupFrame(){
        JFrame frame = createFrame(400, 400, "Boneto");

        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;

        frame.add(getStartupInterface(), constraints);

        return frame;
    }

    /**
     * Use it for open your application interface
     */
    private JFrame createSessionFrame() {
        JFrame frame = createFrame(400, 400, projectsManagerController.getProject().getName());

        frame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;
        constraints.insets = new Insets(0, 5, 0, 5);
        frame.add(getSessionInterface(), constraints);

        frame.setJMenuBar(createMenuBar());

        return frame;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");

        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                projectsManagerController.save();
            }
        });

        fileMenu.add(saveItem);
        menuBar.add(fileMenu);

        return menuBar;
    }

    private JComponent getStartupInterface(){
        JPanel content = new JPanel();

        content.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;

        projectsListPanel = new ProjectsListPanel(this);

        content.add(projectsListPanel, constraints);


        return content;
    }

    private JComponent getSessionInterface() {
        JPanel content = new JPanel();

        canvasPanel = new CanvasPanel(this);
        inspectorPanel = new InspectorPanel(this);
        objectTreePanel = new ObjectTreePanel(this);
        toolbar = new Toolbar(this);

        JPanel canvasContainer = new JPanel();
        canvasContainer.setLayout(new BorderLayout());
        canvasContainer.add(new JLabel("Scene"), BorderLayout.PAGE_START);
        canvasContainer.add(canvasPanel, BorderLayout.CENTER);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasContainer, objectTreePanel);
        rightSplitPane.setResizeWeight(0.9);
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inspectorPanel, rightSplitPane);
        leftSplitPane.setResizeWeight(0.1);

        content.setLayout(new BorderLayout());

        content.add(toolbar, BorderLayout.PAGE_START);
        content.add(leftSplitPane, BorderLayout.CENTER);
        content.add(new JLabel("by anomalou"), BorderLayout.PAGE_END);

        return content;
    }

    private void createControllers(){
        if(!projectsManagerController.isOpened())
            return;

        canvasController = new CanvasController(projectsManagerController.getProject().getCanvas());
        toolsManagerController = new ToolsManagerController(projectsManagerController.getToolPanel());
    }

    public void updateCanvas() { //TODO need optimization
        canvasController.updateObjects();
        canvasPanel.repaint();
    }

    public void updateInspector() { //TODO need optimization
        inspectorPanel.repaint();
        inspectorPanel.updateFields();
    }

    public void updateTree() { //TODO need optimization
        objectTreePanel.repaint();
    }
}
