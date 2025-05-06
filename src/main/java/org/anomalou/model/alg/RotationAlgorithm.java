package org.anomalou.model.alg;

import org.anomalou.model.PixelData;
import org.anomalou.utils.CoordinatesUtils;

import java.awt.*;
import java.awt.image.BufferedImage;


public interface RotationAlgorithm {

    BufferedImage rotate(BufferedImage image, double angle, Point origin);

}
