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
            if (realPixel.x >= 0 && realPixel.x < dest.getWidth() &&
                    realPixel.y >= 0 && realPixel.y < dest.getHeight()) {

                dest.setRGB((int) Math.round(realPixel.x), (int) Math.round(realPixel.y), source.getRGB(pixel.x, pixel.y));
            }
        } else if ((Double.compare(pixelData.getLeftUpper().x, pixelData.getRightBottom().x) == 0 && pixelData.getLeftUpper().x % 1 == 0) ||
                    (Double.compare(pixelData.getRightUpper().x, pixelData.getLeftBottom().x) == 0 && pixelData.getRightUpper().x % 1 == 0)) {
            // pixel opposite corners lay on Y axis

            if (pixelData.getLeftUpper().x % 1 == 0) {
                Line l1 = new Line(pixelData.getLeftBottom(), pixelData.getLeftUpper());
                Line l2 = new Line(pixelData.getLeftBottom(), pixelData.getRightBottom());

                FPoint cornerPixel = findPixel(pixelData.getLeftBottom());

                FPoint int1 = findIntersectionWithPixel(l1, cornerPixel);
                FPoint int2 = findIntersectionWithPixel(l2, cornerPixel);

                // situation when pixel on two pixels
                if (Double.compare(int1.x, pixelData.getLeftUpper().x) == 0 || Double.compare(int2.x, pixelData.getLeftUpper().x) == 0) {
                    FPoint crossPoint = calcPixelCross(pixelData.getLeftUpperBound());
                    FPoint rectPixel;
                    double pentagonPart = calculateSquare(List.of(pixelData.getLeftBottom(), int1, crossPoint, int2));
                    double rectanglePart = Math.round((1.0 - pentagonPart * 2) / 2 * 1e6) / 1e6;

                    if (int1.x % 1 == 0) {
                        rectPixel = findPixel(pixelData.getLeftUpper());
                    } else {
                        rectPixel = findPixel(pixelData.getRightBottom());
                    }

                    drawColor(source, dest, pixel, new FPoint(crossPoint.x - 1, crossPoint.y), pentagonPart);
                    drawColor(source, dest, pixel, crossPoint, pentagonPart);

                    drawColor(source, dest, pixel, new FPoint(rectPixel.x - 1, rectPixel.y), rectanglePart);
                    drawColor(source, dest, pixel, rectPixel, rectanglePart);
                } else {

                }
            } else {

            }

            // pixel center in cross of four pixels
            if (pixelData.getLeftUpper().y % 1 == 0 || pixelData.getRightUpper().y % 1 == 0) {
                FPoint crossPixel = calcPixelCross(pixelData.getLeftUpperBound());

                double part = 1.0 / 4.0;

                FPoint destPixel = new FPoint(crossPixel.x - 1, crossPixel.y - 1);
                drawColor(source, dest, pixel, destPixel, part);

                destPixel = new FPoint(crossPixel.x - 1, crossPixel.y);
                drawColor(source, dest, pixel, destPixel, part);

                destPixel = new FPoint(crossPixel.x, crossPixel.y - 1);
                drawColor(source, dest, pixel, destPixel, part);

                destPixel = new FPoint(crossPixel.x, crossPixel.y);
                drawColor(source, dest, pixel, destPixel, part);

            } else {
                // same but another side (looks like very rare situation so will implement it late)
            }
        } else {
            // Calculate pixel areas squares
            Set<PixelCorner> pixelCorners = new HashSet<>();

            double partSum = 0;
            int triangleNumber = 0;
            FPoint pentagonPixel = null;

            for (PixelCorner corner : PixelCorner.values()) {
                if (!pixelCorners.contains(corner)) {
                    List<FPoint> figureCorners = calculateFigure(pixelData, corner, pixelCorners);
                    // calculate square here
                    // paint corner pixel in color by square proportion

                    if (figureCorners.size() == 3) {
                        // triangle
                        triangleNumber += 1;
                        FPoint destPixel = findPixel(figureCorners.get(0));
                        double part = calculateSquare(figureCorners);
                        partSum += part;

                        if (destPixel.x >= 0 && destPixel.x < dest.getWidth() &&
                            destPixel.y >= 0 && destPixel.y < dest.getHeight()) {

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
                        double part = calculateSquare(figureCorners);
                        partSum += part;

                        if (destPixel.x >= 0 && destPixel.x < dest.getWidth() &&
                            destPixel.y >= 0 && destPixel.y < dest.getHeight()) {

                            Color sourceColor = Color.unpack(source.getRGB(pixel.x, pixel.y));
                            Color destColor = Color.unpack(dest.getRGB((int) destPixel.x, (int) destPixel.y));
                            Color destPartColor = destColor.plus(sourceColor.part(part));

                            dest.setRGB((int) destPixel.x, (int) destPixel.y, destPartColor.pack());
                        }
                    }
                }
            }

            if (triangleNumber == 4 && partSum < 1.0) {
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
            // partSum may be not = 1.0
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
            } else if (isSideBySide(line1Intersection, line2Intersection) && !checkIfPixelCross(line1Intersection) && !checkIfPixelCross(line2Intersection)){
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
            sum += (Math.round(current.x * next.y * 1e6) / 1e6) - (Math.round(next.x * current.y * 1e6) / 1e6);
        }

        return Math.abs(sum) / 2.0;
    }
    
    private FPoint findIntersectionWithPixel(Line line, FPoint pixel) {
        FPoint hIntersection = line.findIntersectionHorisontal(pixel.y);
        FPoint vIntersection = line.findIntersectionVertical(pixel.x);
        
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
        return Math.abs(p1.x - p2.x) == 1 || Math.abs(p1.y - p2.y) == 1;
    }

    private FPoint calcPixelCross(FPoint luRealPixelBound) {
        return new FPoint(Math.ceil(luRealPixelBound.x), Math.ceil(luRealPixelBound.y));
    }

    private Color appendColor(int sColor, int dColor, double part) {
        Color sourceColor = Color.unpack(sColor);
        Color destColor = Color.unpack(dColor);
        return destColor.plus(sourceColor.part(1.0 - part));
    }

    private void drawColor(BufferedImage source, BufferedImage dest, Point sPixel, FPoint dPixel, double part) {
        if (dPixel.x >= 0 && dPixel.x < dest.getWidth() &&
            dPixel.y >= 0 && dPixel.y < dest.getHeight()) {

            Color destColor = appendColor(source.getRGB(sPixel.x, sPixel.y), dest.getRGB((int) dPixel.x, (int) dPixel.y), part);
            dest.setRGB((int) dPixel.x, (int) dPixel.y, destColor.pack());
        }
    }

    // check if intersection point lay on cross of four pixels
    private boolean checkIfPixelCross(FPoint p) {
        return (p.x % 1 == 0 && p.y % 1 == 0);
    }

}
