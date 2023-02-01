package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

public class Project implements Serializable {
    private final Logger logger = Logger.getLogger(Project.class.getName());

    @Getter
    @Setter
    private String name;
    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter
    @Setter
    private Canvas canvas;
    /**
     * Cache that stores names of every object on canvas. The cache do not allow names to repeat.
     */
    @Getter
    private NameCache nameCache;
    /**
     * Cache that stores every object on scene. This allows get needed object faster.
     */
    @Getter
    private ObjectCache objectCache;
    //TODO here maybe something else, like history of changes or something like that

    public Project(){
        name = "NewProject";
        canvas = new Canvas();

        nameCache = new NameCache();
        objectCache = new ObjectCache();
    }

    public Project(String name){
        this();

        this.name = name;
    }
}
