package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Object that store all project settings, canvas, etc
 */
public class Project implements Serializable {
//    private transient final Logger logger = Logger.getLogger(Project.class.getName());

    @Getter
    @Setter
    private String name;
    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    @Setter
    private Canvas canvas;
    //TODO here maybe something else, like history of changes or something like that

    public Project() {
        this(1, 1);
    }

    public Project(int width, int height) {
        name = "NewProject";
        canvas = new Canvas(width, height);
    }

    public Project(String name) {
        this(name, 1, 1);
    }

    public Project(String name, int width, int height) {
        this(width, height);

        this.name = name;
    }
}
