package com.project_m1142.app.base.utils;

import android.graphics.Color;

import com.google.android.gms.common.util.Hex;

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

    /**
     * 转灰度
     */
    public static int convertGrey(int color) {
        byte alpha = (byte) (color >>> 24);
        byte red = (byte) ((color >> 16) & 0xFF);
        byte green = (byte) ((color >> 8) & 0xFF);
        byte blue = (byte) (color & 0xFF);

        /*
        ConversionFactor = 255 / (NumberOfShades - 1)
        AverageValue = (Red + Green + Blue) / 3
        Gray = Math.round((AverageValue / ConversionFactor) + 0.5) * ConversionFactor
         */
//        int numShades = 64; // [2-256]
//        float conFactor = 255f / (numShades - 1);
//        byte avgValue = (byte) ((red + green + blue) / 3);
//        int grey = (int) (Math.round((avgValue / conFactor) + 0.5f) * conFactor);

//        int grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
//        int grey = (int) (red * 0.299 + green * 0.587 + blue * 0.114);
//        int grey = (int) ((red + green + blue) / 3f);
//        int grey = (red*38 + green*75 + blue*15) >> 7;

//        return Color.argb(alpha, grey, grey, grey);
//        return Color.argb(alpha, 255 - grey, 255 - grey, 255 - grey); // 反转

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
//        hsv = new float[]{0f, 0f, hsv[2] * 1.1f};
        hsv[1] = 0f; // 灰色图
        LogUtils.e("zkq", "--> hsv[2]=" + hsv[2]);
//        hsv[2] = hsv[2] * 50f; // 让黑色变浅
//        hsv[2] = 0.2f; // 让黑色变浅  [0,1] 变化，值越小越黑
        int newColor = Color.HSVToColor(hsv);
//        if (newColor != Color.WHITE) {
//            LogUtils.e("zkq", "-->  newColor="
//                    + "#" + Hex.bytesToStringUppercase(new byte[]{(byte) Color.alpha(newColor),
//                    (byte) Color.red(newColor),
//                    (byte) Color.green(newColor),
//                    (byte) Color.blue(newColor)}));
//        }

        // 因为设置了 alpha，所以 drawColorBitmap 和 drawNumberBitmap 之前要加一层白色，避免 alpha 透过看到原图
        return Color.argb(Math.round(Color.alpha(newColor) * 0.3f), Color.red(newColor), Color.green(newColor), Color.blue(newColor));
//        return newColor;

    }


}
