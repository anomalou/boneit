package org.anomalou.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

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
    
}
