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
        
        return new FPoint(point.x * cos - point.y * sin, point.x * sin + point.y * cos);
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
    
}
