package org.anomalou.controller;

import org.anomalou.model.Project;
import org.anomalou.model.ProjectsManager;
import org.anomalou.model.tools.ToolsManager;

import java.util.Map;

public class ProjectsManagerController extends Controller{
    private ProjectsManager projectsManager;

    public ProjectsManagerController(ProjectsManager projectsManager) {
        this.projectsManager = projectsManager;
    }

    public boolean isOpened(){
        return projectsManager.isOpened();
    }

    public Project getProject(){
        return projectsManager.getProject();
    }

    public Map<String, String> getProjects(){
        return projectsManager.getProjects();
    }

    public ToolsManager getToolPanel(){
        return projectsManager.getToolsManager();
    }

    public void create(String name, int width, int height){
        projectsManager.create(name, width, height);
    }

    public void scan(){
        projectsManager.scan();
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

    public void delete(String path){
        projectsManager.delete(path);
    }
}
