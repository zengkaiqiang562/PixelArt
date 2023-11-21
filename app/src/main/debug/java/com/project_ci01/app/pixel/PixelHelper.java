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
     * @param mapResult
     * Map<Integer, int[]> 某种颜色 Integer 的总像素点个数int[0] 和 已绘制个数 int[1]
     *
     * @return
     * result[0] 总像素点个数（去除白色和透明色）
     * result[1] 已绘制像素点个数（去除白色和透明色）
     */
    public static int[] countDrawnPixels(@NonNull PixelList pixelList, @NonNull Map<Integer, int[]> mapResult) {
        int[] result = new int[2];

        mapResult.clear();

        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            int color = pixel.color;
            if (PixelHelper.ignoreColor(color)) {
                continue;
            }

            int[] colorResult = mapResult.get(color);
            if (colorResult == null) {
                colorResult = new int[2];
                mapResult.put(color, colorResult);
            }

            ++result[0];
            ++colorResult[0];
            if (pixel.enableDraw) {
                ++result[1];
                ++colorResult[1];
            }
        }

        return result;
    }

    /**
     * @return
     * result[0] 总像素点个数（去除白色和透明色）
     * result[1] 已绘制像素点个数（去除白色和透明色）
     * Map<Integer, int[]> 某种颜色 Integer 的总像素点个数int[0] 和 已绘制个数 int[1]
     */
    public static int[] countDrawnPixels(@NonNull PixelList pixelList) {
        int[] result = new int[2];

        List<PixelUnit> pixels = pixelList.pixels;
        for (PixelUnit pixel : pixels) {
            if (PixelHelper.ignorePixel(pixel)) {
                continue;
            }
            ++result[0];
            if (pixel.enableDraw) {
                ++result[1];
            }
        }

        return result;
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
