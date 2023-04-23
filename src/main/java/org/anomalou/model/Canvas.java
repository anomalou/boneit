package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.model.scene.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Object that compose all scene objects to solid image
 */
public class Canvas implements Serializable {
//    private transient final Logger logger = Logger.getLogger(Canvas.class.getName()); //TODO

    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
    @Getter
    private final ArrayList<SceneObject> sceneObjects;
    /**
     * Cache that stores every object on scene. This allows get needed object faster.
     */
    @Getter
    private final ObjectCache objectCache;
    @Setter
    private UUID selection; //TODO not safe, invent something else

    public Canvas() {
        this(1, 1);
    }

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        sceneObjects = new ArrayList<>();
        objectCache = new ObjectCache();
        selection = null;

//        logger.fine("Workspace is created!");//TODO
    }

    public void reshape(int width, int height) {
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    public SceneObject getSelection() {
        return objectCache.getObjects().get(selection);
    }

    public void registerObject(SceneObject object) {
        try {
            getObjectCache().registerObject(object.getUuid(), object);

//            logger.fine(String.format("Object %s created!", object.getUuid()));//TODO
        } catch (NullPointerException ex) {
//            logger.warning(ex.getMessage());//TODO
        }
    }

    public void unregisterObject(SceneObject object) {
        try {
            getObjectCache().unregister(object.getUuid());
            if (object instanceof Node<?>) {
                if (!((Node<SceneObject>) object).isRoot()) {
                    SceneObject parent = ((Node<SceneObject>) object).getParent();
                    if (parent instanceof Group<?>) {
                        ((Group<SceneObject>) parent).removeObject(object);
                    }
                } else {
                    sceneObjects.remove(object);
                }
            }
        } catch (NullPointerException ex) {
//            logger.warning(ex.getMessage());//TODO
        }
    }

    public void addObject(SceneObject parent, SceneObject object) {
        if(parent == null){
            sceneObjects.add(object);
        }else{
            if(parent instanceof Group<?>){
                ((Group<SceneObject>) parent).addObject(object);
            }else{
                sceneObjects.add(object);
            }
        }
    }

    public SceneObject getObject(UUID uuid) {
        return objectCache.getObjects().get(uuid);
    }

    /**
     * Get all object on scene sorted by draw priority as list
     *
     * @return ArrayList
     */
    public ArrayList<SceneObject> sort() {
        return _sort(sceneObjects);
    }

    private ArrayList<SceneObject> _sort(ArrayList<SceneObject> iArray) {
        ArrayList<SceneObject> oArray = new ArrayList<>();
        ArrayList<SceneObject> tempArray = new ArrayList<>(iArray);

        Collections.sort(tempArray);

        tempArray.forEach(element -> {
            oArray.add(element);
            if (element instanceof Group)
                oArray.addAll(_sort(((Group) element).getChildren())); //TODO may exception here
        });

        return oArray;
    }

    public void updateObjects() {
        getSceneObjects().forEach(object -> {
            if (object instanceof TransformObject)
                ((TransformObject) object).applyTransformation();
        });
    }
}
