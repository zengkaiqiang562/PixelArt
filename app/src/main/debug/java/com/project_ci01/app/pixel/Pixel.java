package com.project_ci01.app.pixel;

import android.graphics.Color;

import org.apache.commons.codec.binary.Hex;

public class Pixel {
    int x;
    int y;
    int color; // ColorInt
    int unit; // 像素单元长度

    public Pixel(int x, int y, int color, int unit) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.unit = unit;
    }

    @Override
    public String toString() {
        byte alpha = (byte) Color.alpha(color);
        byte red = (byte) Color.red(color);
        byte green = (byte) Color.green(color);
        byte blue = (byte) Color.blue(color);
        String hexColor = "#" + Hex.encodeHexString(new byte[]{alpha, red, green, blue}, false);
        return "Pixel{" +
                "x=" + x +
                ", y=" + y +
                ", color=" + hexColor +
                ", unit=" + unit +
                '}';
    }
}
