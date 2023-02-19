package org.anomalou.controller;

import org.anomalou.model.Canvas;
import org.anomalou.model.ObjectCache;
import org.anomalou.model.PointerTool;
import org.anomalou.model.Tool;

import java.awt.*;
import java.util.ArrayList;

public class ToolPanelController extends Controller{
    private Canvas canvas;
    private ObjectCache objectCache;

    private Tool selectedTool;
    private ArrayList<Tool> tools;

    public ToolPanelController(Canvas canvas, ObjectCache objectCache){
        this.canvas = canvas;
        this.objectCache = objectCache;

        tools = new ArrayList<>();

        loadTools();
    }

    public void press(Graphics g, Point position, int button, boolean released){
        selectedTool.press(g, position, button, released);
    }
    public void click(Graphics g, Point position, int button){
        selectedTool.click(g, position, button);
    }
    public void drag(Graphics g, Point position, int button){
        selectedTool.drag(g, position, button);
    }


    public Rectangle draw(Graphics g, Point position){
        return selectedTool.drawInterface(g, position);
    }

    private void loadTools(){
        tools.add(new PointerTool(canvas));

        selectedTool = tools.get(0);
    }
}
