package org.anomalou.model.scene;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.annotation.Editable;
import org.anomalou.annotation.EditorType;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Default scene object that can have image. Applies all transformation to image
 */
public class Layer extends TransformObject { //a base class for layers or bones
    /**
     * Just image of the layer, its shape MUST be 1x1 or larger! <br>
     * Do not try to transform this image, its just raw pixel information for future transformation!
     */
    @Getter
    @Setter
    protected transient BufferedImage sourceBitmap;
    /**
     * Result image after all transformations.
     */
    @Editable(name = "Result preview", description = "Result image after all transformations", editorType = EditorType.IMAGE_PREVIEW)
    @Getter
    @Setter
    private transient BufferedImage resultBitmap;
    /**
     * Visibility of the bitmap.
     */
    @Editable(name = "Set visibility", description = "Visibility of the layer on scene", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    protected boolean isVisible;

    @Editable(name = "Show source image", description = "Show source image over its rotated variant", editorType = EditorType.CHECK_BOX)
    @Getter
    @Setter
    protected boolean showSourceImage;

    public Layer() {
        this(1, 1);
    }

    public Layer(int width, int height) {
        super();

        name = "NewLayer";
        sourceBitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        resultBitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        isVisible = true;
        showSourceImage = false;

//        logger.fine(String.format("Entity (%s) created!", getUuid().toString())); //TODO
    }

    public Layer(BufferedImage image){
        super();

        name = "NewLayer";
        sourceBitmap = image;
        resultBitmap = image;
        isVisible = true;
//        logger.fine(String.format("Entity (%s) created!", getUuid().toString())); //TODO
    }

    @Override
    public void applyTransformation() {
        setResultBitmap(new BufferedImage(getSourceBitmap().getWidth(), getSourceBitmap().getHeight(), BufferedImage.TYPE_INT_ARGB));
        Graphics2D g2d = getResultBitmap().createGraphics();
        double angle = (rotationAngle + parentRotationAngle) * -1;
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.rotate(angle, getRootVectorOrigin().x, getRootVectorOrigin().y);
        g2d.drawImage(getSourceBitmap(), null, 0, 0);
        g2d.dispose();

//        logger.fine(String.format("Bone %s rotated to %f angle!", getUuid(), -angle)); //TODO
    }

    @Override
    public boolean isInBounds(Point point) {
        return isInRectangle(point, new Rectangle((int) Math.round(getGlobalPosition().x) - rootVectorOrigin.x, (int) Math.round(getGlobalPosition().y) - rootVectorOrigin.y, sourceBitmap.getWidth(), sourceBitmap.getHeight()));
    }

    //------ OVERRIDES SERIALIZATION METHODS FOR IMAGES

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(sourceBitmap, "png", out);
        ImageIO.write(resultBitmap, "png", out);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        sourceBitmap = ImageIO.read(in);
        while (resultBitmap == null)
            resultBitmap = ImageIO.read(in);
    }
}
