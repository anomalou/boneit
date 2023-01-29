package org.anomalou;

import org.anomalou.model.Bone;
import org.anomalou.model.FPoint;

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

        Bone bone = new Bone();

        try{
            BufferedImage img = ImageIO.read(new File("test.png"));
            bone.setBaseBitmap(img);
        }catch (IOException ex){
            ex.printStackTrace();
        }

        bone.setRootPosition(new Point(70, 70));
        bone.setDirectionPosition(new FPoint(75, 69.8));

        logger.info(String.valueOf(bone.getAngle()));

        bone.applyRotation();

        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(bone.getTransformBitmap(), 0, 0, 400, 400, null);
            }
        };

        frame.add(panel);
        frame.invalidate();
        frame.repaint();
    }

    public static JFrame makeFrame(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setVisible(true);
        return frame;
    }
}