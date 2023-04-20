package org.anomalou.model.tools;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Palette {
    @Getter
    @Setter
    private Color foregroundColor;
    @Getter
    @Setter
    private Color backgroundColor;

    public Palette(){
        foregroundColor = Color.black;
        backgroundColor = Color.white;
    }


}
