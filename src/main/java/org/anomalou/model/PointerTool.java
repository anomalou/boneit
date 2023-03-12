package org.anomalou.model;

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
            Bone bone = (Bone) canvas.getSelection();
            Point objectPos = bone.getPosition();
            FPoint rotation = new FPoint(position.x, position.y);
            rotation.x -= objectPos.x;
            rotation.y -= objectPos.y;
            rotation = canvas.calculateRotatedVector(rotation, bone.getParentRotationAngle());
            bone.setRotationAngle(canvas.calculateRotationAngleFor(bone, rotation));
            canvas.applyBoneRotation(bone, bone.getRotationAngle() + bone.getParentRotationAngle());
            canvas.applyBoneTransform(bone, bone.getRotationAngle() + bone.getParentRotationAngle());
        }
        if(isMoveMode){
            Point dragDirection = new Point(canvas.getSelection().getPosition().x + position.x - oldPosition.x, canvas.getSelection().getPosition().y + position.y - oldPosition.y);
            oldPosition = position;
            if(canvas.getSelection().getParent() != null)
                return;

            canvas.getSelection().setPosition(dragDirection);
            if(canvas.getSelection().getClass().equals(Bone.class)){
                    canvas.applyBoneTransform((Bone) canvas.getSelection(), ((Bone) canvas.getSelection()).getRotationAngle() + ((Bone) canvas.getSelection()).getParentRotationAngle());
            }
        }
    }

    /**
     * Check all objects in cache, that can be in mouse pointer hit area.
     * @param clickPosition mouse click position in screen coordinates (raw position)
     */
    private void select(Point clickPosition){
        boolean selected = false;
        for(Layer layer : canvas.sort()){
            if(isClickInBound(layer, clickPosition)){
                canvas.setSelection(layer.getUuid());
                selected = true;
            }
        }

        if(!selected)
            canvas.setSelection(null);
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
            return clickPosition.y >= position.y && clickPosition.y < (position.y + height);
        }

        return false;
    }
}
