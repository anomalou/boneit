package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Layer extends TransformObject { //a base class for layers or bones
    /**
     * Just image of the layer, its shape MUST be 1x1 or more! <br>
     * Do not try to transform this image, its just raw pixel information for future transformation!
     */
    @Getter
    @Setter
    protected transient BufferedImage sourceBitmap;
    /**
     * Result image after all transformations.
     */
    @Getter
    @Setter
    private transient BufferedImage resultBitmap;
    /**
     * Visibility of the bitmap.
     */
    @Editable(name = "Set visibility", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    protected boolean isVisible;


    public Layer(){
        super();

        name = "NewLayer";
        sourceBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        resultBitmap = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        isVisible = true;
        logger.fine(String.format("Entity (%s) created!", getUuid().toString()));
    }

//    public boolean isRoot(){
//        return parent == null;
//    }
//
//    public boolean isLeaf(){
//        return children.isEmpty();
//    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(sourceBitmap, "png", out);
        ImageIO.write(resultBitmap, "png", out);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
        in.defaultReadObject();
        sourceBitmap = ImageIO.read(in);
        resultBitmap = ImageIO.read(in);
    }
}
