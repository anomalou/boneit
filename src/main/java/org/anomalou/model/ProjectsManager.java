package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.tools.ToolsManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectsManager {
    /**
     * Currently opened project
     */
    @Getter
    private Project project;
    @Getter
    private ToolsManager toolsManager;
    private PropertiesController propertiesController;
    @Getter
    @Setter
    private String projectsDirectory;
    @Getter
    private Map<String, String> projects;

    public ProjectsManager(PropertiesController propertiesController) {
        this.propertiesController = propertiesController;
        projectsDirectory = propertiesController.getString("projects.dir");
        projects = new HashMap<>();
    }

    public boolean isOpened() {
        return project != null;
    }

    public void create(String name, int width, int height) {
        project = new Project(name, width, height);
        toolsManager = new ToolsManager(project.getCanvas());
    }

    public void open(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            project = (Project) objectInputStream.readObject();
            objectInputStream.close();

            toolsManager = new ToolsManager(project.getCanvas());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void scan() {
        try {
            try (Stream<Path> stream = Files.list(Paths.get(projectsDirectory))) {
                projects = stream.filter(file -> !Files.isDirectory(file)).filter(file -> file.toString().endsWith("boneto")).
                        collect(Collectors.toMap(name -> name.getFileName().toString(), path -> path.toAbsolutePath().toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(String.format("%s.boneto", project.getName()));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(project);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void close() {
        project = null;
    }
}
