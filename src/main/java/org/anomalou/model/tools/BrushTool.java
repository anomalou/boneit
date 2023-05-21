package org.anomalou.model.tools;

import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;
import org.anomalou.model.Canvas;
import org.anomalou.model.FPoint;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;
import org.anomalou.view.CanvasPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

public class BrushTool implements Tool {
    private String name;
    private Image icon;

    private Canvas canvas;
    private Palette palette;
    private Point oldPosition;
    private boolean useStatus;

    @Editable(name = "Brush width", description = "Set current brush width", editorType = EditorType.TEXT_FIELD)
    private Integer brushWidth;

    public BrushTool(Canvas canvas, Palette palette) {
        name = "Brush";
        this.canvas = canvas;
        this.palette = palette;
        oldPosition = new Point();
        useStatus = false;

        brushWidth = 1;

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
        return null;
    }

    @Override
    public void primaryUse(Graphics g, Point position) {
        draw(palette.getForegroundColor(), position);
    }

    @Override
    public void secondaryUse(Graphics g, Point position) {
        draw(palette.getBackgroundColor(), position);
    }

    @Override
    public void startUse(Point position) {
        SceneObject sceneObject = canvas.getSelection();

        if(sceneObject instanceof Layer layer){
            oldPosition = toLocalPosition(layer, position);
            useStatus = true;
        }
    }

    @Override
    public void endUse(Point position) {
        SceneObject sceneObject = canvas.getSelection();

        if(sceneObject instanceof Layer layer){
            oldPosition = toLocalPosition(layer, position);
            useStatus = false;
        }
    }

    private void loadResources() {
        try {
            icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("brush.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void draw(Color color, Point position) {
        SceneObject sceneObject = canvas.getSelection();

        if (sceneObject instanceof Layer) {


            Layer layer = (Layer) sceneObject;
            position = toLocalPosition(layer, position);

            Graphics2D g2d = layer.getSourceBitmap().createGraphics();
            Color oldColor = g2d.getColor();
            Stroke oldStroke = g2d.getStroke();

            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(brushWidth));
            g2d.drawLine(oldPosition.x, oldPosition.y, position.x, position.y);
            g2d.setColor(oldColor);

            oldPosition = position;

//            FPoint rotatedPoint = layer.calculateRotationVectorForAngle(position, -layer.getFullRotationAngle());
//            rotatedPoint = new FPoint(rotatedPoint.x + layer.getRootVectorOrigin().x, (rotatedPoint.y) + layer.getRootVectorOrigin().y);
//            position = new Point((int) Math.round((int) rotatedPoint.x + layer.getRootVectorOrigin().x), (int) Math.round((int) (rotatedPoint.y) + layer.getRootVectorOrigin().y));
//            System.out.printf("%s\n", rotatedPoint);

//            layer.getSourceBitmap().setRGB((int) position.x, (int) position.y, color.getRGB()); //TODO coordinates out of bounds

            layer.applyTransformation();
        }
    }

    private Point toLocalPosition(Layer layer, Point position){
        position = new Point(position.x - (int) layer.getGlobalPosition().x + layer.getRootVectorOrigin().x,
                position.y - (int) layer.getGlobalPosition().y + layer.getRootVectorOrigin().y);

        return position;
    }
}
