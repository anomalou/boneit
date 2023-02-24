package org.anomalou.model;

import java.awt.*;

public interface Tool {
    String getName();

    /**
     * Return rect to redraw
     * @param g graphic to draw tool interface
     * @param position mouse position on screen
     * @return rect to redraw
     */
    Rectangle drawInterface(Graphics g, Point position);
    void press(Graphics g, Point position, int button, boolean released);
    void click(Graphics g, Point position, int button);
    void drag(Graphics g, Point position, int button);
}
