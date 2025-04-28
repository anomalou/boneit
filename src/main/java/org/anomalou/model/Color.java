package org.anomalou.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Color {

    private int alpha;
    private int red;
    private int green;
    private int blue;

    public static Color unpack(int argb) {
        int alpha = (argb >> 24) & 255;
        int red = (argb >> 16) & 255;
        int green = (argb >> 8) & 255;
        int blue = argb & 255;

        return new Color(alpha, red, green, blue);
    }

    public int pack() {
        return (alpha << 24) + (red << 16) + (green << 8) + blue;
    }

    public Color part(double part) {
        int alpha = (int) Math.round((double) this.alpha * part);
        int red = (int) Math.round((double) this.red * part);
        int green = (int) Math.round((double) this.green * part);
        int blue = (int) Math.round((double) this.blue * part);

        return new Color(alpha, red, green, blue);
    }

}
