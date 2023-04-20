package org.anomalou.model;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;

public class Vector2D implements Serializable {
    @Getter
    @Setter
    private FPoint origin;
    @Getter
    @Setter
    private FPoint direction;

    public Vector2D(FPoint origin, FPoint direction) {
        this.origin = origin;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return String.format("[%f; %f] -> [%f; %f]", origin.x, origin.y, direction.x, direction.y);
    }
}
