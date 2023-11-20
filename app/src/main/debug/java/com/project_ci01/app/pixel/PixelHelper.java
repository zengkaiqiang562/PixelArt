package com.project_ci01.app.pixel;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixelHelper {
    private static final String TAG = "PixelHelper";

    /**
     * @param result
     * result[0] 总像素点个数（去除白色和透明色）
     * result[1] 已绘制像素点个数（去除白色和透明色）
     */
    public static void countDrawnPixels(@NonNull PixelList pixelList, int[] result) {
        int total = 0; // 总像素点（去除白色和透明色）
        int drawn = 0; // 已绘制像素点（去除白色和透明色）

        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            if (PixelHelper.ignorePixel(pixel)) {
                continue;
            }
            ++total;
            if (pixel.enableDraw) {
                ++drawn;
            }
        }

        result[0] = total;
        result[1] = drawn;
    }


    /**
     *
     * @return 检查是否要处理 pixel
     * 目前 不处理白色和透明
     */
    public static boolean ignorePixel(@NonNull PixelUnit pixel) {
        // 不处理白色和透明
        return pixel.color == Color.WHITE || pixel.color == Color.TRANSPARENT;
    }

    public static boolean ignoreColor(int color) {
        // 不处理白色和透明
        return color == Color.WHITE || color == Color.TRANSPARENT;
    }
}
