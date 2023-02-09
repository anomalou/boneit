package org.anomalou.view;

import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;
import org.anomalou.model.ObjectCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class CanvasPanel extends JPanel {

    private Canvas canvas;
    private ObjectCache objectCache;

    /**
     * Offset of the canvas on the workspace
     */
    private Point offset;
    /**
     * Scale of one pixel of canvas in screen pixels
     */
    private int scale;

    private boolean isScrollPressed;

    public CanvasPanel(Canvas canvas, ObjectCache objectCache){
        this.canvas = canvas;
        this.objectCache = objectCache;
        offset = new Point(0, 0);
        scale = 1;

        isScrollPressed = false;

        createMouseListener();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        drawInterface(g);

        ArrayList<Layer> layers = sort(canvas.getLayersHierarchy());

        layers.forEach(layer -> {
            if(layer.isVisible()){
                if(layer.getClass().equals(Bone.class))
                    drawBone((Bone) layer, g);
                else
                    drawLayer(layer, g);
            }
        });
    }

    private void drawInterface(Graphics g){
        g.setColor(Color.gray);

        //Draw ruler
        //LU corner
        g.drawString("0", scale * offset.x + 1, 10); //TODO MAGIC numbers!!!
        g.drawString("0", 0, scale * offset.y + 10);
        g.drawLine(scale * offset.x, 0, scale * offset.x, getHeight());
        g.drawLine(0, scale * offset.y, getWidth(), scale * offset.y);

        //RD corner
        g.drawString(String.format("%d", canvas.getWidth()), scale * (offset.x + 1 + canvas.getWidth()), 10);
        g.drawString(String.format("%d", canvas.getHeight()), 0, scale * (offset.y + 10 + canvas.getHeight()));
        g.drawLine(scale * (offset.x + canvas.getWidth()), 0, scale * (offset.x + canvas.getWidth()), getHeight());
        g.drawLine(0, scale * (offset.y + canvas.getHeight()), getWidth(), scale * (offset.y + canvas.getHeight()));

        //Pixel in corners
        g.drawString(String.format("%d", -offset.x), 10, 10); //TODO magic numbers!
        g.drawString(String.format("%d", -offset.y), 1, 20);
        g.drawString(String.format("%d", getWidth() / scale - offset.x - canvas.getWidth()), getWidth() - 30, 10);
        g.drawString(String.format("%d", getHeight() / scale - offset.y - canvas.getHeight()), 1, getHeight() - 10);

        g.setColor(Color.black);
    }

    private void createMouseListener(){
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

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scale = Math.max(1, scale - e.getWheelRotation());
                scale = Math.min(50, scale); //TODO magic number!
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            Point oldPos = new Point(0, 0);
            Point direction = new Point(0, 0);

            int pixelsPassed = 0;
            @Override
            public void mouseDragged(MouseEvent e) {
                calculateDirection(e.getPoint());
                if(isScrollPressed){
                    pixelsPassed += 1;
                }
                if(pixelsPassed >= scale){
                    pixelsPassed = 0;
                    offset.x += direction.x;
                    offset.y += direction.y;
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                calculateDirection(e.getPoint());
            }

            private int calculateDirection(Point pos){
                direction.x = pos.x - oldPos.x;
                direction.y = pos.y - oldPos.y;

                Point temp = oldPos;

                oldPos = pos;

                return (int) temp.distance(pos);
            }
        });
    }

    private ArrayList<Layer> sort(ArrayList<UUID> iArray){
        ArrayList<Layer> oArray = new ArrayList<>();
        ArrayList<Layer> tempArray = new ArrayList<>();

        iArray.forEach(uuid -> {
            tempArray.add(objectCache.getLayers().get(uuid));
        });

        Collections.sort(tempArray);

        tempArray.forEach(element -> {
            oArray.add(element);
            if(element.getClass().equals(Bone.class)){
                oArray.addAll(sort(((Bone) element).getChildren()));
            }
        });

        return oArray;
    }

    //TODO remove useless shitcode
//    private ArrayList<Layer> sortLayers(){
//        ArrayList<Layer> result = new ArrayList<>();
//        ArrayList<Layer> tempLayers = new ArrayList<>();
//
//        canvas.getLayersHierarchy().forEach(uuid -> {
//            tempLayers.add(objectCache.getLayers().get(uuid));
//        });
//
//        Collections.sort(tempLayers);
//
//        tempLayers.forEach(layer -> {
//            result.add(layer);
//            if(layer.getClass().equals(Bone.class)){
//                result.addAll(sortBone((Bone) layer));
//            }
//        });
//
//        return result;
//    }
//
//    private ArrayList<Layer> sortBone(Bone bone){
//        ArrayList<Layer> result = new ArrayList<>();
//        ArrayList<Layer> tempBones = new ArrayList<>();
//
//        bone.getChildren().forEach(uuid -> {
//            tempBones.add(objectCache.getLayers().get(uuid));
//        });
//
//        Collections.sort(tempBones);
//
//        tempBones.forEach(layer -> {
//            result.add(layer);
//            if(layer.getClass().equals(Bone.class)){
//                result.addAll(sortBone((Bone) layer));
//            }
//        });
//
//        return result;
//    }

    private void drawLayer(Layer layer, Graphics g){
        g.drawImage(layer.getBaseBitmap(), scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y), scale * layer.getBaseBitmap().getWidth(), scale * layer.getBaseBitmap().getHeight(), null);
    }

    private void drawBone(Bone bone, Graphics g){
        g.drawImage(bone.getTransformBitmap(), scale * (offset.x + bone.getPosition().x - bone.getRootBasePosition().x), scale * (offset.y + bone.getPosition().y - bone.getRootBasePosition().y), scale * bone.getTransformBitmap().getWidth(), scale * bone.getTransformBitmap().getHeight(), null);
    }
}
