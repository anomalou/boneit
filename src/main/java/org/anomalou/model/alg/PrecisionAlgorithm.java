package org.anomalou.model.alg;

import lombok.NonNull;
import org.anomalou.model.Color;
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

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                calculatePixel(image, buffer, new Point(x, y), angle, origin);
            }
        }

        return buffer;
    }

    private void calculatePixel(BufferedImage source, BufferedImage dest, Point pixel, double angle, Point pivot) {
        PixelData pixelData = rotatePixel(pixel, angle, pivot);
        FPoint realPixel = pixelData.getLeftUpperBound(); // left upper pixel bound

        if (Double.compare(pixelData.getLeftUpper().y, pixelData.getRightUpper().y) == 0 ||
            Double.compare(pixelData.getLeftUpper().x, pixelData.getRightUpper().x) == 0) {
            dest.setRGB((int) Math.round(realPixel.x), (int) Math.round(realPixel.y), source.getRGB(pixel.x, pixel.y));
        } else {
            // Calculate pixel areas squares
            Set<PixelCorner> pixelCorners = new HashSet<>();

            double partSum = 0;
            int rectangleNumber = 0;
            FPoint pentagonPixel = null;

            for (PixelCorner corner : PixelCorner.values()) {
                if (!pixelCorners.contains(corner)) {
                    List<FPoint> figureCorners = calculateFigure(pixelData, corner, pixelCorners);
                    // calculate square here
                    // paint corner pixel in color by square proportion

                    if (figureCorners.size() == 3) {
                        // triangle
                        rectangleNumber += 1;
                        FPoint destPixel = findPixel(figureCorners.get(0));
                        if (destPixel.x >= 0 && destPixel.x < dest.getWidth() &&
                            destPixel.y >= 0 && destPixel.y < dest.getHeight()) {

                            double part = calculateSquare(figureCorners);
                            partSum += part;

                            Color sourceColor = Color.unpack(source.getRGB(pixel.x, pixel.y));
                            Color destColor = Color.unpack(dest.getRGB((int) destPixel.x, (int) destPixel.y));
                            Color destPartColor = destColor.plus(sourceColor.part(part));

                            dest.setRGB((int) destPixel.x, (int) destPixel.y, destPartColor.pack());
                        }
                    } else if (figureCorners.size() == 1) {
                        // pentagon, hexagon or two cornered figure
                        // calculate as difference
                        pentagonPixel = findPixel(figureCorners.get(0));
                    } else {
                        // rectangle
                        FPoint destPixel = findPixel(figureCorners.get(0));
                        if (destPixel.x >= 0 && destPixel.x < dest.getWidth() &&
                                destPixel.y >= 0 && destPixel.y < dest.getHeight()) {

                            double part = calculateSquare(figureCorners);
                            partSum += part;

                            Color sourceColor = Color.unpack(source.getRGB(pixel.x, pixel.y));
                            Color destColor = Color.unpack(dest.getRGB((int) destPixel.x, (int) destPixel.y));
                            Color destPartColor = destColor.plus(sourceColor.part(part));

                            dest.setRGB((int) destPixel.x, (int) destPixel.y, destPartColor.pack());
                        }
                    }
                }
            }

            if (rectangleNumber == 4 && partSum < 1.0) {
                // pixel in center of dest pixel
                FPoint centerPixel = calcPixelCross(realPixel);

                if (centerPixel.x >= 0 && centerPixel.x < dest.getWidth() && centerPixel.y >= 0 && centerPixel.y < dest.getHeight()) {
                    Color sourceColor = Color.unpack(source.getRGB(pixel.x, pixel.y));
                    Color destColor = Color.unpack(dest.getRGB((int) centerPixel.x, (int) centerPixel.y));
                    Color destPartColor = destColor.plus(sourceColor.part(1.0 - partSum));

                    dest.setRGB((int) centerPixel.x, (int) centerPixel.y, destPartColor.pack());
                }
            }

            if (Objects.nonNull(pentagonPixel) && partSum < 1.0) {
                if (pentagonPixel.x >= 0 && pentagonPixel.x < dest.getWidth() &&
                    pentagonPixel.y >= 0 && pentagonPixel.y < dest.getHeight()) {

                    Color sourceColor = Color.unpack(source.getRGB(pixel.x, pixel.y));
                    Color destColor = Color.unpack(dest.getRGB((int) pentagonPixel.x, (int) pentagonPixel.y));
                    Color destPartColor = destColor.plus(sourceColor.part(1.0 - partSum));

                    dest.setRGB((int) pentagonPixel.x, (int) pentagonPixel.y, destPartColor.pack());
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

        FPoint cornerPoint = switch (corner) {
            case LU -> data.getLeftUpper();
            case RU -> data.getRightUpper();
            case RB -> data.getRightBottom();
            case LB -> data.getLeftBottom();
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
            if (Double.compare(line1Intersection.x, line2Intersection.x) == 0 ||
                Double.compare(line1Intersection.y, line2Intersection.y) == 0) {
                // triangle
                figure.addAll(List.of(cornerPoint, line2Intersection, line1Intersection));
            } else if (isSideBySide(line1Intersection, line2Intersection)){
                // pentagon, hexagon etc
                figure.add(cornerPoint);
            } else {
                // rectangle
                FPoint crossPoint = calcPixelCross(data.getLeftUpperBound());
                figure.addAll(List.of(cornerPoint, line2Intersection, crossPoint, line1Intersection));
            }
        } else {
            // Two pixel corner in matrix pixel. Need recalculate another intersection by the corner

            switch (corner) {
                case LU -> {
                    if (Objects.isNull(line1Intersection)) {
                        line1Intersection = findIntersectionWithPixel(new Line(data.getLeftBottom(), data.getRightBottom()), pixel);
                        usedCorners.add(PixelCorner.LB);
                    } else {
                        line2Intersection = findIntersectionWithPixel(new Line(data.getRightUpper(), data.getRightBottom()), pixel);
                        usedCorners.add(PixelCorner.RU);
                    }
                }
                case RU -> {
                    if (Objects.isNull(line1Intersection)) {
                        line1Intersection = findIntersectionWithPixel(new Line(data.getLeftUpper(), data.getLeftBottom()), pixel);
                        usedCorners.add(PixelCorner.LU);
                    } else {
                        line2Intersection = findIntersectionWithPixel(new Line(data.getRightBottom(), data.getLeftBottom()), pixel);
                        usedCorners.add(PixelCorner.RB);
                    }
                }
                case RB -> {
                    if (Objects.isNull(line1Intersection)) {
                        line1Intersection = findIntersectionWithPixel(new Line(data.getRightUpper(), data.getLeftUpper()), pixel);
                        usedCorners.add(PixelCorner.RU);
                    } else {
                        line2Intersection = findIntersectionWithPixel(new Line(data.getLeftBottom(), data.getLeftUpper()), pixel);
                        usedCorners.add(PixelCorner.LB);
                    }
                }
                case LB -> {
                    if (Objects.isNull(line1Intersection)) {
                        line1Intersection = findIntersectionWithPixel(new Line(data.getLeftUpper(), data.getRightUpper()), pixel);
                        usedCorners.add(PixelCorner.LU);
                    } else {
                        line2Intersection = findIntersectionWithPixel(new Line(data.getRightBottom(), data.getRightUpper()), pixel);
                        usedCorners.add(PixelCorner.RB);
                    }
                }
            }

            figure.add(cornerPoint);
        }

        usedCorners.add(corner);

        return figure;
    }

    private double calculateSquare(List<FPoint> figure) {
        int n = figure.size();
        if (n < 3) {
            throw new IllegalArgumentException("Incorrect figure vertices. Figure should be with 3 or more vertices");
        }

        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            FPoint current = figure.get(i);
            FPoint next = figure.get((i + 1) % n);
            sum += (current.x * next.y) - (next.x * current.y);
        }

        return Math.abs(sum) / 2.0;
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

    private boolean isSideBySide(FPoint p1, FPoint p2) {
        return (p1.x + 1) == p2.x || (p1.x - 1) == p2.x || (p1.y + 1) == p2.y || (p2.y - 1) == p2.y;
    }

    private FPoint calcPixelCross(FPoint luRealPixelBound) {
        return new FPoint(Math.ceil(luRealPixelBound.x), Math.ceil(luRealPixelBound.y));
    }

}
