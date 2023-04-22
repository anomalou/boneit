package org.anomalou.controller;

import org.anomalou.model.ProjectsManager;

public class ProjectsManagerController extends Controller{
    private ProjectsManager projectsManager;

    public ProjectsManagerController(ProjectsManager projectsManager) {
        this.projectsManager = projectsManager;
    }

    public void create(String name, int width, int height){
        projectsManager.create(name, width, height);
    }

    public void open(String path){
        projectsManager.open(path);
    }

    public void save(){
        projectsManager.save();
    }

    public void close(){
        projectsManager.close();
    }
}
