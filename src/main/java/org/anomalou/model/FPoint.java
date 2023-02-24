package org.anomalou.model;

import java.io.Serializable;

public class FPoint implements Serializable {
    public double x;
    public double y;

    public FPoint(){
        x = 0d;
        y = 0d;
    }

    public FPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return String.format("[%f:%f]", x, y);
    }
}
