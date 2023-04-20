package org.anomalou.controller;

import org.anomalou.model.tools.Palette;
import org.anomalou.model.tools.Tool;
import org.anomalou.model.tools.ToolPanel;

import java.awt.*;
import java.util.ArrayList;

public class ToolPanelController extends Controller {
    private ToolPanel toolPanel;

    public ToolPanelController(ToolPanel toolPanel) {
        this.toolPanel = toolPanel;
    }

    public Tool getCurrentTool() {
        return toolPanel.getCurrentTool();
    }

    public void setCurrentTool(Tool tool) {
        toolPanel.setCurrentTool(tool);
    }

    public ArrayList<Tool> getToolList() {
        return toolPanel.getToolList();
    }

    public Palette getPalette(){
        return toolPanel.getPalette();
    }

    public void primaryUseTool(Graphics g, Point point) {
        toolPanel.primaryUseTool(g, point);
    }

    public void secondaryUseTool(Graphics g, Point point) {
        toolPanel.secondaryUseTool(g, point);
    }
}
