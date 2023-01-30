package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class Canvas implements Serializable {
    private final Logger logger = Logger.getLogger(Canvas.class.getName());

    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
    @Getter
    private ArrayList<Layer> layersHierarchy;

    public Canvas(){
        width = 0;
        height = 0;
        layersHierarchy = new ArrayList<>();

        logger.info(String.format("Workspace (%s) is created", uuid.toString()));
    }

    public void createLayer(){

    }
}
