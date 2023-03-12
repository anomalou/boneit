package org.anomalou.view;

import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;

import javax.swing.*;

public class UIManager extends JPanel {
    private PropertiesController propertiesController;
    private CanvasController canvasController;
    private ToolPanelController toolPanelController;

    public UIManager(PropertiesController propertiesController, CanvasController canvasController, ToolPanelController toolPanelController) {
        this.propertiesController = propertiesController;
        this.canvasController = canvasController;
        this.toolPanelController = toolPanelController;
    }


}
