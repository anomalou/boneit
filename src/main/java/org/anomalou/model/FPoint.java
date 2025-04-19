package org.anomalou.model;

import java.awt.*;
import java.io.Serializable;

/**
 * Default object to store coordinates in real format. By default, type is <code>double<code/>
 */
public class FPoint implements Serializable {
    public double x;
    public double y;

    public FPoint() {
        x = 0d;
        y = 0d;
    }

    public FPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FPoint(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public String toString() {
        return String.format("[%f; %f]", x, y);
    }

    public boolean equals(FPoint point) {
        return !(Double.compare(point.x, x) != 0 || Double.compare(point.y, y) != 0);
    }

    public FPoint plus(FPoint point) {
        this.x += point.x;
        this.y += point.y;
        
        return this;
    }
    
    public FPoint plus(Point point) {
        return plus(new FPoint(point));
    }
    
    public FPoint mul(double value) {
        this.x *= value;
        this.y *= value;
        
        return this;
    }
}
