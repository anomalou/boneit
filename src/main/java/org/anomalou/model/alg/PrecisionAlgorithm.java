package org.anomalou.model.alg;

import lombok.NonNull;
import org.anomalou.model.FPoint;
import org.anomalou.model.Line;
import org.anomalou.model.PixelCorner;
import org.anomalou.model.PixelData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
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
            Set<PixelCorner> pixelCorners = new HashSet<>();
            
            for (PixelCorner corner : PixelCorner.values()) {
                if (!pixelCorners.contains(corner)) {
                    List<FPoint> figureCorners = calculateFigure(pixelData, corner, pixelCorners);
                    // calculate square here
                    // paint corner pixel in color by square proportion
                }
            }
        }
    }

    

    private List<FPoint> calculateFigure(PixelData data, PixelCorner corner, @NonNull Set<PixelCorner> usedCorners) {
        List<FPoint> figure = new ArrayList<>(); // Corners of figure in clockwise direction
        
        FPoint pixel = switch (corner) {
            case LU -> findPixel(data.getLeftUpper());
            case RU -> findPixel(data.getRightUpper());
            case RB -> findPixel(data.getRightBottom());
            case LB -> findPixel(data.getLeftBottom());
        };
        
        Line l1 = null, l2 = null;
        
        switch (corner) {
            case LU -> {
                l1 = new Line(data.getLeftUpper(), data.getLeftBottom());
                l2 = new Line(data.getLeftUpper(), data.getRightUpper());
            }
            case RU -> {
                l1 = new Line(data.getRightUpper(), data.getLeftUpper());
                l2 = new Line(data.getRightUpper(), data.getRightBottom());
            }
            case RB -> {
                l1 = new Line(data.getRightBottom(), data.getRightUpper());
                l2 = new Line(data.getRightBottom(), data.getLeftBottom());
            }
            case LB -> {
                l1 = new Line(data.getLeftBottom(), data.getLeftUpper());
                l2 = new Line(data.getLeftBottom(), data.getRightBottom());
            }
        }
        
        FPoint line1Intersection = findIntersectionWithPixel(l1, pixel);
        FPoint line2Intersection = findIntersectionWithPixel(l2, pixel);
        
        // I never found situation when pixel side intersected by two matrix pixel sides in same time
        // So the two variants should be enough. Also, I do not calculate square of part that been formed without pixel corner in head of it
        if (Objects.nonNull(line1Intersection) && Objects.nonNull(line2Intersection)) {
            // Triangle or pentagon / hexagon situation
            // Need define matrix pixel side with lines intersects and  then use matrix pixel coordinates for creation of figure corners
            usedCorners.add(corner);
        } else {
            // Two pixel corner in matrix pixel. Need recalculate another intersection by the corner
        }
        
    }
    
    private FPoint findIntersectionWithPixel(Line line, FPoint pixel) {
        FPoint hIntersection = line.findIntersectionHorisontal(Math.max(0, pixel.y));
        FPoint vIntersection = line.findIntersectionVertical(Math.max(0, pixel.x));
        
        if (Objects.isNull(hIntersection)) {
            hIntersection = line.findIntersectionHorisontal(pixel.y + 1);
        }
        if (Objects.isNull(vIntersection)) {
            vIntersection = line.findIntersectionVertical(pixel.x + 1);
        }
        
        if (Objects.nonNull(hIntersection) && Objects.nonNull(vIntersection)) {
            double hLength = findLength(line.p1, hIntersection);
            double vLength = findLength(line.p1, vIntersection);
            
            if (hLength < vLength) {
                return hIntersection;
            } else {
                return vIntersection;
            }
        }
        
        if (Objects.nonNull(hIntersection)) {
            return hIntersection;
        } else {
            return vIntersection;
        }
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
