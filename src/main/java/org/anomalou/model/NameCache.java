package org.anomalou.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class NameCache implements Serializable {
    private final Logger logger = Logger.getLogger(NameCache.class.getName());

    @Getter
    private HashMap<UUID, String> names;

    public NameCache(){
        names = new HashMap<>();
    }

    public void registerName(UUID uuid, String name){
        if(names.containsKey(uuid)){
            logger.warning(String.format("%s already registered as %s!", uuid.toString(), names.get(uuid)));
            return;
        }
        if(names.containsValue(name)){
            String temp = ""; //TODO check it work later, its MAYBE, AS ALWAYS, ITS NOT WORKING AS I WANT
            for(int i = 0; i < 1000; i++){
                temp = String.format("%s (%d)", name, i);
                if(!names.containsValue(temp))
                    break;
            }
            names.put(uuid, temp);
        }else{
            names.put(uuid, name);
        }

        logger.info(String.format("%s registered as %s!", uuid.toString(), name));
    }

    public void unregister(UUID uuid){
        if(!names.containsKey(uuid)){
            logger.warning(String.format("Object with uuid %s do not exist!", uuid.toString()));
            return;
        }

        names.remove(uuid);
        logger.info(String.format("Object with uuid %s successfully unregistered!", uuid.toString()));
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
