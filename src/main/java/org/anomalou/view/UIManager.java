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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, canvasPanel, objectTreePanel);
        splitPane.setResizeWeight(0.9);

        this.setLayout(new BorderLayout());

        this.add(splitPane, BorderLayout.CENTER);
        this.add(new JLabel("Info panel"), BorderLayout.PAGE_END);
        this.add(inspectorPanel, BorderLayout.WEST);
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
