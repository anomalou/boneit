package org.anomalou.view;

import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;
import org.anomalou.model.ObjectCache;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.UUID;

public class CanvasPanel extends JPanel {

    private Canvas canvas;
    private ObjectCache objectCache;

    public CanvasPanel(Canvas canvas, ObjectCache objectCache){
        this.canvas = canvas;
        this.objectCache = objectCache;
    }

    @Override
    protected void paintComponent(Graphics g){
        for(UUID uuid : canvas.getLayersHierarchy()){
            Layer layer = objectCache.getLayers().get(uuid);
            if(layer.getClass().equals(Layer.class)){
                drawLayer(uuid, g);
            }
            if(layer.getClass().equals(Bone.class)){
                drawSkeleton(uuid, g);
            }
        }
    }

    private void drawLayer(UUID uuid, Graphics g){
        Layer layer = objectCache.getLayers().get(uuid);
        g.drawImage(layer.getBaseBitmap(), layer.getPosition().x, layer.getPosition().y, null);
    }

    private void drawSkeleton(UUID uuid, Graphics g){
        for(UUID u : ((Bone) objectCache.getLayers().get(uuid)).getChildren()){
            drawSkeleton(u, g);
        }
        Bone bone = (Bone) objectCache.getLayers().get(uuid);
        g.drawImage(bone.getTransformBitmap(), bone.getPosition().x - bone.getRootBasePosition().x, bone.getPosition().y - bone.getRootBasePosition().y, null);
    }
}
