package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Coordinates;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;

public class Bone extends Layer{
    @Getter
    @Setter
    private transient BufferedImage transformBitmap;
    /**
     * Source of normal vector of the bone.
     */
    @Editable
    @Coordinates(type = Integer.class)
    @Getter
    @Setter
    private Point rootVectorOrigin;
    /**
     * Direction of normal vector of the bone.
     */
    @Editable
    @Coordinates(type = Integer.class)
    @Getter
    @Setter
    private Point rootVectorDirection;
    /**
     * Angle of the bone in radian.
     */
    @Editable
    @Value
    @Getter
    @Setter
    private Double rotationAngle;
    /**
     * Sum of angles of all parents of the bone in radian.
     */
    @Editable
    @Value
    @Getter
    @Setter
    private Double parentRotationAngle;
    /**
     * Visibility of the bone rig.
     */
    @Editable
    @Value
    @Getter
    @Setter
    private boolean isBoneVisible;

    public Bone(){
        super();

        name = "NewBone";
        transformBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        rootVectorOrigin = new Point(0, 0);
        rootVectorDirection = new Point(0, 0);
        rotationAngle = 0d;
        parentRotationAngle = 0d;
        isBoneVisible = false;
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Override
    public String toString(){
        return String.format("%s(%.2f deg)", name, Math.toDegrees(rotationAngle));
    }

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
