package org.anomalou.view;

import org.anomalou.controller.ObjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.Bone;
import org.anomalou.model.Canvas;
import org.anomalou.model.FPoint;
import org.anomalou.model.Layer;

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
    private ObjectController objectController;
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

    public CanvasPanel(Canvas canvas, ObjectController objectController, PropertiesController propertiesController){
        this.canvas = canvas;
        this.objectController = objectController;
        this.propertiesController = propertiesController;
        offset = new Point(0, 0);
        scale = 1;

        isScrollPressed = false;

        loadGraphics();
        loadProperties();
        createMouseListeners();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        drawScene(g);
        drawInterface(g);
        drawSelection(g);
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

    private void drawScene(Graphics g){
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

    private void drawSelection(Graphics g){
        Layer layer = objectController.getObject(canvas.getSelection());

        if(layer == null)
            return;

        g.setColor(Color.black);

        if(layer.getClass().equals(Bone.class)){
            Point bonePosition = new Point(offset.x + layer.getPosition().x - ((Bone) layer).getRootVectorOrigin().x, offset.y + layer.getPosition().y - ((Bone) layer).getRootVectorOrigin().y);
            g.drawRect(scale * (bonePosition.x),
                    scale * (bonePosition.y),
                    scale * (layer.getBaseBitmap().getWidth()), scale * (layer.getBaseBitmap().getHeight()));

            g.setColor(Color.green);

            //Cross in the rootBasePosition
            g.drawLine(scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y - 1), scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y + 1));
            g.drawLine(scale * (offset.x + layer.getPosition().x - 1), scale * (offset.y + layer.getPosition().y), scale * (offset.x + layer.getPosition().x + 1), scale * (offset.y + layer.getPosition().y));

            //Vectors
            //rootDirection
            FPoint parentRotationVector = objectController.getParentRotationVector((Bone) layer);
            g.drawLine(scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y),
                    (int) Math.round(scale * (offset.x + layer.getPosition().x + parentRotationVector.x)),
                    (int) Math.round(scale * (offset.y + layer.getPosition().y + parentRotationVector.y)));

            //rotation vector
            g.setColor(Color.cyan);

            FPoint rotationVector = objectController.getFullRotationVector((Bone) layer);
            g.drawLine(scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y),
                    (int) Math.round(scale * (offset.x + layer.getPosition().x + rotationVector.x)),
                    (int) Math.round(scale * (offset.y + layer.getPosition().y + rotationVector.y)));

        }else{
            g.drawRect(scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y),
                    scale * layer.getBaseBitmap().getWidth(), scale * layer.getBaseBitmap().getHeight());
        }
    }

    /**
     * Check all objects in cache, that can be in mouse pointer hit area.
     * @param clickPosition mouse click position in screen coordinates (raw position)
     */
    private void select(Point clickPosition){
        final Point onCanvasPosition = screenToCanvas(clickPosition);

        sort(canvas.getLayersHierarchy()).forEach(layer -> {
            if(isClickInBound(layer, onCanvasPosition))
                canvas.setSelection(layer.getUuid());
        });
    }

    /**
     * Check if mouse pointer is hit in bound of object (layer, bone etc)
     * @param layer layer to check
     * @param clickPosition mouse click position in canvas coordinates (use screenToCanvas to convert!)
     * @return boolean
     */
    private boolean isClickInBound(Layer layer, Point clickPosition){
        Point position = new Point(0, 0);
        int width = 0;
        int height = 0;

        if(layer.getClass().equals(Bone.class)){
            position.x = layer.getPosition().x - ((Bone) layer).getRootVectorOrigin().x;
            position.y = layer.getPosition().y - ((Bone) layer).getRootVectorOrigin().y;
        }else{
            position.x = layer.getPosition().x;
            position.y = layer.getPosition().y;
        }

        width = layer.getBaseBitmap().getWidth();
        height = layer.getBaseBitmap().getHeight();

        if(clickPosition.x >= position.x && clickPosition.x < (position.x + width)){
            if(clickPosition.y >= position.y && clickPosition.y < (position.y + height)){
                return true;
            }
        }

        return false;
    }

    /**
     * Convert mouse screen coordinates to canvas coordinates
     * @param screen mouse coordinates on screen
     * @return Point
     */
    private Point screenToCanvas(Point screen){
        screen.x = screen.x / scale - offset.x;
        screen.y = screen.y / scale - offset.y;

        return screen;
    }

    private void createMouseListeners(){
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isClickInBound(objectController.getObject(canvas.getSelection()), screenToCanvas(e.getPoint())))
                    return;//TODO draw process

                select(e.getPoint());
                repaint();
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
            tempArray.add(objectController.getObject(uuid));
        });

        Collections.sort(tempArray);

        tempArray.forEach(element -> {
            oArray.add(element);
            oArray.addAll(sort((element).getChildren()));
        });

        return oArray;
    }

    private void drawLayer(Layer layer, Graphics g){
        g.drawImage(layer.getBaseBitmap(), scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y), scale * layer.getBaseBitmap().getWidth(), scale * layer.getBaseBitmap().getHeight(), null);
    }

    private void drawBone(Bone bone, Graphics g){
        g.drawImage(bone.getTransformBitmap(), scale * (offset.x + bone.getPosition().x - bone.getRootVectorOrigin().x), scale * (offset.y + bone.getPosition().y - bone.getRootVectorOrigin().y), scale * bone.getTransformBitmap().getWidth(), scale * bone.getTransformBitmap().getHeight(), null);
    }
}
