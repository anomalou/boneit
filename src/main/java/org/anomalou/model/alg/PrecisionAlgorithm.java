package org.anomalou.model.alg;

import org.anomalou.model.FPoint;
import org.anomalou.model.PixelCorner;
import org.anomalou.model.PixelData;
import org.anomalou.utils.CoordinatesUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrecisionAlgorithm implements TransformationAlgorithm{
    @Override
    public BufferedImage rotate(BufferedImage image, double angle, Point origin) {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        

        return buffer;
    }

    private void calculatePixel(BufferedImage source, BufferedImage dest, Point pixel, double angle, Point pivot) {
        PixelData pixelData = rotatePixel(pixel, angle, pivot);

        if (Double.compare(pixelData.getLeftUpper().y, pixelData.getRightUpper().y) == 0) {
            // Process all pixel in color
        } else {
            // Calculate pixel areas squares
            
        }
    }

    

    private List<FPoint> calculateFigure(PixelData data, PixelCorner corner) {
        FPoint pixel = switch (corner) {
            case LU -> findPixel(data.getLeftUpper());
            case RU -> findPixel(data.getRightUpper());
            case RB -> findPixel(data.getRightBottom());
            case LB -> findPixel(data.getLeftBottom());
        };
        
        
    }

    private FPoint findIntersectionHorisontal(FPoint p1, FPoint p2, double k) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        
        // Проверяем, лежит ли k между y1 и y2
        if (Math.min(y1, y2) <= k && k <= Math.max(y1, y2)) {
            if (y1 == y2) {
                // Отрезок горизонтальный, проверяем совпадение y
                if (y1 == k) {
                    return new FPoint(x1, k); // Любая точка отрезка
                } else {
                    return null; // Нет пересечения
                }
            }
            
            // Вычисляем параметр t
            double t = (k - y1) / (y2 - y1);
            double x = x1 + t * (x2 - x1);
            return new FPoint(x, k);
        }
        
        return null; // Нет пересечения
    }
    
    private FPoint findIntesectionVertical(FPoint p1, FPoint p2, double k) {
        double x1 = p1.x, y1 = p1.y;
        double x2 = p2.x, y2 = p2.y;
        
        // Проверяем, лежит ли k между x1 и x2
        if (Math.min(x1, x2) <= k && k <= Math.max(x1, x2)) {
            if (x1 == x2) {
                // Отрезок вертикальный, проверяем совпадение x
                if (x1 == k) {
                    return new FPoint(k, y1); // Любая точка отрезка
                } else {
                    return null; // Нет пересечения
                }
            }
            
            // Вычисляем параметр t
            double t = (k - x1) / (x2 - x1);
            double y = y1 + t * (y2 - y1);
            return new FPoint(k, y);
        }
        
        return null; // Нет пересечения
    }
    
    private double findLength(FPoint p1, FPoint p2) {
        double dX, dY;
        
        dX = Math.pow(p2.x - p1.x, 2.0d);
        dY = Math.pow(p2.y - p1.y, 2.0d);
        
        return Math.sqrt(dX + dY);
    }

    private FPoint findPixel(FPoint point) {
        return new FPoint(Math.floor(point.x), Math.floor(point.y));
    }
}
