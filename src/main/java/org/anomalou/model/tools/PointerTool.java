package org.anomalou.model.tools;

import org.anomalou.model.Canvas;
import org.anomalou.model.scene.SceneObject;
import org.anomalou.model.scene.TransformObject;

import java.awt.*;

public class PointerTool implements Tool{
    private final String name = "Pointer";

    private final Canvas canvas;

    public PointerTool(Canvas canvas){
        this.canvas = canvas;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rectangle drawInterface(Graphics g, Point position) {
        return new Rectangle();
    }

    @Override
    public void primaryUse(Graphics g, Point position) {
        select(position);
    }

    @Override
    public void secondaryUse(Graphics g, Point position) {

    }

    /**
     * Check all objects in cache, that can be in mouse pointer hit area.
     * @param clickPosition mouse click position in screen coordinates (raw position)
     */
    private void select(Point clickPosition){
        boolean selected = false;
        for(SceneObject object : canvas.sort()){
            if(object instanceof TransformObject){
                if(((TransformObject) object).isInBounds(clickPosition)){
                    canvas.setSelection(object.getUuid());
                    selected = true;
                }
            }
        }

        if(!selected)
            canvas.setSelection(null);
    }
}
