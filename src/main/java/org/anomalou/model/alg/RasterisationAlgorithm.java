package org.anomalou.model.alg;

import org.anomalou.model.Color;
import org.anomalou.model.FPoint;
import org.anomalou.model.Line;
import org.anomalou.utils.CoordinatesUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

public class RasterisationAlgorithm implements TransformationAlgorithm{
    @Override
    public BufferedImage rotate(BufferedImage image, double angle, Point origin) {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        drawRectangle(buffer, image, angle, origin,
        List.of(
            new FPoint(0, 0),
            new FPoint(buffer.getWidth() - 1, buffer.getHeight() - 1),
            new FPoint(0, buffer.getHeight() - 1)
        ),
        List.of(
            new FPoint(0, 0),
            new FPoint(1, 1),
            new FPoint(0, 1)
        ), new Color(255, 255, 0, 0));

        drawRectangle(buffer, image, angle, origin,
        List.of(
            new FPoint(0, 0),
            new FPoint(buffer.getWidth() - 1, 0),
            new FPoint(buffer.getWidth() - 1, buffer.getHeight() - 1)
        ),
        List.of(
            new FPoint(0, 0),
            new FPoint(1, 0),
            new FPoint(1, 1)
        ), new Color(255, 0, 0, 255));

        return buffer;
    }

    private void drawRectangle(BufferedImage dest, BufferedImage texture, double angle, Point pivot, List<FPoint> points, List<FPoint> texPoints, Color color) {
        FPoint p1 = CoordinatesUtils.rotatePoint(points.get(0), angle, pivot);
        FPoint p2 = CoordinatesUtils.rotatePoint(points.get(1), angle, pivot);
        FPoint p3 = CoordinatesUtils.rotatePoint(points.get(2), angle, pivot);

        FPoint t1 = texPoints.get(0);
        FPoint t2 = texPoints.get(1);
        FPoint t3 = texPoints.get(2);

        if (p2.y < p1.y) {
            swapPoints(p2, p1);
            swapPoints(t2, t1);
        }
        if (p3.y < p1.y) {
            swapPoints(p3, p1);
            swapPoints(t3, t1);
        }
        if (p3.y < p2.y) {
            swapPoints(p3, p2);
            swapPoints(t3, t2);
        }

        Line l12 = new Line(p1, p2);
        Line l23 = new Line(p2, p3);
        Line l13 = new Line(p1, p3); // the longest rect side

        double area = (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
        double c1 = (p3.y - p1.y) * (1.0 / area);
        double c2 = -(p3.x - p1.x) * (1.0 / area);
        double c3 = -(p2.y - p1.y) * (1.0 / area);
        double c4 = (p2.x - p1.x) * (1.0 / area);

        Line left, right;
        if (l12.center().x < l13.center().x) {
            left = l12;
            right = l13;
        } else {
            left = l13;
            right = l12;
        }

        double beta, gamma, alpha;

        for (double y = p1.y; y < p2.y; y += 1) {
            FPoint leftSide = left.interpolate(y);
            FPoint rightSide = right.interpolate(y);
            for (double x = leftSide.x; x <= rightSide.x; x += 1) {
                beta = ((x - p1.x) * c1 + (y - p1.y) * c2);
                gamma = ((x - p1.x) * c3 + (y - p1.y) * c4);
                alpha = 1.0 - beta - gamma;
                FPoint texturePoint = new FPoint(
                        alpha * t1.x + beta * t2.x + gamma * t3.x,
                        alpha * t1.y + beta * t2.y + gamma * t3.y
                );
                drawPixel(dest, (int) Math.round(x), (int) Math.round(y), color);
                Color pixelColor = getPixel(texture, texturePoint);
                if (Objects.nonNull(pixelColor)) {
                    drawPixel(dest, (int) Math.round(x), (int) Math.round(y), pixelColor);
                }
            }
        }

        if (l23.center().x < l13.center().x) {
            left = l23;
            right = l13;
        } else {
            left = l13;
            right = l23;
        }

        for (double y = p2.y; y < p3.y; y += 1) {
            FPoint leftSide = left.interpolate(y);
            FPoint rightSide = right.interpolate(y);
            for (double x = leftSide.x; x <= rightSide.x; x += 1) {
                beta = ((x - p1.x) * c1 + (y - p1.y) * c2);
                gamma = ((x - p1.x) * c3 + (y - p1.y) * c4);
                alpha = 1.0 - beta - gamma;
                FPoint texturePoint = new FPoint(
                        alpha * t1.x + beta * t2.x + gamma * t3.x,
                        alpha * t1.y + beta * t2.y + gamma * t3.y
                );
                drawPixel(dest, (int) Math.round(x), (int) Math.round(y), color);
                Color pixelColor = getPixel(texture, texturePoint);
                if (Objects.nonNull(pixelColor)) {
                    drawPixel(dest, (int) Math.round(x), (int) Math.round(y), pixelColor);
                }
            }
        }

    }

    private void swapPoints(FPoint p1, FPoint p2) {
        FPoint temp = new FPoint(p1.x, p1.y);
        p1.x = p2.x;
        p1.y = p2.y;
        p2.x = temp.x;
        p2.y = temp.y;
    }

    private void drawPixel(BufferedImage dest, int x, int y, Color color) {
        if (x >= 0 && x < dest.getWidth() && y >= 0 && y < dest.getHeight()) {
            dest.setRGB(x, y, color.pack());
        }
    }

    private Color getPixel(BufferedImage texture, FPoint texPoint) {
        texPoint.x = texPoint.x % 1;
        texPoint.y = texPoint.y % 1;

        texPoint.x = texPoint.x * texture.getWidth();
        texPoint.y = texPoint.y * texture.getHeight();

        if (texPoint.x >= 0 && texPoint.x < texture.getWidth() && texPoint.y >= 0 && texPoint.y < texture.getHeight()) {
            return Color.unpack(texture.getRGB((int) Math.floor(texPoint.x), (int) Math.floor(texPoint.y)));
        } else {
            return null;
        }
    }

}
