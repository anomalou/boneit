package org.anomalou.model.tools;

import java.awt.*;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.model.Canvas;

/**
 * Core for tools. Use it and load all available tools. Also save its params.
 */
public class ToolPanel {
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

    public ToolPanel(Canvas canvas){
        this.canvas = canvas;
        tools = new ArrayList<>();
        currentTool = null;

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

    private void loadDefaultTools(){
        tools.add(new PointerTool(canvas));
        tools.add(new TransformTool(canvas));

        currentTool = tools.get(0);
    }
}
