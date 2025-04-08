package org.anomalou.model.alg;

import org.anomalou.model.PixelData;
import org.anomalou.utils.CoordinatesUtils;

import java.awt.*;
import java.awt.image.BufferedImage;


public interface RotationAlgorithm {

    BufferedImage rotate(BufferedImage image, double angle, Point origin);
    
    default PixelData rotatePixel(Point luCorner, double angle, Point origin) {
        PixelData pixelData = new PixelData(luCorner);
        
        pixelData.setLeftUpper(CoordinatesUtils.rotatePoint(pixelData.getLeftUpper(), angle, origin));
        pixelData.setRightUpper(CoordinatesUtils.rotatePoint(pixelData.getRightUpper(), angle, origin));
        pixelData.setLeftBottom(CoordinatesUtils.rotatePoint(pixelData.getLeftBottom(), angle, origin));
        pixelData.setRightBottom(CoordinatesUtils.rotatePoint(pixelData.getRightBottom(), angle, origin));
        
        return pixelData;
    }

}
