package com.project_ci01.app.pixel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 因为实体类实现了 Serializable 接口，但没有定义 序列化id（serialVersionUID）的值，
 * 系统会根据类的修饰符、实现接口、定义的方法以及属性等信息计算出 serialVersionUID，
 * 所以，在以后版本迭代时，PixelUnit 的程序结构（包括成员的访问修饰符）都不能变，
 * 否则无法将本地保存的对象文件反序列回来，会报错：
 * java.io.InvalidClassException:<包名>;
 * local class incompatible: stream classdesc serialVersionUID = xxx,local class serialVersionUID = xxx
 */
public class PixelList implements Serializable {

    public Map<Integer, List<PixelUnit>> colorMap; // 相同颜色的像素点分类
    public Map<Integer, String> numberMap; // 相同颜色对应的数字

    public Map<Integer, List<List<PixelUnit>>> adjoinMap; // key 为 color，value 为一组组的相邻同色集

    public List<PixelUnit> pixels; // 所有像素点的集合
    public int stdUnitSize;  // 标准的像素单元长度
    public int curUnitSize;  // 当前的（缩放后）像素单元长度

    public int originWidth; // 原始像素宽度 （实际的 = 原始的 x unitSize）
    public int originHeight; // 原始像素高度

    public PixelList(Map<Integer, List<List<PixelUnit>>> adjoinMap, Map<Integer, List<PixelUnit>> colorMap, Map<Integer, String> numberMap, List<PixelUnit> pixels, int stdUnitSize, int curUnitSize, int originWidth, int originHeight) {
        this.adjoinMap = adjoinMap;
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
                "adjoinMap.size=" + adjoinMap.size() +
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
