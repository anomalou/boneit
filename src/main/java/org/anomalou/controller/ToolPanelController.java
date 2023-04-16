package org.anomalou.controller;

import org.anomalou.model.tools.ToolPanel;

import java.awt.*;

public class ToolPanelController extends Controller{
    private ToolPanel toolPanel;

    public ToolPanelController(ToolPanel toolPanel){
        this.toolPanel = toolPanel;
    }

    public void primaryUseTool(Graphics g, Point point){
        toolPanel.primaryUseTool(g, point);
    }

    public void secondaryUseTool(Graphics g, Point point){
        toolPanel.secondaryUseTool(g, point);
    }
}
