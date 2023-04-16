package org.anomalou.model.tools;

import java.awt.*;

/**
 * Interface for connection all tools. All tools can have only two action: primary and secondary
 */
public interface Tool {
    String getName();

    /**
     * Return rect to redraw
     * @param g graphic to draw tool interface
     * @param position mouse position on screen
     * @return rect to redraw
     */
    Rectangle drawInterface(Graphics g, Point position);
    void primaryUse(Graphics g, Point position);
    void secondaryUse(Graphics g, Point position);
}
