package org.anomalou.view;

import org.anomalou.controller.PropertiesController;
import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.Layer;
import org.anomalou.model.ObjectCache;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class CanvasPanel extends JPanel {

    private Canvas canvas;
    private ObjectCache objectCache;
    private PropertiesController propertiesController;

    private BufferedImage rulerImage;

    private int rulerWidth;
    private int rulerHeight;
    private int rulerCornerOffsetLX;
    private int rulerCornerOffsetLY;
    private int rulerCornerOffsetUX;
    private int rulerCornerOffsetUY;
    private int rulerOffsetX;
    private int rulerOffsetY;
    private int scaleMin;
    private int scaleMax;

    /**
     * Offset of the canvas on the workspace
     */
    private Point offset;
    /**
     * Scale of one pixel of canvas in screen pixels
     */
    private int scale;

    private boolean isScrollPressed;

    public CanvasPanel(Canvas canvas, ObjectCache objectCache, PropertiesController propertiesController){
        this.canvas = canvas;
        this.objectCache = objectCache;
        this.propertiesController = propertiesController;
        offset = new Point(0, 0);
        scale = 1;

        isScrollPressed = false;

        loadGraphics();
        loadProperties();
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

    private void loadGraphics(){//TODO something with textures
        try{
            rulerImage = ImageIO.read(new File("ruler.png"));
        }catch (IOException exception){
            //TODO temporary
        }
    }

    private void loadProperties(){
        rulerWidth = propertiesController.getInt("ruler.width");
        rulerHeight = propertiesController.getInt("ruler.height");
        rulerCornerOffsetLX = propertiesController.getInt("ruler.corner.l.offset.x");
        rulerCornerOffsetLY = propertiesController.getInt("ruler.corner.l.offset.y");
        rulerCornerOffsetUX = propertiesController.getInt("ruler.corner.u.offset.x");
        rulerCornerOffsetUY = propertiesController.getInt("ruler.corner.u.offset.y");
        rulerOffsetX = propertiesController.getInt("ruler.offset.x");
        rulerOffsetY = propertiesController.getInt("ruler.offset.y");
        scaleMin = propertiesController.getInt("scale.min");
        scaleMax = propertiesController.getInt("scale.max");
    }

    private void drawInterface(Graphics g){
        g.setColor(Color.gray);

        //Draw ruler
        Point mousePosition = this.getMousePosition();
        if(mousePosition != null){
            g.drawImage(rulerImage, mousePosition.x - rulerWidth / 2, 0, rulerWidth, rulerHeight, null);
            g.drawImage(rulerImage, 0, mousePosition.y - rulerHeight / 2, rulerWidth, rulerHeight, null);
        }

        //LU corner
        g.drawString("0", scale * offset.x + rulerOffsetX, rulerOffsetY);
        g.drawString("0", 0, scale * offset.y + rulerOffsetY);
        g.drawLine(scale * offset.x, 0, scale * offset.x, getHeight());
        g.drawLine(0, scale * offset.y, getWidth(), scale * offset.y);

        //RD corner
        g.drawString(String.format("%d", canvas.getWidth()), scale * (offset.x + 1 + canvas.getWidth()), rulerOffsetY);
        g.drawString(String.format("%d", canvas.getHeight()), rulerOffsetX, scale * (offset.y + rulerOffsetY + canvas.getHeight()));
        g.drawLine(scale * (offset.x + canvas.getWidth()), 0, scale * (offset.x + canvas.getWidth()), getHeight());
        g.drawLine(0, scale * (offset.y + canvas.getHeight()), getWidth(), scale * (offset.y + canvas.getHeight()));

        //Pixel in corners
        g.drawString(String.format("%d", -offset.x), rulerCornerOffsetUX, rulerCornerOffsetUY);
        g.drawString(String.format("%d", -offset.y), rulerCornerOffsetLX, rulerCornerOffsetLY);
        g.drawString(String.format("%d", getWidth() / scale - offset.x - canvas.getWidth()), getWidth() - rulerCornerOffsetUX, rulerCornerOffsetUY);
        g.drawString(String.format("%d", getHeight() / scale - offset.y - canvas.getHeight()), rulerCornerOffsetLX, getHeight() - rulerCornerOffsetLY);

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
                scale = Math.max(scaleMin, scale - e.getWheelRotation());
                scale = Math.min(scaleMax, scale);
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
                repaint();
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
            oArray.addAll(sort((element).getChildren()));
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
