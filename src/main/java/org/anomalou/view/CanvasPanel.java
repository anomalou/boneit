package org.anomalou.view;

import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.scene.*;
import org.anomalou.model.FPoint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CanvasPanel extends JPanel {
    private final UIManager uiManager;
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

    public CanvasPanel(UIManager uiManager){
        this.uiManager = uiManager;
        this.canvasController = uiManager.getCanvasController();
        this.propertiesController = uiManager.getPropertiesController();
        this.toolPanelController = uiManager.getToolPanelController();
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
        ArrayList<SceneObject> objects = canvasController.sort();

        objects.forEach(object -> {
            if(object instanceof Layer){
                if(((Layer) object).isVisible()){
                    drawLayer((Layer) object, g);
                }
            }
        });
    }

    private void drawLayer(Layer layer, Graphics g){
        g.drawImage(layer.getResultBitmap(), scale * (offset.x + (int)layer.getGlobalPosition().x - layer.getRootVectorOrigin().x), scale * (offset.y + (int)layer.getGlobalPosition().y - layer.getRootVectorOrigin().y), scale * layer.getSourceBitmap().getWidth(), scale * layer.getSourceBitmap().getHeight(), null);
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
        g.drawString(String.format("%d", canvasController.getWidth()), scale * (offset.x + 1 + canvasController.getWidth()), rulerOffsetY);
        g.drawString(String.format("%d", canvasController.getHeight()), rulerOffsetX, scale * (offset.y + rulerOffsetY + canvasController.getHeight()));
        g.drawLine(scale * (offset.x + canvasController.getWidth()), 0, scale * (offset.x + canvasController.getWidth()), getHeight());
        g.drawLine(0, scale * (offset.y + canvasController.getHeight()), getWidth(), scale * (offset.y + canvasController.getHeight()));

        //Pixel in corners
        g.drawString(String.format("%d", -offset.x), rulerCornerOffsetUX, rulerCornerOffsetUY);
        g.drawString(String.format("%d", -offset.y), rulerCornerOffsetLX, rulerCornerOffsetLY);
        g.drawString(String.format("%d", getWidth() / scale - offset.x - canvasController.getWidth()), getWidth() - rulerCornerOffsetUX, rulerCornerOffsetUY);
        g.drawString(String.format("%d", getHeight() / scale - offset.y - canvasController.getHeight()), rulerCornerOffsetLX, getHeight() - rulerCornerOffsetLY);

        g.setColor(Color.black);
    }

    private void drawSelection(Graphics g){
        SceneObject object = canvasController.getSelection();

        if(object == null)
            return;

        g.setColor(Color.black);

        if(object instanceof Layer){
            drawSelectedLayer(g, (Layer) object);
        }
        if(object instanceof TransformObject){
            drawSelectedTransformObject(g, (TransformObject) object);
        }
    }

    private void drawSelectedLayer(Graphics g, Layer object){
        g.setColor(Color.black);
        g.drawRect(scale * (offset.x + (int)object.getGlobalPosition().x - object.getRootVectorOrigin().x), scale * (offset.y + (int)object.getGlobalPosition().y - object.getRootVectorOrigin().y),
                scale * object.getSourceBitmap().getWidth(), scale * object.getSourceBitmap().getHeight());
    }

    private void drawSelectedSkeleton(Graphics g, SceneObject object){
        Bone bone = (Bone) object;
//        Point bonePosition = new Point(offset.x + bone.getPosition().x - bone.getRootVectorOrigin().x, offset.y + bone.getPosition().y - bone.getRootVectorOrigin().y);
//        g.drawRect(scale * (bonePosition.x),
//                scale * (bonePosition.y),
//                scale * (bone.getSourceBitmap().getWidth()), scale * (bone.getSourceBitmap().getHeight()));

        drawSelectedTransformObject(g, bone);
    }

    private void drawSelectedTransformObject(Graphics g, TransformObject transformObject){
        if(transformObject instanceof Group<?>){
            ((Group<SceneObject>) transformObject).getChildren().forEach(object -> {
                if(object instanceof Bone)
                    drawSelectedTransformObject(g, (Bone) object);
            });
        }

        g.setColor(Color.green);

        //TODO add more adaptivity. You too dump to remember all spots like this
        Point position = new Point((int)transformObject.getGlobalPosition().x, (int)transformObject.getGlobalPosition().y);

        //Cross in the rootBasePosition //TODO DEPRECATED delete this
//        g.drawLine(scale * (offset.x + position.x), scale * (offset.y + position.y - 1), scale * (offset.x + position.x), scale * (offset.y + position.y + 1));
//        g.drawLine(scale * (offset.x + position.x - 1), scale * (offset.y + position.y), scale * (offset.x + position.x + 1), scale * (offset.y + position.y));

        //Vectors
        //rootDirection
        FPoint parentRotationVector = transformObject.calculateParentRotationVector();
        g.drawLine(scale * (offset.x + position.x), scale * (offset.y + position.y),
                (int) Math.round(scale * (offset.x + position.x + parentRotationVector.x)),
                (int) Math.round(scale * (offset.y + position.y + parentRotationVector.y)));

        //rotation vector
        g.setColor(Color.cyan);

        FPoint rotationVector = transformObject.calculateFullRotationVector();
        g.drawLine(scale * (offset.x + position.x), scale * (offset.y + position.y),
                (int) Math.round(scale * (offset.x + position.x + rotationVector.x)),
                (int) Math.round(scale * (offset.y + position.y + rotationVector.y)));

    }

    private void drawLocalCoordinateOrigin(Graphics g, TransformObject object){

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
                uiManager.updateInspector();
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
                uiManager.updateTree();
                uiManager.updateInspector();

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

                oldPos = pos;
            }
        });
    }
}
