package org.anomalou.controller;

import jdk.jshell.spi.ExecutionControl;
import org.anomalou.model.*;
import org.anomalou.model.Canvas;
import org.anomalou.model.scene.Bone;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;
import org.anomalou.model.scene.TransformObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.UUID;

public class CanvasController extends Controller {
    private final Canvas canvas;

    public CanvasController(Canvas canvas) {
        this.canvas = canvas;
    }

    public SceneObject getSelection() {
        return canvas.getSelection();
    }

    public void setSelection(UUID objectID) {
        canvas.setSelection(objectID);
    }

    public void registerObject(SceneObject object) {
        canvas.registerObject(object);
    }

    public void unregisterObject(SceneObject object) {
        canvas.unregisterObject(object);
    }

    public ArrayList<SceneObject> sort() {
        return canvas.sort();
    }

    public int getWidth() {
        return canvas.getWidth();
    }

    public int getHeight() {
        return canvas.getHeight();
    }

    @Deprecated
    public void reshape(Layer layer, int w, int h) {
        if (layer.getSourceBitmap().getWidth() == 1 || layer.getSourceBitmap().getHeight() == 1)
            layer.setSourceBitmap(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        else {
            Image tmp = layer.getSourceBitmap().getSubimage(0, 0, layer.getSourceBitmap().getWidth(), layer.getSourceBitmap().getHeight());
            BufferedImage nBaseBitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = nBaseBitmap.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();
            layer.setSourceBitmap(nBaseBitmap);
        }

        if (!layer.getClass().equals(Bone.class))
            return;
    }

    public void addObject(SceneObject parent, SceneObject object) {
        canvas.addObject(parent, object);
    }

    public SceneObject getObject(UUID uuid) {
        try {
            return canvas.getObject(uuid);
        } catch (Exception exception) {
            logger.severe(String.format("Object with UUID %s not exist! Error:\n%s", uuid, exception.getMessage()));
        }

        return null; //TODO check, controller can return nulls?
    }

    public ArrayList<SceneObject> getLayersHierarchy() {
        return canvas.getSceneObjects();
    }

    public void updateObjects() {
        canvas.updateObjects();
    }
}
