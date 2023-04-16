package org.anomalou.view;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.Getter;
import org.anomalou.Main;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

public class UIManager {
    private JFrame mainFrame;
    private JPanel content;
    @Getter
    private PropertiesController propertiesController;
    @Getter
    private CanvasController canvasController;
    @Getter
    private ToolPanelController toolPanelController;


    @Getter
    private CanvasPanel canvasPanel;
    private InspectorPanel inspectorPanel;
    private ObjectTreePanel objectTreePanel;
    private Toolbar toolbar;

    public UIManager(PropertiesController propertiesController, CanvasController canvasController, ToolPanelController toolPanelController) {
        content = new JPanel();

        this.propertiesController = propertiesController;
        this.canvasController = canvasController;
        this.toolPanelController = toolPanelController;
    }

    /**
     * Use it for open your application interface
     */
    public void openInterface() {
        setUpLookAndFeel();

        mainFrame = makeFrame();
        initInterface();
        relocateView();

        if (mainFrame.getComponentCount() > 0)
            mainFrame.remove(content);
        mainFrame.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0d;
        constraints.weighty = 1.0d;
        constraints.insets = new Insets(0, 5, 0, 5);
        mainFrame.add(content, constraints);
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

    private JFrame makeFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setTitle("Boneto");
        try {
            frame.setIconImage(ImageIO.read(this.getClass().getResource("icon.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return frame;
    }

    public void initInterface() {
        canvasPanel = new CanvasPanel(this);
        inspectorPanel = new InspectorPanel(this);
        objectTreePanel = new ObjectTreePanel(this);
        toolbar = new Toolbar(this);
    }

    public void relocateView() {
        content.removeAll();

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
        content.revalidate();
    }

    public void updateCanvas() {
        canvasController.updateObjects();
        canvasPanel.repaint();
    }

    public void updateInspector() {
        inspectorPanel.repaint();
        inspectorPanel.updateFields();
    }

    public void updateTree() {
        objectTreePanel.repaint();
    }
}
