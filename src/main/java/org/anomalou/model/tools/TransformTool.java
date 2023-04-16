package org.anomalou.model.tools;

import org.anomalou.model.Canvas;
import org.anomalou.model.FPoint;
import org.anomalou.model.scene.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

/**
 * Tools for accept rotation and reposition transformations
 */
public class TransformTool implements Tool {
    private final String name = "Transform";
    private Image icon;

    private final org.anomalou.model.Canvas canvas;

    private Point oldPosition;

    public TransformTool(Canvas canvas){
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
        return new Rectangle(position.x - 5, position.y - 5, 10, 10);
    }

    @Override
    public void primaryUse(Graphics g, Point position) {
        repose(position);
    }

    @Override
    public void secondaryUse(Graphics g, Point position) {
        rotate(position);
    }

    private void rotate(Point direction){
        TransformObject object = (TransformObject) canvas.getSelection();
        if(object == null)
            return;

        FPoint rotation = new FPoint(direction.x, direction.y);
        rotation.x -= (int)object.getGlobalPosition().x;
        rotation.y -= (int)object.getGlobalPosition().y;
        rotation = object.calculateRotationVectorForAngle(rotation, object.getParentRotationAngle());
        object.setRotationAngle(object.calculateRotationAngle(rotation));
        object.applyTransformation();
    }

    private void repose(Point position){
        SceneObject object = canvas.getSelection();
        if(object == null)
            return;

        Point dragDirection = normalizeDirection(new Point(position.x - oldPosition.x, -(position.y - oldPosition.y))); //TODO oldposition make impact into calculation when you unpress LMB
        oldPosition = position;

        if(object instanceof TransformObject){
            FPoint rotatedDragDirection = ((TransformObject) object).calculateRotationVectorForAngle(dragDirection, -((TransformObject) object).getParentRotationAngle());
            object.setLocalPosition(new Point((int) Math.round(object.getLocalPosition().x + rotatedDragDirection.x), (int) Math.round(object.getLocalPosition().y - rotatedDragDirection.y)));
        }

        if(object instanceof TransformObject){
            ((TransformObject) object).applyTransformation();
        }
    }

    /**
     * Normalize vector point to 0..1
     * @param point vector to normalize
     * @return Normalized vector
     */
    private Point normalizeDirection(Point point){
        Point result = new Point(point);

        if(point.x > 0)
            result.x = 1;
        if(point.x < 0)
            result.x = -1;
        if(point.y > 0)
            result.y = 1;
        if(point.y < 0)
            result.y = -1;

        return result;
    }

    private void loadResources(){
        try{
            icon = ImageIO.read(Objects.requireNonNull(getClass().getResource("transform.png")));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
