package org.anomalou.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Author: Aleksandr Borodin
 * Creation date: 4/8/25
 */
@Getter
@Setter
@NoArgsConstructor
public class PixelData {
    
    private FPoint leftUpper;
    private FPoint rightUpper;
    private FPoint leftBottom;
    private FPoint rightBottom;
    
    public PixelData(Point luCorner) {
        this.leftUpper = new FPoint(luCorner);
        this.rightUpper = new FPoint(luCorner.x + 1, luCorner.y);
        this.leftBottom = new FPoint(luCorner.x, luCorner.y + 1);
        this.rightBottom = new FPoint(luCorner.x + 1, luCorner.y + 1);
    }

    public FPoint getLeftUpperCorner() {
        FPoint point = new FPoint();
        point.x = Stream.of(leftUpper.x, rightUpper.x, leftBottom.x, rightBottom.x).min(Double::compareTo).get();
        point.y = Stream.of(leftUpper.y, rightUpper.y, leftBottom.y, rightBottom.y).min(Double::compareTo).get();

        return point;
    }

    @Override
    public String toString() {
        return String.format("([%f;%f])([%f;%f])([%f;%f])([%f;%f])", leftUpper.x, leftUpper.y, rightUpper.x, rightUpper.y, rightBottom.x, rightBottom.y, leftBottom.x, leftBottom.y);
    }
}
