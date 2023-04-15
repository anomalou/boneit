package org.anomalou.model;

import org.anomalou.model.scene.*;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PointerTool implements Tool{
    private final String name = "Pointer";

    private final Canvas canvas;

    private boolean isMoveMode;
    private boolean isRotateMode;

    private Point oldPosition;

    public PointerTool(Canvas canvas){
        this.canvas = canvas;

        isMoveMode = false;
        isRotateMode = false;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rectangle drawInterface(Graphics g, Point position) {
        return new Rectangle(position.x - 5, position.y - 5, 10, 10);
    }

    public void press(Graphics g, Point position, int button, boolean released){
        if(button == MouseEvent.BUTTON3)
            isRotateMode = !released;
        if(button == MouseEvent.BUTTON1){
            isMoveMode = !released;
            oldPosition = position;
        }
    }

    @Override
    public void click(Graphics g, Point position, int button) {
        if(button == MouseEvent.BUTTON1) {
            select(position);
        }
    }

    @Override
    public void drag(Graphics g, Point position, int button) {
        if(isRotateMode){ //TODO rewrite
            TransformObject object = (TransformObject) canvas.getSelection();
            Point objectPos = new Point((int)object.getGlobalPosition().x, (int)object.getGlobalPosition().y);
            FPoint rotation = new FPoint(position.x, position.y);
            rotation.x -= objectPos.x;
            rotation.y -= objectPos.y;
            rotation = object.calculateRotationVectorForAngle(rotation, object.getParentRotationAngle());
            object.setRotationAngle(object.calculateRotationAngle(rotation));
            object.applyTransformation();
        }
        if(isMoveMode){ //TODO fix
            SceneObject selectedObject = canvas.getSelection();

            Point dragDirection = new Point(selectedObject.getLocalPosition().x + position.x - oldPosition.x, selectedObject.getLocalPosition().y + position.y - oldPosition.y);
            oldPosition = position;

            if(selectedObject instanceof TransformObject){
                FPoint rotatedDragDirection = ((TransformObject) selectedObject).calculateRotationVectorForAngle(dragDirection, ((TransformObject) selectedObject).getParentRotationAngle());
                selectedObject.setLocalPosition(new Point((int)rotatedDragDirection.x, (int)rotatedDragDirection.y));
                System.out.printf("%s\n", rotatedDragDirection);
            }else{
                selectedObject.setLocalPosition(new Point(dragDirection.x, dragDirection.y));
            }

            if(selectedObject instanceof TransformObject){
                ((TransformObject) selectedObject).applyTransformation();
            }
        }
    }

    /**
     * Check all objects in cache, that can be in mouse pointer hit area.
     * @param clickPosition mouse click position in screen coordinates (raw position)
     */
    private void select(Point clickPosition){
        boolean selected = false;
        for(SceneObject object : canvas.sort()){
            if(object instanceof TransformObject){
                if(((TransformObject) object).isInBounds(clickPosition)){
                    canvas.setSelection(object.getUuid());
                    selected = true;
                }
            }
        }

        if(!selected)
            canvas.setSelection(null);
    }
}
