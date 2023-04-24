package org.anomalou.view;

import com.formdev.flatlaf.ui.FlatRoundBorder;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePreview extends JPanel {
    private final BufferedImage image;

    public ImagePreview(BufferedImage image){
        this.image = image;
        setBackground(Color.white);
        setBorder(new FlatRoundBorder());
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, getWidth() / 2 - image.getWidth() / 2, getHeight() / 2 - image.getHeight() / 2, null);

        Color old = g.getColor();
        g.setColor(Color.lightGray);
        g.drawRect(getWidth() / 2 - image.getWidth() / 2, getHeight() / 2 - image.getHeight() / 2, image.getWidth(), image.getHeight());
        g.setColor(old);
    }
}
