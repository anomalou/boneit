package org.anomalou;

import lombok.Getter;
import org.anomalou.controller.ProjectController;
import org.anomalou.model.Project;

public class Application {
    private Project project;
    @Getter
    private ProjectController projectController;

    public Application(){

    }

    public void createProject(){
        project = new Project();
    }

    public void createProject(String name){
        project = new Project(name);
    }
}
