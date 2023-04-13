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

public class Main {
    public static void main(String[] args) {
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

        CanvasController canvasController = application.getCanvasController();

        Layer layer = new Layer();
        application.getCanvasController().registerObject(null, layer);
        layer.setSourceBitmap(img);
        layer.setPosition(new Point(0, 0));
        layer.setVisible(true);

        Bone root = new Bone();
        application.getCanvasController().registerObject(null, root);
        root.setSourceBitmap(stem_img);
        root.setPosition(new Point(30 ,80));
        root.setRootVectorOrigin(new Point(15, 15));
        root.setRootVectorDirection(new Point(15,30));
        canvasController.applyBoneRotation(root, root.getRotationAngle());

        Bone prev = root;
        for(int i = 0; i < 1000; i++){
            Bone newBone = new Bone();

            application.getCanvasController().registerObject(prev, newBone);
            newBone.setSourceBitmap(stem_img);
            newBone.setPosition(new Point(30 ,80));
            newBone.setRootVectorOrigin(new Point(15, 15));
            newBone.setRootVectorDirection(new Point(15,30));
            newBone.setRotationAngle(i * 0.1d);
            prev = newBone;
        }

//        Bone stem = new Bone();
//        application.getCanvasController().registerObject(root, stem);
//        stem.setBaseBitmap(stem_img);
//        stem.setRootVectorOrigin(new Point(15, 15));
//        stem.setRootVectorDirection(new Point(15,30));
//
//        Bone stem2 = new Bone();
//        application.getCanvasController().registerObject(root, stem2);
//        stem2.setBaseBitmap(stem_img);
//        stem2.setRootVectorOrigin(new Point(15, 15));
//        stem2.setRootVectorDirection(new Point(15,30));
////        objectController.calculateRotationAngleFor(stem, new FPoint(1, -1));
////        application.getObjectController().applyRotation(childBone, childBone.getRotationAngle());
//
//        Bone sunflower = new Bone();
//        application.getCanvasController().registerObject(stem, sunflower);
//        sunflower.setBaseBitmap(sunflower_img);
//        sunflower.setRootVectorOrigin(new Point(15, 15));
//        sunflower.setRootVectorDirection(new Point(30,15));
////        objectController.calculateRotationAngleFor(sunflower, new FPoint(0, 1));
////        application.getObjectController().applyRotation(childBone2, childBone2.getRotationAngle());

        application.getCanvasController().applyTransform(root);

        application.openInterface();

//        JFrame frame = makeFrame();
//        application.getUiManager().initInterface();
//        application.getUiManager().relocateView();
//
//        frame.setLayout(new BorderLayout());
//        frame.add(application.getUiManager(), BorderLayout.CENTER);
//        frame.revalidate();
//        frame.setVisible(true);
    }

    private static JFrame makeFrame(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
//        frame.setVisible(true);
        frame.setTitle("Boneto");
        return frame;
    }
}