package com.project_ci01.app.pixel;

import java.util.List;
import java.util.Map;

public class PixelList {

    public Map<Integer, List<PixelUnit>> colorMap; // 相同颜色的像素点分类
    public Map<Integer, String> numberMap; // 相同颜色对应的数字

    public List<PixelUnit> pixels;
    public int stdUnitSize;  // 标准的像素单元长度
    public int curUnitSize;  // 当前的（缩放后）像素单元长度

    public int originWidth; // 原始像素宽度 （实际的 = 原始的 x unitSize）
    public int originHeight; // 原始像素高度

    public PixelList(Map<Integer, List<PixelUnit>> colorMap, Map<Integer, String> numberMap, List<PixelUnit> pixels, int stdUnitSize, int curUnitSize, int originWidth, int originHeight) {
        this.colorMap = colorMap;
        this.numberMap = numberMap;
        this.pixels = pixels;
        this.stdUnitSize = stdUnitSize;
        this.curUnitSize = curUnitSize;
        this.originWidth = originWidth;
        this.originHeight = originHeight;
    }

    @Override
    public String toString() {
        return "PixelList{" +
                "colorMap.size=" + colorMap.size() +
                "pixels.size=" + pixels.size() +
                ", stdUnitSize=" + stdUnitSize +
                ", curUnitSize=" + curUnitSize +
                ", originWidth=" + originWidth +
                ", originHeight=" + originHeight +
                '}';
    }

    public int stdWidth() {
        return originWidth * stdUnitSize;
    }

    public int stdHeight() {
        return originHeight * stdUnitSize;
    }

    public int realWidth() {
        return originWidth * curUnitSize;
    }

    public int realHeight() {
        return originHeight * curUnitSize;
    }
}
