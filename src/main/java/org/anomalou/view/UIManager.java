package org.anomalou.view;

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
    }

    public void relocateView(){
        this.removeAll();

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasPanel, objectTreePanel);
        rightSplitPane.setResizeWeight(0.9);
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inspectorPanel, rightSplitPane);
        leftSplitPane.setResizeWeight(0.1);

        this.setLayout(new BorderLayout());

        this.add(leftSplitPane, BorderLayout.CENTER);
        this.add(new JLabel("Info panel"), BorderLayout.PAGE_END);
        this.revalidate();
    }

    public void updateCanvas(){
//        canvasController.updateObjects();
//        canvasPanel.repaint();
    }

    public void updateInspector(){
//        inspectorPanel.repaint();
//        inspectorPanel.updateFields();
    }

    public void updateTree(){
//        objectTreePanel.repaint();
    }
}
