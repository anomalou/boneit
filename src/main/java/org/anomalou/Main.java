package org.anomalou;

import org.anomalou.model.Bone;
import org.anomalou.model.FPoint;
import org.anomalou.model.Layer;
import org.anomalou.model.Project;
import org.anomalou.view.CanvasPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(Main.class.getName());

        JFrame frame = makeFrame();

        BufferedImage img = null;
        try{
            img = ImageIO.read(new File("test.png"));
        }catch (IOException ex){
            ex.printStackTrace();
        }

        Application application = new Application();
        Project project = application.createProject();
        project.getCanvas().reshape(400, 400);
        Layer layer = application.getController().createLayer();
        layer.setBaseBitmap(img);
        layer.setPosition(new Point(50, 50));
        Bone bone = application.getController().createSkeleton();
        bone.setBaseBitmap(img);
        bone.setPosition(new Point(70 ,70));
        bone.setRootBasePosition(new Point(10, 10));
        bone.setRootDirectionPosition(new Point(10,20));
        bone.setDirection(new FPoint(-1, -0.5));
        bone.applyRotation();

        CanvasPanel canvasPanel = new CanvasPanel(application.getProject().getCanvas(), application.getProject().getObjectCache());

        frame.add(canvasPanel);
        frame.revalidate();
    }

    public static JFrame makeFrame(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
        return frame;
    }
}