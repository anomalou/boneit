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

public class Layer implements Serializable { //a base class for layers or bones
    protected transient final Logger logger = Logger.getLogger(Layer.class.getName());

    @Getter
    @Setter
    protected String name;
    @Getter
    protected final UUID uuid = UUID.randomUUID();
    @Getter
    @Setter
    protected transient BufferedImage baseBitmap;
    @Getter
    @Setter
    protected Point position; //position on workspace
    @Getter
    protected ArrayList<Layer> children;

    public Layer(){
        name = "NewLayer";
        position = new Point(0, 0);
        baseBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        children = new ArrayList<>();
        logger.info(String.format("Entity %s(%s) created!", name, getUuid().toString()));
    }

    //maybe is HUETA and do not work
    public Layer findByName(String name) {
        for(Layer child : children){
            if(child.getName().equals(name)){
                logger.info(String.format("Child with name \"%s\"(%s) found!", name, getUuid().toString()));
                return child;
            }
            Layer outChild = child.findByName(name);
            if(outChild.equals(null))
                logger.info(String.format("Layer by name \"%s\"(%s) do not have child with name \"%s\"", child.getName(), child.getUuid().toString(), name));
            else
                return outChild;
        }
        return null;
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
}
