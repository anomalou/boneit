package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class Workspace {
    private final Logger logger = Logger.getLogger(Workspace.class.getName());

    @Getter
    @Setter
    private String name;
    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
    @Getter
    private ArrayList<Layer> layers;

    public Workspace(){
        name = "NewWorkspace";
        width = 0;
        height = 0;

        logger.info(String.format("Workspace %s(%s) is created", name, uuid.toString()));
    }
}
