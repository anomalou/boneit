package org.anomalou.model;

import java.awt.*;
import java.awt.event.MouseEvent;

public class PointerTool implements Tool{
    private final String name = "Pointer";

    private Canvas canvas;

    public PointerTool(Canvas canvas){
        this.canvas = canvas;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Rectangle drawInterface(Graphics g, Point position) {
        return new Rectangle(position.x - 5, position.y - 5, 10, 10);
    }

    @Override
    public void click(Graphics g, Point position, int button) {
        select(position);
    }

    @Override
    public void drag(Graphics g, Point position, int button) {
        if(button == MouseEvent.BUTTON3){ //TODO rewrite
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
    }

    /**
     * Check all objects in cache, that can be in mouse pointer hit area.
     * @param clickPosition mouse click position in screen coordinates (raw position)
     */
    private void select(Point clickPosition){
        canvas.sort().forEach(layer -> {
            if(isClickInBound(layer, clickPosition))
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
}
