package com.project_ci01.app.pixel;

import android.graphics.Color;

import com.google.android.gms.common.util.Hex;

public class PixelUnit {
    int x;
    int y;
    int color; // ColorInt

    boolean enableDraw; // 是否绘制

    public PixelUnit(int x, int y, int color, boolean enableDraw) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.enableDraw = enableDraw;
    }

    @Override
    public String toString() {
        byte alpha = (byte) Color.alpha(color);
        byte red = (byte) Color.red(color);
        byte green = (byte) Color.green(color);
        byte blue = (byte) Color.blue(color);
        String hexColor = "#" + Hex.bytesToStringUppercase(new byte[]{alpha, red, green, blue});
        return "Pixel{" +
                "x=" + x +
                ", y=" + y +
                ", color=" + hexColor +
                ", isDrawn=" + enableDraw +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PixelUnit pixelUnit = (PixelUnit) o;

        if (x != pixelUnit.x) return false;
        return y == pixelUnit.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
