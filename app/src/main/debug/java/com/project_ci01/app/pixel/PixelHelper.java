package com.project_ci01.app.pixel;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PixelHelper {
    private static final String TAG = "PixelHelper";
    /**
     * @param bitmap
     * @param unit 图片像素化时的像素单元长度
     */
    public static PixelList getAllPixels(@NonNull Bitmap bitmap, int unit) { // bitmap 像素点太多时，会内存溢出，所以 bitmap 一定是很小很小的原图
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        List<PixelUnit> pixels = new ArrayList<>();
        Map<Integer, List<PixelUnit>> colorMap = new HashMap<>();
        PixelUnit pixel;
        for (int x = 0; x < bitmapWidth; x++) { // 按列遍历（先垂直遍历[内]，再水平遍历[外]）
            for (int y = 0; y < bitmapHeight; y++) {

                int color = bitmap.getPixel(x, y);
                pixel = new PixelUnit(x, y, color, false);

                // 添加到总集合
                pixels.add(pixel);

                // 添加到按颜色分类的集合
                List<PixelUnit> colorPixels = colorMap.get(color);
                if (colorPixels == null) {
                    colorPixels = new ArrayList<>();
                    colorMap.put(color, colorPixels);
                }
                colorPixels.add(pixel);
            }
        }

        Map<Integer, String> numberMap = new HashMap<>(); // 颜色-Number 键值对
        List<Map.Entry<Integer, List<PixelUnit>>> colorEntries = new ArrayList<>(colorMap.entrySet());
        Collections.sort(colorEntries, (o1, o2) -> { // 同颜色像素点多的排在前面
            int size1 = o1.getValue().size();
            int size2 = o2.getValue().size();
            return size2 - size1;
        });

        int number = 1;
        for (int index = 0; index < colorEntries.size(); index++) {
            Integer color = colorEntries.get(index).getKey();
            if (color == Color.WHITE || color == Color.TRANSPARENT) { // 不处理白色和透明
                continue;
            }
            numberMap.put(color, String.valueOf(number));
            number++;
        }

        LogUtils.e(TAG, "--> getAllPixels()  numberMap=" + numberMap);

        return new PixelList(colorMap, numberMap, pixels, unit, unit, bitmapWidth, bitmapHeight);
    }
}
