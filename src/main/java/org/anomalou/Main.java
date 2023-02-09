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
        BufferedImage arrow = null;
        try{
            img = ImageIO.read(new File("test.png"));
            arrow = ImageIO.read(new File("arrow.png"));
        }catch (IOException ex){
            ex.printStackTrace();
        }

        Application application = new Application();
        Project project = application.createProject();
        project.getCanvas().reshape(400, 400);
        Layer layer = application.getProjectController().createLayer();
        layer.setBaseBitmap(img);
        layer.setPosition(new Point(50, 50));
        layer.setVisible(false);
        Bone bone = application.getProjectController().createSkeleton();
        bone.setBaseBitmap(arrow);
        bone.setPosition(new Point(50 ,50));
        bone.setRootBasePosition(new Point(50, 50));
        bone.setRootDirectionPosition(new Point(100,50));
        bone.setDirection(new FPoint(1, -1));
        application.getLayerController().applyRotation(bone);

        Bone childBone = application.getLayerController().extrudeBone(bone);
        childBone.setBaseBitmap(arrow);
        childBone.setRootBasePosition(new Point(50, 50));
        childBone.setRootDirectionPosition(new Point(100,50));
        childBone.setDirection(new FPoint(0, 1));
        application.getLayerController().applyRotation(childBone);

        Bone childBone2 = application.getLayerController().extrudeBone(childBone);
        childBone2.setBaseBitmap(arrow);
        childBone2.setRootBasePosition(new Point(50, 50));
        childBone2.setRootDirectionPosition(new Point(100,50));
        childBone2.setDirection(new FPoint(0, 0));
        application.getLayerController().applyRotation(childBone2);

        application.getLayerController().applySkeletonPosition(bone);

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