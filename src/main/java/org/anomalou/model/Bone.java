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

public class Bone extends Layer{
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

    public Bone(){
        super();

        name = "NewBone";
        transformBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        rootBasePosition = new Point(0, 0);
        rootDirectionPosition = new Point(0, 0);
        direction = new FPoint(0, 0);
        isBoneVisible = false;
    }

    public void setDirection(FPoint direction) {
        this.direction = direction;
    }

    // its WORKS!
    //get angle between direction and rootDirectionPosition vectors, as it began in (0, 0)
    public double getAngle(){
        FPoint normalizedRootDirectionVector = getNormalizedRootVector();

        double side = (direction.x) * (normalizedRootDirectionVector.y) - (direction.y) * (normalizedRootDirectionVector.x);

        side = side <= 0 ? 1 : -1;

        double cos = (direction.x * normalizedRootDirectionVector.x + direction.y * normalizedRootDirectionVector.y) /
                     (Math.sqrt(Math.pow(direction.x, 2) + Math.pow(direction.y, 2)) * Math.sqrt(Math.pow(normalizedRootDirectionVector.x, 2) + Math.pow(normalizedRootDirectionVector.y, 2)));
        cos = Math.abs(cos) > 1d ? 1d : cos;

        Double resultAngle =  Math.acos(cos) * side;
        if(resultAngle.isNaN())
            return 0;
        else
            return resultAngle;
    }

    public FPoint getNormalizedRootVector(){
        return new FPoint(rootDirectionPosition.x - rootBasePosition.x, (rootDirectionPosition.y - rootBasePosition.y) * -1);
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
