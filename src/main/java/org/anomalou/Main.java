package org.anomalou;

import org.anomalou.controller.ObjectController;
import org.anomalou.model.Bone;
import org.anomalou.model.FPoint;
import org.anomalou.model.Layer;
import org.anomalou.model.Project;
import org.anomalou.view.CanvasPanel;
import org.anomalou.view.ObjectTreePanel;

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
        BufferedImage sunflower_img = null;
        BufferedImage stem_img = null;
        try{
            img = ImageIO.read(new File("test.png"));
            arrow = ImageIO.read(new File("low_arrow.png"));
            sunflower_img = ImageIO.read(new File("sunflower.png"));
            stem_img = ImageIO.read(new File("stem.png"));
        }catch (IOException ex){
            ex.printStackTrace();
        }

        Application application = new Application();
        Project project = application.createProject();
        project.getCanvas().reshape(400, 400);

        ObjectController objectController = application.getObjectController();

        Layer layer = application.getProjectController().createLayer();
        layer.setBaseBitmap(img);
        layer.setPosition(new Point(0, 0));
        layer.setVisible(true);

        Bone root = application.getProjectController().createSkeleton();
        root.setBaseBitmap(stem_img);
        root.setPosition(new Point(30 ,80));
        root.setRootVectorOrigin(new Point(15, 15));
        root.setRootVectorDirection(new Point(15,30));
//        objectController.calculateRotationAngleFor(root, new FPoint(0, -1));
        application.getObjectController().applyRotation(root, root.getRotationAngle());

        Bone stem = application.getObjectController().extrudeBone(root);
        stem.setBaseBitmap(stem_img);
        stem.setRootVectorOrigin(new Point(15, 15));
        stem.setRootVectorDirection(new Point(15,30));
//        objectController.calculateRotationAngleFor(stem, new FPoint(1, -1));
//        application.getObjectController().applyRotation(childBone, childBone.getRotationAngle());

        Bone sunflower = application.getObjectController().extrudeBone(stem);
        sunflower.setBaseBitmap(sunflower_img);
        sunflower.setRootVectorOrigin(new Point(15, 15));
        sunflower.setRootVectorDirection(new Point(30,15));
//        objectController.calculateRotationAngleFor(sunflower, new FPoint(0, 1));
//        application.getObjectController().applyRotation(childBone2, childBone2.getRotationAngle());

        application.getObjectController().applyTransform(root, root.getRotationAngle());

        CanvasPanel canvasPanel = new CanvasPanel(application.getProject().getCanvas(), application.getObjectController(), application.getPropertiesController());

        ObjectTreePanel treePanel = new ObjectTreePanel(application.getProject().getCanvas(), application.getObjectController(), application.getPropertiesController());

        //TODO move to Application
        JSplitPane splitPane = new JSplitPane(SwingConstants.VERTICAL, canvasPanel, treePanel);
        splitPane.setResizeWeight(0.9);

        frame.setLayout(new BorderLayout());

        frame.add(splitPane, BorderLayout.CENTER);
        frame.add(new JLabel("Info panel"), BorderLayout.PAGE_END);
        frame.revalidate();
    }

    public static JFrame makeFrame(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
        frame.setTitle("Bone-it");
        return frame;
    }
}