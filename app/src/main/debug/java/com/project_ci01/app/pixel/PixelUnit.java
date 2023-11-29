package com.project_ci01.app.pixel;

import android.graphics.Color;

import org.apache.commons.codec.binary.Hex;

import java.io.Serializable;

/**
 * 因为实体类实现了 Serializable 接口，但没有定义 序列化id（serialVersionUID）的值，
 * 系统会根据类的修饰符、实现接口、定义的方法以及属性等信息计算出 serialVersionUID，
 * 所以，在以后版本迭代时，PixelUnit 的程序结构（包括成员的访问修饰符）都不能变，
 * 否则无法将本地保存的对象文件反序列回来，会报错：
 * java.io.InvalidClassException:<包名>;
 * local class incompatible: stream classdesc serialVersionUID = xxx,local class serialVersionUID = xxx
 */
public class PixelUnit implements Serializable {
    public short x;
    public short y;
    public int color; // ColorInt

    public boolean enableDraw; // 是否绘制

    public PixelUnit(int x, int y, int color, boolean enableDraw) {
        this.x = (short) x;
        this.y = (short) y;
        this.color = color;
        this.enableDraw = enableDraw;
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
