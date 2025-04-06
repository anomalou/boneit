package org.anomalou.model.alg;

import java.awt.image.BufferedImage;

@FunctionalInterface
public interface RotationAlgorithm {

    BufferedImage rotate(BufferedImage image, float angle);

}
