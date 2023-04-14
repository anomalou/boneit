package org.anomalou.model;

import jdk.jshell.spi.ExecutionControl;
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
            Point objectPos = new Point(object.getGlobalPosition().x, object.getGlobalPosition().y);
            FPoint rotation = new FPoint(position.x, position.y);
            rotation.x -= objectPos.x;
            rotation.y -= objectPos.y;
            rotation = object.calculateRotationVectorForAngle(rotation, object.getParentRotationAngle());
            object.setRotationAngle(object.calculateRotationAngle(rotation));
//            canvas.applyBoneRotation(object, object.getRotationAngle() + object.getParentRotationAngle());
            try {
                object.applyTransformation();
            }catch (ExecutionControl.NotImplementedException ex){
                ex.printStackTrace();
            }
        }
        if(isMoveMode){
            //TODO need fix
            Point dragDirection = new Point(canvas.getSelection().getLocalPosition().x + position.x - oldPosition.x, canvas.getSelection().getLocalPosition().y + position.y - oldPosition.y);
            oldPosition = position;
            if(canvas.getSelection() instanceof Groupable<?>){
                if(!((Groupable<SceneObject>) canvas.getSelection()).isRoot()) //TODO <<< here convert child position as parent was main coordinate axis
                    return;
            }

//            canvas.getSelection().setPosition(dragDirection);
//            if(canvas.getSelection().getClass().equals(Bone.class)){
//                    canvas.applyPosition((Bone) canvas.getSelection());
//            }
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
