package org.anomalou.view;

import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class CanvasPanel extends JPanel {

    private Canvas canvas;

    public CanvasPanel(Canvas canvas){
        this.canvas = canvas;
    }

    @Override
    protected void paintComponent(Graphics g){
        for(Layer l : canvas.getLayersHierarchy()){
            if(l instanceof Layer){
                drawLayer(l, g);
            }
            if(l instanceof Bone){
                drawSkeleton((Bone) l, g);
            }
        }
    }

    private void drawLayer(Layer layer, Graphics g){
        g.drawImage(layer.getBaseBitmap(), layer.getPosition().x, layer.getPosition().y, null);
    }

    private void drawSkeleton(Bone bone, Graphics g){
        for(Bone b : bone.getChildren()){
            drawSkeleton(b, g);
        }
        g.drawImage(bone.getTransformBitmap(), bone.getPosition().x - bone.getRootBasePosition().x, bone.getPosition().y - bone.getRootBasePosition().y, null);
    }
}
