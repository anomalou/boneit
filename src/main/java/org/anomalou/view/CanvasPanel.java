package org.anomalou.view;

import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
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

public class CanvasPanel extends JPanel {

    private final Canvas canvas;
    private final CanvasController canvasController;
    private final PropertiesController propertiesController;
    private final ToolPanelController toolPanelController;

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
    private final Point offset;
    /**
     * Scale of one pixel of canvas in screen pixels
     */
    private int scale;

    private boolean isScrollPressed;

    public CanvasPanel(Canvas canvas, CanvasController canvasController, PropertiesController propertiesController, ToolPanelController toolPanelController){
        this.canvas = canvas;
        this.canvasController = canvasController;
        this.propertiesController = propertiesController;
        this.toolPanelController = toolPanelController;
        offset = new Point(0, 0);
        scale = 1;

        isScrollPressed = false;

        loadGraphics();
        loadProperties();
        createMouseListeners();

        linkKeyShortcuts();
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
        ArrayList<Layer> layers = canvasController.sort();

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
        Layer layer = canvas.getSelection();

        if(layer == null)
            return;

        g.setColor(Color.black);

        if(layer.getClass().equals(Bone.class)){
            drawSelectedSkeleton(g, (Bone) layer);
        }else{
            drawSelectedLayer(g, layer);
        }
    }

    private void drawSelectedLayer(Graphics g, Layer layer){
        g.drawRect(scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y),
                scale * layer.getBaseBitmap().getWidth(), scale * layer.getBaseBitmap().getHeight());
    }

    private void drawSelectedSkeleton(Graphics g, Bone bone){
        Point bonePosition = new Point(offset.x + bone.getPosition().x - bone.getRootVectorOrigin().x, offset.y + bone.getPosition().y - bone.getRootVectorOrigin().y);
        g.drawRect(scale * (bonePosition.x),
                scale * (bonePosition.y),
                scale * (bone.getBaseBitmap().getWidth()), scale * (bone.getBaseBitmap().getHeight()));

        drawSelectedBone(g, bone);
    }

    private void drawSelectedBone(Graphics g, Bone bone){
        bone.getChildren().forEach(uuid -> {
            if(canvasController.getObject(uuid).getClass().equals(Bone.class))
                drawSelectedBone(g, (Bone) canvasController.getObject(uuid));
        });

        g.setColor(Color.green);

        //Cross in the rootBasePosition
        g.drawLine(scale * (offset.x + bone.getPosition().x), scale * (offset.y + bone.getPosition().y - 1), scale * (offset.x + bone.getPosition().x), scale * (offset.y + bone.getPosition().y + 1));
        g.drawLine(scale * (offset.x + bone.getPosition().x - 1), scale * (offset.y + bone.getPosition().y), scale * (offset.x + bone.getPosition().x + 1), scale * (offset.y + bone.getPosition().y));

        //Vectors
        //rootDirection
        FPoint parentRotationVector = canvas.calculateParentRotationVector(bone);
        g.drawLine(scale * (offset.x + bone.getPosition().x), scale * (offset.y + bone.getPosition().y),
                (int) Math.round(scale * (offset.x + bone.getPosition().x + parentRotationVector.x)),
                (int) Math.round(scale * (offset.y + bone.getPosition().y + parentRotationVector.y)));

        //rotation vector
        g.setColor(Color.cyan);

        FPoint rotationVector = canvas.calculateFullRotationVector(bone);
        g.drawLine(scale * (offset.x + bone.getPosition().x), scale * (offset.y + bone.getPosition().y),
                (int) Math.round(scale * (offset.x + bone.getPosition().x + rotationVector.x)),
                (int) Math.round(scale * (offset.y + bone.getPosition().y + rotationVector.y)));

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

    private void linkKeyShortcuts(){ //TODO replace to KeyBinds
        Action pressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print("Key pressed!\n");
            }
        };
        Action released = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.print("Key released!\n");
            }
        };
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed mmb"), "pressed BUTTON1");
        getActionMap().put("pressed BUTTON1", pressed);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released mmb"), "released BUTTON1");
        getActionMap().put("released BUTTON1", released);
    }

    private void createMouseListeners(){
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toolPanelController.click(getGraphics(), screenToCanvas(e.getPoint()), e.getButton());
                //wrap into draw method
//                if(isClickInBound(objectController.getObject(canvas.getSelection()), screenToCanvas(e.getPoint()))){
//                    //Rotation tests! :3
//                    if(!objectController.getObject(canvas.getSelection()).getClass().equals(Bone.class))
//                        return;
//
//
//                    return;//TODO draw process
//                }
                // ^^^ it is

//                select(e.getPoint());

                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON2){
                    isScrollPressed = true;
                }

                toolPanelController.press(getGraphics(), screenToCanvas(e.getPoint()), e.getButton(), false);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON2){
                    isScrollPressed = false;
                }

                toolPanelController.press(getGraphics(), screenToCanvas(e.getPoint()), e.getButton(), true);
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
            final Point direction = new Point(0, 0);

            int pixelsPassed = 0;
            @Override
            public void mouseDragged(MouseEvent e) {
                calculateDirection(e.getPoint());

                toolPanelController.drag(getGraphics(), screenToCanvas(e.getPoint()), e.getModifiersEx());

                //LOCKED! CAN NOT BE IN TOOLS! IMPORTANT FUNCTION!
                pixelsPassed += 1;

                if(pixelsPassed >= scale){
                    if(isScrollPressed){
                        offset.x += direction.x;
                        offset.y += direction.y;
                    }
                    pixelsPassed = 0;
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                calculateDirection(e.getPoint());
            }

            private void calculateDirection(Point pos){
                direction.x = pos.x - oldPos.x;
                direction.y = pos.y - oldPos.y;

                Point temp = oldPos;

                oldPos = pos;

                temp.distance(pos);
            }
        });
    }



    private void drawLayer(Layer layer, Graphics g){
        g.drawImage(layer.getBaseBitmap(), scale * (offset.x + layer.getPosition().x), scale * (offset.y + layer.getPosition().y), scale * layer.getBaseBitmap().getWidth(), scale * layer.getBaseBitmap().getHeight(), null);
    }

    private void drawBone(Bone bone, Graphics g){
        g.drawImage(bone.getTransformBitmap(), scale * (offset.x + bone.getPosition().x - bone.getRootVectorOrigin().x), scale * (offset.y + bone.getPosition().y - bone.getRootVectorOrigin().y), scale * bone.getTransformBitmap().getWidth(), scale * bone.getTransformBitmap().getHeight(), null);
    }
}
