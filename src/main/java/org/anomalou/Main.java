package org.anomalou;

import org.anomalou.controller.CanvasController;
import org.anomalou.model.scene.Bone;
import org.anomalou.model.scene.Layer;
import org.anomalou.model.Project;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Main {
    public static void main(String[] args) {
        BufferedImage img = null;
        BufferedImage arrow = null;
        BufferedImage sunflower_img = null;
        BufferedImage stem_img = null;
        try {
            img = ImageIO.read(Main.class.getResource("debug/test.png"));
//            arrow = ImageIO.read(new File("low_arrow.png"));
//            sunflower_img = ImageIO.read(new File("sunflower.png"));
            stem_img = ImageIO.read(Main.class.getResource("debug/stem.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Application application = new Application();
        Project project = application.createProject();
        project.getCanvas().reshape(400, 400);

        CanvasController canvasController = application.getCanvasController();

        Layer layer = new Layer();
        application.getCanvasController().registerObject(layer);
        layer.setSourceBitmap(img);
        layer.setVisible(true);
//        application.getCanvasController().addObject(layer);

        Layer root_stem = new Layer();
        application.getCanvasController().registerObject(root_stem);
        root_stem.setSourceBitmap(stem_img);
        root_stem.setRootVectorOrigin(new Point(15, 15));
        root_stem.setRootVectorDirection(new Point(15, 30));
        root_stem.setRotationAngle(Math.PI / 2);
        root_stem.setVisible(true);

        Bone root = new Bone();
        application.getCanvasController().registerObject(root);
        root.setRootVectorOrigin(new Point(0, 0));
        root.setRootVectorDirection(new Point(15, 0));
        root.addObject(root_stem);
        application.getCanvasController().addObject(root);

        Bone prev = root;
        for (int i = 0; i < 5; i++) {
            Bone newBone = new Bone();

            application.getCanvasController().registerObject(newBone);
            prev.addObject(newBone);
            newBone.setRootVectorOrigin(new Point(0, 0));
            newBone.setRootVectorDirection(new Point(15, 0));
            newBone.setRotationAngle(i * 0.1d);

            Layer stem = new Layer();
            application.getCanvasController().registerObject(stem);
            stem.setSourceBitmap(stem_img);
            stem.setRootVectorOrigin(new Point(15, 15));
            stem.setRootVectorDirection(new Point(15, 30));
            stem.setRotationAngle(Math.PI / 2);
            stem.setVisible(true);

            newBone.addObject(stem);
            prev = newBone;
        }

//        Bone root_stem = new Bone();
//        application.getCanvasController().registerObject(root, root_stem);
//        root_stem.setBaseBitmap(stem_img);
//        root_stem.setRootVectorOrigin(new Point(15, 15));
//        root_stem.setRootVectorDirection(new Point(15,30));
//
//        Bone stem2 = new Bone();
//        application.getCanvasController().registerObject(root, stem2);
//        stem2.setBaseBitmap(stem_img);
//        stem2.setRootVectorOrigin(new Point(15, 15));
//        stem2.setRootVectorDirection(new Point(15,30));
////        objectController.calculateRotationAngleFor(root_stem, new FPoint(1, -1));
////        application.getObjectController().applyRotation(childBone, childBone.getRotationAngle());
//
//        Bone sunflower = new Bone();
//        application.getCanvasController().registerObject(root_stem, sunflower);
//        sunflower.setBaseBitmap(sunflower_img);
//        sunflower.setRootVectorOrigin(new Point(15, 15));
//        sunflower.setRootVectorDirection(new Point(30,15));
////        objectController.calculateRotationAngleFor(sunflower, new FPoint(0, 1));
////        application.getObjectController().applyRotation(childBone2, childBone2.getRotationAngle());

        application.getCanvasController().applyTransform(root);

        application.start();

//        JFrame frame = makeFrame();
//        application.getUiManager().initInterface();
//        application.getUiManager().relocateView();
//
//        frame.setLayout(new BorderLayout());
//        frame.add(application.getUiManager(), BorderLayout.CENTER);
//        frame.revalidate();
//        frame.setVisible(true);
    }

    private static JFrame makeFrame() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
//        frame.setVisible(true);
        frame.setTitle("Boneto");
        return frame;
    }
}