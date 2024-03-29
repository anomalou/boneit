package org.anomalou.model.tools;

import org.anomalou.model.Canvas;
import org.anomalou.model.scene.SceneObject;
import org.anomalou.model.scene.TransformObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PointerTool implements Tool{
    private final String name = "Pointer";
    private Image icon;

    private final Canvas canvas;

    public PointerTool(Canvas canvas){
        this.canvas = canvas;

        loadResources();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Image getIcon() {
        return icon;
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

    @Override
    public void startUse() {

    }

    @Override
    public void endUse() {

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

    private void loadResources(){
        try{
            icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("pointer.png")));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
