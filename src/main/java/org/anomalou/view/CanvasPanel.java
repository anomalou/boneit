package org.anomalou.view;

import org.anomalou.controller.CanvasController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolsManagerController;
import org.anomalou.model.scene.*;
import org.anomalou.model.FPoint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class CanvasPanel extends JPanel {
    private final UIManager uiManager;
    private final CanvasController canvasController;
    private final PropertiesController propertiesController;
    private final ToolsManagerController toolsManagerController;

    private BufferedImage horizontalRulerImage;
    private BufferedImage verticalRulerImage;

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

    private Color xAxisColor;
    private Color directionColor;

    public CanvasPanel(UIManager uiManager) {
        this.uiManager = uiManager;
        this.canvasController = uiManager.getCanvasController();
        this.propertiesController = uiManager.getPropertiesController();
        this.toolsManagerController = uiManager.getToolsManagerController();

        offset = new Point(0, 0);
        scale = 1;

        isScrollPressed = false;

        xAxisColor = new Color(255, 100, 100);
        directionColor = new Color(100, 255, 100);

        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setBorder(new LineBorder(Color.lightGray));
        setBackground(Color.white);

        loadGraphics();
        loadProperties();
        createMouseListeners();

        linkKeyShortcuts();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawScene(g);
        drawInterface(g);
        drawSelection(g);
    }

    private void loadGraphics() {//TODO something with textures
        try {
            horizontalRulerImage = ImageIO.read(this.getClass().getResource("ruler.png"));
            verticalRulerImage = new BufferedImage(horizontalRulerImage.getWidth(), horizontalRulerImage.getHeight(), horizontalRulerImage.getType());
            Graphics2D g2d = (Graphics2D) verticalRulerImage.getGraphics();
            g2d.rotate(Math.toRadians(-90), horizontalRulerImage.getWidth() / 2.0, horizontalRulerImage.getHeight() / 2.0);
            g2d.drawImage(horizontalRulerImage, 0, 0, null);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void loadProperties() {
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

    private void drawScene(Graphics g) {
        ArrayList<SceneObject> objects = canvasController.sort();

        objects.forEach(object -> {
            if (object instanceof Layer) {
                if (((Layer) object).isVisible()) {
                    drawLayer(g, (Layer) object);
                }
            }
        });

        objects.forEach(object -> {
            if (object instanceof Bone) {
                if (((Bone) object).isBoneVisible()) {
                    drawTransformObject(g, (TransformObject) object);
                }
            }
        });
    }

    private void drawLayer(Graphics g, Layer layer) {
        if (!layer.isShowSourceImage())
            g.drawImage(layer.getResultBitmap(), scale * (offset.x + (int) layer.getGlobalPosition().x - layer.getRootVectorOrigin().x), scale * (offset.y + (int) layer.getGlobalPosition().y - layer.getRootVectorOrigin().y), scale * layer.getSourceBitmap().getWidth(), scale * layer.getSourceBitmap().getHeight(), null);
        else
            g.drawImage(layer.getSourceBitmap(), scale * (offset.x + (int) layer.getGlobalPosition().x - layer.getRootVectorOrigin().x), scale * (offset.y + (int) layer.getGlobalPosition().y - layer.getRootVectorOrigin().y), scale * layer.getSourceBitmap().getWidth(), scale * layer.getSourceBitmap().getHeight(), null);
    }

    private void drawInterface(Graphics g) {
        Color oldColor = g.getColor();

        g.setColor(Color.gray);

        //Draw ruler
        Point mousePosition = this.getMousePosition();
        if (mousePosition != null) {
            g.drawImage(horizontalRulerImage, mousePosition.x - rulerWidth / 2, 0, rulerWidth, rulerHeight, null);
            g.drawImage(verticalRulerImage, 0, mousePosition.y - rulerHeight / 2, rulerWidth, rulerHeight, null);
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

        g.setColor(oldColor);
    }

    private void drawSelection(Graphics g) {
        SceneObject object = canvasController.getSelection();

        if (object == null)
            return;

        if (object instanceof Layer) {
            drawSelectedLayer(g, (Layer) object);
        }
        if (object instanceof TransformObject transformObject) {
            drawLocalCoordinateOrigin(g, transformObject);
            drawTransformObjects(g, transformObject);
        }
    }

    private void drawSelectedLayer(Graphics g, Layer object) {
        Graphics2D g2d = (Graphics2D) g;

        Color oldColor = g2d.getColor();
        Stroke oldStroke = g2d.getStroke();

        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.5f, new float[]{2f}, 0f));
        g2d.drawRect(scale * (offset.x + (int) object.getGlobalPosition().x - object.getRootVectorOrigin().x), scale * (offset.y + (int) object.getGlobalPosition().y - object.getRootVectorOrigin().y),
                scale * object.getSourceBitmap().getWidth(), scale * object.getSourceBitmap().getHeight());

        g2d.setStroke(oldStroke);
        g2d.setColor(oldColor);
    }

    private void drawTransformObjects(Graphics g, TransformObject transformObject) {
        if (transformObject instanceof Group<?>) {
            ((Group<SceneObject>) transformObject).getChildren().forEach(object -> {
                if (object instanceof Bone)
                    drawTransformObjects(g, (Bone) object);
            });
        }

        drawTransformObject(g, transformObject);
    }

    private void drawTransformObject(Graphics g, TransformObject transformObject) {
        Graphics2D graphics2D = (Graphics2D) g;

        Color oldColor = graphics2D.getColor();
        Stroke oldStroke = graphics2D.getStroke();

        graphics2D.setStroke(new BasicStroke(scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); //TODO magic number

        graphics2D.setColor(xAxisColor);

        //TODO add more adaptivity. You too dump to remember all spots like this
        Point position = new Point((int) transformObject.getGlobalPosition().x, (int) transformObject.getGlobalPosition().y);

        //Vectors
        //rootDirection
        FPoint parentRotationVector = transformObject.calculateParentRotationVector();
        graphics2D.drawLine(scale * (offset.x + position.x), scale * (offset.y + position.y),
                (int) Math.round(scale * (offset.x + position.x + parentRotationVector.x)),
                (int) Math.round(scale * (offset.y + position.y + parentRotationVector.y)));

        //rotation vector
        graphics2D.setColor(directionColor);

        FPoint rotationVector = transformObject.calculateFullRotationVector();
        graphics2D.drawLine(scale * (offset.x + position.x), scale * (offset.y + position.y),
                (int) Math.round(scale * (offset.x + position.x + rotationVector.x)),
                (int) Math.round(scale * (offset.y + position.y + rotationVector.y)));

        graphics2D.setColor(oldColor);
        graphics2D.setStroke(oldStroke);
    }

    private void drawLocalCoordinateOrigin(Graphics g, TransformObject object) {
        Color oldColor = g.getColor();

        g.setColor(xAxisColor);
        g.fillOval((int) Math.round(scale * (offset.x + object.getGlobalPosition().x) - (scale * 3 / 2)), (int) Math.round(scale * (offset.y + object.getGlobalPosition().y) - (scale * 3 / 2)), scale * 3, scale * 3); //TODO magic

        g.setColor(oldColor);
    }

    /**
     * Convert mouse screen coordinates to canvas coordinates
     *
     * @param screen mouse coordinates on screen
     * @return Point
     */
    private Point screenToCanvas(Point screen) {
        screen.x = screen.x / scale - offset.x;
        screen.y = screen.y / scale - offset.y;

        return screen;
    }

    private void linkKeyShortcuts() { //TODO replace to KeyBinds
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

    private void createMouseListeners() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toolsManagerController.startUse(screenToCanvas(e.getPoint()));
                if (SwingUtilities.isLeftMouseButton(e))
                    toolsManagerController.primaryUseTool(getGraphics(), screenToCanvas(e.getPoint()));
                if (SwingUtilities.isRightMouseButton(e))
                    toolsManagerController.secondaryUseTool(getGraphics(), screenToCanvas(e.getPoint()));
                toolsManagerController.endUse(screenToCanvas(e.getPoint()));

                uiManager.updateInspector(); //TODO optimization
                uiManager.updateTree();

                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    isScrollPressed = true;
                }

                toolsManagerController.startUse(screenToCanvas(e.getPoint()));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON2) {
                    isScrollPressed = false;
                }

                toolsManagerController.endUse(screenToCanvas(e.getPoint()));

                uiManager.updateInspector();
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
                toolsManagerController.endUse(screenToCanvas(e.getPoint()));
            }
        });

        this.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scale = Math.max(scaleMin, scale - e.getWheelRotation());
                scale = Math.min(scaleMax, scale);
                repaint(); //TODO optimization
            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            Point oldPos = new Point(0, 0);
            final Point direction = new Point(0, 0);

            int pixelsPassed = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                calculateDirection(e.getPoint());

                //LOCKED! CAN NOT BE IN TOOLS! IMPORTANT FUNCTION!
                pixelsPassed += 1;

                if (pixelsPassed >= scale) {
                    if (isScrollPressed) {
                        offset.x += direction.x;
                        offset.y += direction.y;
                    }
                    pixelsPassed = 0;
                    repaint();
                }

                if (isScrollPressed)
                    return;

                if (SwingUtilities.isLeftMouseButton(e))
                    toolsManagerController.primaryUseTool(getGraphics(), screenToCanvas(e.getPoint()));
                if (SwingUtilities.isRightMouseButton(e))
                    toolsManagerController.secondaryUseTool(getGraphics(), screenToCanvas(e.getPoint()));
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                calculateDirection(e.getPoint());

                repaint(new Rectangle(0, 0, rulerWidth, getHeight()));
                repaint(new Rectangle(0, 0, getWidth(), rulerHeight));
            }

            private void calculateDirection(Point pos) {
                direction.x = pos.x - oldPos.x;
                direction.y = pos.y - oldPos.y;

                oldPos = pos;
            }
        });
    }
}
