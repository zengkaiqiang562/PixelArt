package com.project_ci01.app.pixel;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PixelHelper {
    /**
     * @param bitmap
     * @param unit 图片像素化时的像素单元长度
     */
    public static List<Pixel> getAllPixels(@NonNull Bitmap bitmap, int unit) { // bitmap 像素点太多时，会内存溢出，所以 bitmap 一定是很小很小的原图
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        List<Pixel> pixels = new ArrayList<>();
        for (int x = 0; x < bitmapWidth; x++) {
            for (int y = 0; y < bitmapHeight; y++) {
                int color = bitmap.getPixel(x, y);
                pixels.add(new Pixel(x, y, color, unit));
            }
        }
        return pixels;
    }
}
