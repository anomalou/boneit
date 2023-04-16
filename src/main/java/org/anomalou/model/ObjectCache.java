package org.anomalou.model;

import lombok.Getter;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Object that store all scene objects. Used for faster access to them
 */
public class ObjectCache implements Serializable {
    private final Logger logger = Logger.getLogger(ObjectCache.class.getName());

    @Getter
    private final HashMap<UUID, SceneObject> objects;

    public ObjectCache() {
        objects = new HashMap<>();
    }

    public void registerObject(UUID uuid, SceneObject object) {
        if (objects.containsKey(uuid)) {
            logger.warning(String.format("Object with uuid %s already registered!", uuid.toString()));
            return;
        }

        objects.put(uuid, object);
        logger.fine(String.format("Object with uuid %s registered! [%s|%s]", uuid.toString(), uuid.toString(), object.getName()));
    }

    public void unregister(UUID uuid) {
        if (!objects.containsKey(uuid)) {
            logger.warning(String.format("Cache do not contain object with uuid %s!", uuid.toString()));
            return;
        }

        objects.remove(uuid);
        logger.fine(String.format("Object with uuid %s successfully unregistered!", uuid.toString()));
    }
}
