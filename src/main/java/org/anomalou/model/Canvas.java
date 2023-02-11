package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.Main;

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
    @Getter
    @Setter
    private UUID selection; //TODO not safe, invent something else

    public Canvas(){
        width = 1;
        height = 1;
        layersHierarchy = new ArrayList<>();
        selection = null;

        logger.fine("Workspace is created!");
    }

    public Canvas(int width, int height){
        this();

        this.width = width;
        this.height = height;
    }

    public void reshape(int width, int height){
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }
}
