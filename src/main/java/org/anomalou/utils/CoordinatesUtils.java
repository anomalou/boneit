package org.anomalou.utils;

import org.anomalou.model.FPoint;

import java.awt.*;

/**
 * Author: Aleksandr Borodin
 * Creation date: 4/8/25
 */
public class CoordinatesUtils {
    
    public static FPoint rotateNormalizedPoint(FPoint point, double angle) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        
        return new FPoint(Math.round((point.x * cos - point.y * sin) * 1e6) / 1e6, Math.round((point.x * sin + point.y * cos) * 1e6) / 1e6);
    }
    
    public static FPoint rotateNormalizedPoint(Point point, double angle) {
        return rotateNormalizedPoint(new FPoint(point), angle);
    }
    
    public static FPoint rotatePoint(FPoint point, double angle, Point origin) {
        point.x -= origin.x;
        point.y -= origin.y;
        
        FPoint rotatedNormal = rotateNormalizedPoint(point, angle);
        
        return rotatedNormal.plus(origin);
    }
    
    public static FPoint rotatePoint(Point point, double angle, Point origin) {
        return rotatePoint(new FPoint(point), angle, origin);
    }

    public static class PixelCorners {
        public boolean leftUpper;
        public boolean rightUpper;
        public boolean leftBottom;
        public boolean rightBottom;
    }
    
}
