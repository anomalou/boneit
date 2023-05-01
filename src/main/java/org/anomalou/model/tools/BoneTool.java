package org.anomalou.model.tools;

import org.anomalou.model.Canvas;
import org.anomalou.model.scene.SceneObject;
import org.anomalou.model.scene.TransformObject;

import javax.imageio.ImageIO;
import javax.sql.PooledConnection;
import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class BoneTool implements Tool{
    private String name;
    private Image icon;

    private Canvas canvas;

    private Point oldPosition;

    public BoneTool(Canvas canvas){
        name = "Bone editor";

        this.canvas = canvas;
        oldPosition = new Point();

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
        SceneObject sceneObject = canvas.getSelection();

        if(sceneObject instanceof TransformObject transformObject){
            Point direction = new Point(oldPosition.x - position.x, oldPosition.y - position.y);

            Point originPosition = transformObject.getRootVectorOrigin();
            transformObject.setRootVectorOrigin(new Point(originPosition.x + direction.x, originPosition.y + direction.y));
            oldPosition = position;
        }
    }

    @Override
    public void secondaryUse(Graphics g, Point position) {
        SceneObject sceneObject = canvas.getSelection();

        if(sceneObject instanceof TransformObject transformObject){
            transformObject.setRootVectorDirection(calculatePoint(position));
        }
    }

    @Override
    public void startUse(Point position) {
        oldPosition = position;
    }

    @Override
    public void endUse(Point position) {
        oldPosition = position;
    }

    private Point calculatePoint(Point point){
        SceneObject sceneObject = canvas.getSelection();

        if(sceneObject instanceof TransformObject transformObject){
            point = new Point(point.x - (int) transformObject.getGlobalPosition().x + transformObject.getRootVectorOrigin().x,
                    point.y - (int) transformObject.getGlobalPosition().y + transformObject.getRootVectorOrigin().y);

            return point;
        }

        return new Point();
    }

    private void loadResources() {
        try {
            icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("bone_edit.png")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
