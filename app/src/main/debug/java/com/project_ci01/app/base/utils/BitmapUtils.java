package com.project_ci01.app.base.utils;

import android.graphics.Color;

public class BitmapUtils {

    /**
     * 转黑白
     */
    public static int convertBW(int color) {
        byte alpha = (byte) (color >>> 24);
        byte red = (byte) ((color >> 16) & 0xFF);
        byte green = (byte) ((color >> 8) & 0xFF);
        byte blue = (byte) (color & 0xFF);
        //拼凑出新的颜色
        int grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
        if (grey > 255 / 2) {
            grey = 255;
        } else {
            grey = 0;
        }
        return Color.argb(alpha, grey, grey, grey);
    }

    /*
    转灰度方法1：
    byte alpha = (byte) (color >>> 24);
    byte red = (byte) ((color >> 16) & 0xFF);
    byte green = (byte) ((color >> 8) & 0xFF);
    byte blue = (byte) (color & 0xFF);

    int numShades = 64; // [2-256]
    float conFactor = 255f / (numShades - 1);
    byte avgValue = (byte) ((red + green + blue) / 3);
    int grey = (int) (Math.round((avgValue / conFactor) + 0.5f) * conFactor);
    return Color.argb(alpha, grey, grey, grey);

    转灰度方法2：
    int grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
    return Color.argb(alpha, grey, grey, grey);

    转灰度方法3：
    int grey = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
    return Color.argb(alpha, grey, grey, grey);

    转灰度方法4：
    int grey = (int) ((red + green + blue) / 3f);
    return Color.argb(alpha, grey, grey, grey);

    转灰度方法5：
    int grey = (red*38 + green*75 + blue*15) >> 7;
    return Color.argb(alpha, grey, grey, grey);

    转灰度方法6：
    float[] hsv = new float[3];
    Color.colorToHSV(color, hsv);
    hsv[1] = 0f; // 去饱和度后 可以得到 灰色图
    return Color.HSVToColor(hsv);

    转灰度方法7：
    int color = pixel.color;
    int red = Color.red(color);
    int blue = Color.blue(color) * 29;
    int green = ((blue + ((Color.green(color) * 150) + (red * 77))) + 128) >> 8;
    color = Color.argb(255, green, green, green);
    */

    /**
     * 转灰度（采用方法7）
     */
    public static int convertGrey(int color) {
        int red = Color.red(color);
        int blue = Color.blue(color) * 29;
        int green = ((blue + ((Color.green(color) * 150) + (red * 77))) + 128) >> 8;
        return Color.argb(255, green, green, green);
    }


}
