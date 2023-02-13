package org.anomalou;

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
        try{
            img = ImageIO.read(new File("test.png"));
            arrow = ImageIO.read(new File("low_arrow.png"));
        }catch (IOException ex){
            ex.printStackTrace();
        }

        Application application = new Application();
        Project project = application.createProject();
        project.getCanvas().reshape(400, 400);
        Layer layer = application.getProjectController().createLayer();
        layer.setBaseBitmap(img);
        layer.setPosition(new Point(0, 0));
        layer.setVisible(true);

        Bone bone = application.getProjectController().createSkeleton();
        bone.setBaseBitmap(arrow);
        bone.setPosition(new Point(0 ,0));
        bone.setRootBasePosition(new Point(5, 5));
        bone.setRootDirectionPosition(new Point(10,5));
        bone.setDirection(new FPoint(1, 0));
        application.getObjectController().applyRotation(bone, bone.getAngle());

        Bone childBone = application.getObjectController().extrudeBone(bone);
        childBone.setBaseBitmap(arrow);
        childBone.setRootBasePosition(new Point(5, 5));
        childBone.setRootDirectionPosition(new Point(10,5));
        childBone.setDirection(new FPoint(1, -1));
        application.getObjectController().applyRotation(childBone, childBone.getAngle());

        Bone childBone2 = application.getObjectController().extrudeBone(childBone);
        childBone2.setBaseBitmap(arrow);
        childBone2.setRootBasePosition(new Point(5, 5));
        childBone2.setRootDirectionPosition(new Point(10,5));
        childBone2.setDirection(new FPoint(1, 1));
        application.getObjectController().applyRotation(childBone2, childBone2.getAngle());

        application.getObjectController().applyTransform(bone, bone.getAngle());

        application.getProject().getCanvas().setSelection(childBone2.getUuid());

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