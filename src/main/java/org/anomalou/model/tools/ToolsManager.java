package org.anomalou.model.tools;

import java.awt.*;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.model.Canvas;

/**
 * Core for tools. Use it and load all available tools. Also save its params.
 */
public class ToolsManager {
    private final Canvas canvas;
    /**
     * Array of all available tools
     */
    private final ArrayList<Tool> tools;
    /**
     * Currently selected tool.
     */
    @Getter
    @Setter
    private Tool currentTool;

    @Getter
    private Palette palette;

    public ToolsManager(Canvas canvas){
        this.canvas = canvas;
        tools = new ArrayList<>();
        currentTool = null;
        palette = new Palette();

        loadDefaultTools();
    }

    public ArrayList<Tool> getToolList(){
        return new ArrayList<>(tools);
    }

    public void primaryUseTool(Graphics g, Point point){
        if(currentTool == null)
            return;

        currentTool.primaryUse(g, point);
    }

    public void secondaryUseTool(Graphics g, Point point){
        if(currentTool == null)
            return;

        currentTool.secondaryUse(g, point);
    }

    public void startUse(Point position){
        if(currentTool == null)
            return;

        currentTool.startUse(position);
    }

    public void endUse(Point position){
        if(currentTool == null)
            return;

        currentTool.endUse(position);
    }

    private void loadDefaultTools(){
        tools.add(new PointerTool(canvas));
        tools.add(new TransformTool(canvas));
        tools.add(new BrushTool(canvas, palette));
        tools.add(new BoneTool(canvas));

        currentTool = tools.get(0);
    }
}
