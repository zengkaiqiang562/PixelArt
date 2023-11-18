package com.project_ci01.app.base.utils;

import com.blankj.utilcode.constant.MemoryConstants;

public class MyConvertUtils {
    public static String byte2FitMemorySize(final long byteSize, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision shouldn't be less than zero!");
        }
        if (byteSize < 0) {
            throw new IllegalArgumentException("byteSize shouldn't be less than zero!");
        } else if (byteSize < MemoryConstants.KB) {
            return String.format("%." + precision + "fb", (double) byteSize);
        } else if (byteSize < MemoryConstants.MB) {
            return String.format("%." + precision + "fKb", (double) byteSize / MemoryConstants.KB);
        } else if (byteSize < MemoryConstants.GB) {
            return String.format("%." + precision + "fMb", (double) byteSize / MemoryConstants.MB);
        } else {
            return String.format("%." + precision + "fGb", (double) byteSize / MemoryConstants.GB);
        }
    }

    public static String byte2FitMemoryValue(final long byteSize, int precision) {
        if (precision < 0) {
            throw new IllegalArgumentException("precision shouldn't be less than zero!");
        }
        if (byteSize < 0) {
            throw new IllegalArgumentException("byteSize shouldn't be less than zero!");
        } else if (byteSize < MemoryConstants.KB) {
            return NumUtils.formatFloatValueToStr(byteSize * 1f, precision);
        } else if (byteSize < MemoryConstants.MB) {
            return NumUtils.formatFloatValueToStr(byteSize * 1f / MemoryConstants.KB, precision);
        } else if (byteSize < MemoryConstants.GB) {
            return NumUtils.formatFloatValueToStr(byteSize * 1f / MemoryConstants.MB, precision);
        } else {
            return  NumUtils.formatFloatValueToStr(byteSize * 1f / MemoryConstants.GB, precision);
        }
    }

    public static String byte2FitMemoryUnit(final long byteSize) {
        if (byteSize < 0) {
            throw new IllegalArgumentException("byteSize shouldn't be less than zero!");
        } else if (byteSize < MemoryConstants.KB) {
            return "b";
        } else if (byteSize < MemoryConstants.MB) {
            return "Kb";
        } else if (byteSize < MemoryConstants.GB) {
            return "Mb";
        } else {
            return "Gb";
        }
    }
}
