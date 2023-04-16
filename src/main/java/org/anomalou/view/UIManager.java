package org.anomalou.view;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;

import javax.swing.*;
import java.awt.*;

public class UIManager extends JPanel {
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
        super();
        this.propertiesController = propertiesController;
        this.canvasController = canvasController;
        this.toolPanelController = toolPanelController;
    }

    public void initInterface(){
        canvasPanel = new CanvasPanel(this);
        inspectorPanel = new InspectorPanel(this);
        objectTreePanel = new ObjectTreePanel(this);
        toolbar = new Toolbar(this);
    }

    public void relocateView(){
        this.removeAll();

        JPanel canvasContainer = new JPanel();
        canvasContainer.setLayout(new BorderLayout());
        canvasContainer.add(new JLabel("Scene"), BorderLayout.PAGE_START);
        canvasContainer.add(canvasPanel, BorderLayout.CENTER);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasContainer, objectTreePanel);
        rightSplitPane.setResizeWeight(0.9);
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inspectorPanel, rightSplitPane);
        leftSplitPane.setResizeWeight(0.1);

        this.setLayout(new BorderLayout());

        this.add(toolbar, BorderLayout.PAGE_START);
        this.add(leftSplitPane, BorderLayout.CENTER);
        this.add(new JLabel("by anomalou"), BorderLayout.PAGE_END);
        this.revalidate();
    }

    public void updateCanvas(){
        canvasController.updateObjects();
        canvasPanel.repaint();
    }

    public void updateInspector(){
        inspectorPanel.repaint();
        inspectorPanel.updateFields();
    }

    public void updateTree(){
        objectTreePanel.repaint();
    }
}
