package org.anomalou.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class NameBuffer implements Serializable {
    private final Logger logger = Logger.getLogger(NameBuffer.class.getName());

    @Getter
    private HashMap<UUID, String> names;

    public NameBuffer(){
        names = new HashMap<>();
    }

    public void registerObject(UUID uuid, String name){
        if(names.containsKey(uuid)){
            logger.warning(String.format("%s already registered as %s!", uuid.toString(), names.get(uuid)));
            return;
        }
        if(names.containsValue(name)){
            for(int i = 0; i < 1000; i++){
                name = String.format("%s (%d)", name, i);
                if(!names.containsValue(name))
                    break;
            }
            names.put(uuid, name);
        }else{
            names.put(uuid, name);
        }

        logger.info(String.format("%s registered as %s!", uuid.toString(), name));
    }

    public void renameObject(UUID uuid, String newName){
        if(names.containsKey(uuid)){
            String oldName = names.replace(uuid, newName);
            logger.info(String.format("Object %s renamed from %s to %s.", uuid.toString(), oldName, newName));
        }else{
            logger.warning(String.format("Please register %s!", uuid.toString()));
        }
    }
}
