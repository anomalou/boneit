package org.anomalou.controller;

import org.anomalou.model.tools.Palette;
import org.anomalou.model.tools.Tool;
import org.anomalou.model.tools.ToolsManager;

import java.awt.*;
import java.util.ArrayList;

public class ToolsManagerController extends Controller {
    private ToolsManager toolsManager;

    public ToolsManagerController(ToolsManager toolsManager) {
        this.toolsManager = toolsManager;
    }

    public Tool getCurrentTool() {
        return toolsManager.getCurrentTool();
    }

    public void setCurrentTool(Tool tool) {
        toolsManager.setCurrentTool(tool);
    }

    public ArrayList<Tool> getToolList() {
        return toolsManager.getToolList();
    }

    public Palette getPalette(){
        return toolsManager.getPalette();
    }

    public void primaryUseTool(Graphics g, Point point) {
        toolsManager.primaryUseTool(g, point);
    }

    public void secondaryUseTool(Graphics g, Point point) {
        toolsManager.secondaryUseTool(g, point);
    }

    public void startUse(Point position){
        toolsManager.startUse(position);
    }

    public void endUse(Point position){
        toolsManager.endUse(position);
    }
}
