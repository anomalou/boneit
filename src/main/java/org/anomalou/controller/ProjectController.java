package org.anomalou.controller;

import org.anomalou.model.*;
import org.anomalou.model.Canvas;

public class ProjectController extends Controller{
    private final Project project;

    public ProjectController(Project project) {
        this.project = project;
    }
    public Canvas createCanvas(){
        Canvas canvas = new Canvas();
        project.setCanvas(canvas);
        return canvas;
    }
}
