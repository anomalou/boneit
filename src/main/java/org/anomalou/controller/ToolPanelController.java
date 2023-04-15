package org.anomalou.controller;

import org.anomalou.model.Canvas;
import org.anomalou.model.ObjectCache;
import org.anomalou.model.tools.PointerTool;
import org.anomalou.model.tools.Tool;
import org.anomalou.model.tools.ToolPanel;

import java.awt.*;
import java.util.ArrayList;

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
