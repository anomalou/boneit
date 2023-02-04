package org.anomalou.view;

import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;
import org.anomalou.model.ObjectCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;

public class CanvasPanel extends JPanel {

    private Canvas canvas;
    private ObjectCache objectCache;

    private Point offset;

    private boolean isScrollPressed;

    public CanvasPanel(Canvas canvas, ObjectCache objectCache){
        this.canvas = canvas;
        this.objectCache = objectCache;
        offset = new Point(0, 0);

        isScrollPressed = false;

        createMouseListener();
    }

    @Override
    protected void paintComponent(Graphics g){
        drawInterface(g);

        ArrayList<Layer> layers = sortLayers();

        layers.forEach(layer -> {
            if(layer.getClass().equals(Bone.class))
                drawBone((Bone) layer, g);
            else
                drawLayer(layer, g);
        });
    }

    private void drawInterface(Graphics g){
        g.setColor(Color.gray);

        //Draw ruler
        //LU corner
        g.drawString("0", offset.x + 1, 10); //TODO MAGIC numbers!!!
        g.drawString("0", 0, offset.y + 10);
        g.drawLine(offset.x, 0, offset.x, getHeight());
        g.drawLine(0, offset.y, getWidth(), offset.y);

        //RD corner
        g.drawString(String.format("%d", canvas.getWidth()), offset.x + 1 + canvas.getWidth(), 10);
        g.drawString(String.format("%d", canvas.getHeight()), 0, offset.y + 10 + canvas.getHeight());
        g.drawLine(offset.x + canvas.getWidth(), 0, offset.x + canvas.getWidth(), getHeight());
        g.drawLine(0, offset.y + canvas.getHeight(), getWidth(), offset.y + canvas.getHeight());

        //Pixel in corners
        g.drawString(String.format("%d", -offset.x), 10, 10);
        g.drawString(String.format("%d", -offset.y), 1, 20);
        g.drawString(String.format("%d", getWidth() - offset.x), getWidth() - 30, 10);
        g.drawString(String.format("%d", getHeight() - offset.y), 1, getHeight() - 10);

        g.setColor(Color.black);
    }

    private void createMouseListener(){
        this.addMouseMotionListener(new MouseMotionListener() {
            Point oldPos = new Point(0, 0);
            Point direction = new Point(0, 0);
            @Override
            public void mouseDragged(MouseEvent e) {
                calculateDirection(e.getPoint());
                if(isScrollPressed){
                    offset.x += direction.x;
                    offset.y += direction.y;
                    getParent().repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                calculateDirection(e.getPoint());
            }

            private void calculateDirection(Point pos){
                direction.x = pos.x - oldPos.x;
                direction.y = pos.y - oldPos.y;

                oldPos = pos;
            }
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON2){
                    isScrollPressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON2){
                    isScrollPressed = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private ArrayList<Layer> sortLayers(){
        ArrayList<Layer> result = new ArrayList<>();
        ArrayList<Layer> tempLayers = new ArrayList<>();

        canvas.getLayersHierarchy().forEach(uuid -> {
            tempLayers.add(objectCache.getLayers().get(uuid));
        });

        Collections.sort(tempLayers);

        tempLayers.forEach(layer -> {
            result.add(layer);
            if(layer.getClass().equals(Bone.class)){
                result.addAll(sortBone((Bone) layer));
            }
        });

        return result;
    }

    private ArrayList<Layer> sortBone(Bone bone){ //TODO make it to draw also a layers
        ArrayList<Layer> result = new ArrayList<>();
        ArrayList<Layer> tempBones = new ArrayList<>();

        bone.getChildren().forEach(uuid -> {
            tempBones.add(objectCache.getLayers().get(uuid));
        });

        Collections.sort(tempBones);

        tempBones.forEach(layer -> {
            result.add(layer);
            if(layer.getClass().equals(Bone.class)){
                result.addAll(sortBone((Bone) layer));
            }
        });

        return result;
    }

    private void drawLayer(Layer layer, Graphics g){
        g.drawImage(layer.getBaseBitmap(), offset.x + layer.getPosition().x, offset.y + layer.getPosition().y, null);
    }

    private void drawBone(Bone bone, Graphics g){
        g.drawImage(bone.getTransformBitmap(), offset.x + bone.getPosition().x - bone.getRootBasePosition().x, offset.y + bone.getPosition().y - bone.getRootBasePosition().y, null);
    }
}
