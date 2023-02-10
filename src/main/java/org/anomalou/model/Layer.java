package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class Layer implements Serializable, Comparable<Layer> { //a base class for layers or bones
    protected transient final Logger logger = Logger.getLogger(Layer.class.getName());

    /**
     * Unique ID of the layer object
     */
    @Getter
    protected final UUID uuid = UUID.randomUUID();
    /**
     * Name of the layer. Human friendly
     */
    @Getter
    @Setter
    protected String name;
    /**
     * Just image of the layer, its shape MUST be 1x1 or more! <br>
     * Do not try to transform this image, its just raw pixel information for future transformation!
     */
    @Getter
    @Setter
    protected transient BufferedImage baseBitmap;
    /**
     * Offset on canvas. Bones will ignore this parameter.
     */
    @Getter
    @Setter
    protected Point position;
    /**
     * Priority of the layer in render queue. <br>
     * Big number of priority, means early render, also work oppositely
     */
    @Getter
    @Setter
    protected Integer priority;
    /**
     * Visibility og the bitmap.
     */
    @Getter
    @Setter
    protected boolean isVisible;
    /**
     * Children of the layer, follows for its parent
     */
    @Getter
    private ArrayList<UUID> children;

    public Layer(){
        name = "NewLayer";
        position = new Point(0, 0);
        baseBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        logger.fine(String.format("Entity (%s) created!", getUuid().toString()));
        priority = 0;
        isVisible = true;
        children = new ArrayList<>();
    }

    public void reshape(int w, int h){
        if(baseBitmap.getWidth() == 0 || baseBitmap.getHeight() == 0)
            baseBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        else{
            Image tmp = baseBitmap.getSubimage(0, 0, baseBitmap.getWidth(), baseBitmap.getHeight());
            BufferedImage nBaseBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = nBaseBitmap.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            baseBitmap = nBaseBitmap;
        }
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        ImageIO.write(baseBitmap, "png", out);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        baseBitmap = ImageIO.read(in);
    }

    @Override
    public int compareTo(Layer o) {
        int result = this.priority.compareTo(o.priority);
        return result;
    }
}
