package org.anomalou.model;

import lombok.Getter;
import org.anomalou.controller.CanvasController;
import org.anomalou.controller.ProjectController;
import org.anomalou.controller.PropertiesController;
import org.anomalou.controller.ToolPanelController;
import org.anomalou.model.tools.ToolPanel;

public class ProjectsManager {
    /**
     * Currently opened project
     */
    @Getter
    private Project project;
    private ToolPanel toolPanel;

    public ProjectsManager(){

    }

    public void create(String name, int width, int height){
        project = new Project(name, width, height);
    }

    public void open(String path){

    }

    public void save(){

    }

    public void close(){

    }
}
