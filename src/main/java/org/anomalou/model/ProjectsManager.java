package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;
import org.anomalou.controller.PropertiesController;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.scene.SceneObject;
import org.anomalou.model.tools.ToolsManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
                        collect(Collectors.toMap(name -> {
                            String value = name.getFileName().toString();
                            value = value.substring(0, value.lastIndexOf('.'));
                            return value;
                        }, path -> path.toAbsolutePath().toString()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        save(String.format("%s.boneto", project.getName()));
    }

    public void save(String path){
        try {
            if(!path.endsWith(".boneto"))
                path += ".boneto";

            FileOutputStream fileOutputStream = new FileOutputStream(path);
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

    public void delete(String path){
        try{
            File project = new File(path);
            project.delete(); //TODO
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void exportPng(String path){
        BufferedImage canvas = new BufferedImage(project.getCanvas().getWidth(), project.getCanvas().getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = canvas.createGraphics();

        for(SceneObject object : project.getCanvas().sort()){
            if(object instanceof Layer layer){
                g2d.drawImage(layer.getResultBitmap(), (int) Math.round(layer.getGlobalPosition().x - layer.getRootVectorOrigin().x), (int) Math.round(layer.getGlobalPosition().y - layer.getRootVectorOrigin().y), null);
            }
        }

        g2d.dispose();

        if(!path.endsWith(".png"))
            path += ".png";

        try{
            File file = new File(path);
            file.createNewFile();
            ImageIO.write(canvas, "png", file);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
