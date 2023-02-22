package org.anomalou.model;

import lombok.Getter;
import org.anomalou.exception.RegistrationException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class ObjectCache implements Serializable {
    private final Logger logger = Logger.getLogger(ObjectCache.class.getName());

    @Getter
    private HashMap<UUID, Layer> layers;

    public ObjectCache(){
        layers = new HashMap<>();
    }

    public void registerObject(UUID uuid, Layer layer){
        if(layers.containsKey(uuid)){
            logger.warning(String.format("Object with uuid %s already registered!", uuid.toString()));
            return;
        }

        layers.put(uuid, layer);
        logger.fine(String.format("Object with uuid %s registered! [%s|%s]", uuid.toString(), uuid.toString(), layer.getName()));
    }

    public void unregister(UUID uuid){
        if(!layers.containsKey(uuid)){
            logger.warning(String.format("Cache do not contain object with uuid %s!", uuid.toString()));
            return;
        }

        layers.remove(uuid);
        logger.fine(String.format("Object with uuid %s successfully unregistered!", uuid.toString()));
    }
}
