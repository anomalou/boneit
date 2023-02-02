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
import java.util.ArrayList;
import java.util.UUID;

public class Bone extends Layer{ //is a bone, like a rig in blender
    @Getter
    @Setter
    private transient BufferedImage transformBitmap;
    @Getter
    @Setter
    private Point rootBasePosition; //position of root of the bone on its layer, not the same as "position"
    @Getter
    @Setter
    private Point rootDirectionPosition; //position of the direction of the bone on its layer
    @Getter
    @Setter
    private FPoint direction; //direction in unit circle
    /**
     * Visibility of the bone rig.
     */
    @Getter
    @Setter
    private boolean isBoneVisible;
    @Getter
    private ArrayList<UUID> children; //TODO now can be only bones, not else!

    public Bone(){
        super();

        name = "NewBone";
        transformBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        rootBasePosition = new Point(0, 0);
        rootDirectionPosition = new Point(0, 0);
        direction = new FPoint(0, 0);
        isBoneVisible = false;
        children = new ArrayList<>();
    }

    // its WORKS!
    //get angle between direction and rootDirectionPosition vectors, as it began in (0, 0)
    public double getAngle(){
        FPoint normalizedRootDirectionVector = new FPoint(rootDirectionPosition.x - rootBasePosition.x, (rootDirectionPosition.y - rootBasePosition.y) * -1);

        double side = (direction.x) * (normalizedRootDirectionVector.y) - (direction.y) * (normalizedRootDirectionVector.x);

        side = side <= 0 ? -1 : 1;

        double cos = (direction.x * normalizedRootDirectionVector.x + direction.y * normalizedRootDirectionVector.y) /
                     (Math.sqrt(Math.pow(direction.x, 2) + Math.pow(direction.y, 2)) * Math.sqrt(Math.pow(normalizedRootDirectionVector.x, 2) + Math.pow(normalizedRootDirectionVector.y, 2)));
        cos = Math.abs(cos) > 1d ? 1d : cos;

        return Math.acos(cos) * side;
    }

    //TODO maybe it should be in controller. I will try move it later
    //TODO NOT WORKS NOW
//    public void applyPosition(){
//        for(Bone child : children){
//            if(child instanceof Bone){
//                FPoint diff = new FPoint(direction.x - child.position.x, direction.y - child.position.y);
//                child.position = new Point((int) (child.position.x + diff.x), (int) (child.position.y + diff.y));
//                FPoint oldDirection = child.direction;
//                child.direction = new FPoint(oldDirection.x + diff.x, oldDirection.y + diff.y);
//                child.applyPosition();
//            }
//        }
//        logger.info(String.format("Positions for bone (%s) is applied", getUuid().toString()));
//    }

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

    //TODO move it to controller IMPORTANT
    //TODO maybe, in future, if i have time, make transformBitmap adaptation to rotation of the image
    public void applyRotation(){
        if(transformBitmap.getWidth() != baseBitmap.getWidth() || transformBitmap.getHeight() != baseBitmap.getHeight())
            transformBitmap = new BufferedImage(baseBitmap.getWidth(), baseBitmap.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transformBitmap.createGraphics();
        g2d.rotate(getAngle(), rootBasePosition.x, rootBasePosition.y);
        g2d.drawImage(baseBitmap, null, 0, 0);
        g2d.dispose();
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(baseBitmap, "png", out);
        ImageIO.write(transformBitmap, "png", out); //TODO NEED MORE TEST! ITS MAY NOT BE WORK
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        baseBitmap = ImageIO.read(in);
        transformBitmap = ImageIO.read(in); //TODO NEED MORE TEST! ITS MAY NOT BE WORK
    }
}
