package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;

public class Bone extends Layer{ //is a bone, like a rig in blender
    @Getter
    @Setter
    private transient BufferedImage transformBitmap;
    @Getter
    @Setter
    private Point rootPosition; //position of root of the bone on its layer, not the same as "position"
    @Getter
    @Setter
    private FPoint directionPosition; //position of direction of the bone on workspace

    public Bone(){
        super();

        name = "NewBone";
        transformBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        rootPosition = new Point(0, 0);
        directionPosition = new FPoint(0, 0);
    }

    public double getAngle(){
        FPoint normalizedVector = new FPoint(directionPosition.x - position.x - rootPosition.x, directionPosition.y - position.y - rootPosition.y);

        double cos = normalizedVector.x / (Math.sqrt(1) * Math.sqrt(Math.pow(normalizedVector.x, 2) + Math.pow(normalizedVector.y, 2)));
        return -Math.acos(cos);
    }

    //maybe it should be in controller. I will try move it later
    public void applyPosition(){
        for(Layer child : children){
            if(child instanceof Bone){
                FPoint diff = new FPoint(directionPosition.x - child.position.x, directionPosition.y - child.position.y);
                child.position = new Point((int) (child.position.x + diff.x), (int) (child.position.y + diff.y));
                FPoint oldDirection = ((Bone)child).directionPosition;
                ((Bone)child).directionPosition = new FPoint(oldDirection.x + diff.x, oldDirection.y + diff.y);
                ((Bone)child).applyPosition();
            }
        }
        logger.info(String.format("Positions for bone %s(%s) is applied", getName(), getUuid().toString()));
    }

    @Override
    public void reshape(int w, int h){
        super.reshape(w, h);

        if(transformBitmap.getWidth() == 0 || transformBitmap.getHeight() == 0)
            transformBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        else{
            Image tmp = transformBitmap.getSubimage(0, 0, transformBitmap.getWidth(), transformBitmap.getHeight());
            BufferedImage nBaseBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = nBaseBitmap.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            transformBitmap = nBaseBitmap;
        }

        applyRotation();
    }

    public void applyRotation(){
        if(transformBitmap.getWidth() != baseBitmap.getWidth() || transformBitmap.getHeight() != baseBitmap.getHeight())
            transformBitmap = new BufferedImage(baseBitmap.getWidth(), baseBitmap.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transformBitmap.createGraphics();
        g2d.rotate(getAngle(), rootPosition.x, rootPosition.y);
        g2d.drawImage(baseBitmap, null, 0, 0);
        g2d.dispose();
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(baseBitmap, "png", out);
        ImageIO.write(transformBitmap, "png", out); //NEED MORE TEST! ITS MAY NOT BE WORK
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        baseBitmap = ImageIO.read(in);
        transformBitmap = ImageIO.read(in); //NEED MORE TEST! ITS MAY NOT BE WORK
    }
}
