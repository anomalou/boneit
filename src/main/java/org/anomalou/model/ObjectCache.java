package org.anomalou.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class ObjectCache implements Serializable {
    private final Logger logger = Logger.getLogger(ObjectCache.class.getName());

    @Getter
    private final HashMap<UUID, Layer> objects;

    public ObjectCache(){
        objects = new HashMap<>();
    }

    public void registerObject(UUID uuid, Layer layer){
        if(objects.containsKey(uuid)){
            logger.warning(String.format("Object with uuid %s already registered!", uuid.toString()));
            return;
        }

        objects.put(uuid, layer);
        logger.fine(String.format("Object with uuid %s registered! [%s|%s]", uuid.toString(), uuid.toString(), layer.getName()));
    }

    public void unregister(UUID uuid){
        if(!objects.containsKey(uuid)){
            logger.warning(String.format("Cache do not contain object with uuid %s!", uuid.toString()));
            return;
        }

        objects.remove(uuid);
        logger.fine(String.format("Object with uuid %s successfully unregistered!", uuid.toString()));
    }
}
