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
    @Setter
    private int width;
    @Getter
    @Setter
    private int height;
    @Getter
    private ArrayList<UUID> layersHierarchy;

    public Canvas(){
        width = 0;
        height = 0;
        layersHierarchy = new ArrayList<>();

        logger.info("Workspace is created!");
    }
}
