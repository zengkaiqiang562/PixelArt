package com.project_ci01.app.base.utils;

import androidx.annotation.StringRes;

import com.project_ci01.app.base.manage.LifecyclerManager;

import java.util.regex.Pattern;

public class StringUtils {

    public static String trimQuote(String src) {// 去掉头尾的 "
        return Pattern.compile("^\"|\"$").matcher(src).replaceAll("");
    }

    public static String trimSuffix(String fileName) {// 去掉文件后缀
        return Pattern.compile("\\.[a-zA-Z0-9_]+$").matcher(fileName).replaceAll("");
    }

    public static String getString(@StringRes int resId) {
        return LifecyclerManager.INSTANCE.getApplication().getString(resId);
    }

    public static String getString(@StringRes int resId, Object... formatArgs) {
        return LifecyclerManager.INSTANCE.getApplication().getString(resId, formatArgs);
    }

    /**
     * Determine if {@code a} contains {@code b} in a case sensitive manner.
     */
    public static boolean contains(CharSequence a, CharSequence b) {
        return contains(a, b, DefaultCharEqualityComparator.INSTANCE);
    }

    /**
     * Determine if {@code a} contains {@code b} in a case insensitive manner.
     */
    public static boolean containsIgnoreCase(CharSequence a, CharSequence b) {
        return contains(a, b, AsciiCaseInsensitiveCharEqualityComparator.INSTANCE);
    }

    private static boolean contains(CharSequence a, CharSequence b, CharEqualityComparator cmp) {
        if (a == null || b == null || a.length() < b.length()) {
            return false;
        }
        if (b.length() == 0) { // 任何字符串 都包含 空字符串
            return true;
        }
        int bStart = 0;
        for (int i = 0; i < a.length(); ++i) {
            if (cmp.equals(b.charAt(bStart), a.charAt(i))) {
                // If b is consumed then true.
                if (++bStart == b.length()) {
                    return true;
                }
            } else if (a.length() - i < b.length()) {
                // If there are not enough characters left in a for b to be contained, then false.
                return false;
            } else {
                bStart = 0;
            }
        }
        return false;
    }

    private interface CharEqualityComparator {
        boolean equals(char a, char b);
    }

    private static final class DefaultCharEqualityComparator implements CharEqualityComparator {
        static final DefaultCharEqualityComparator INSTANCE = new DefaultCharEqualityComparator();
        private DefaultCharEqualityComparator() { }

        @Override
        public boolean equals(char a, char b) {
            return a == b;
        }
    }

    private static final class AsciiCaseInsensitiveCharEqualityComparator implements CharEqualityComparator {
        static final AsciiCaseInsensitiveCharEqualityComparator
                INSTANCE = new AsciiCaseInsensitiveCharEqualityComparator();
        private AsciiCaseInsensitiveCharEqualityComparator() { }

        @Override
        public boolean equals(char a, char b) {
            return equalsIgnoreCase(a, b);
        }
    }

    private static boolean equalsIgnoreCase(char a, char b) {
        return a == b || toLowerCase(a) == toLowerCase(b);
    }

    public static char toLowerCase(char c) {
        return isUpperCase(c) ? (char) (c + 32) : c;
    }

    public static boolean isUpperCase(char value) {
        return value >= 'A' && value <= 'Z';
    }
}
