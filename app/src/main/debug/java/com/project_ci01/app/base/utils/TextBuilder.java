package com.project_ci01.app.base.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public final class TextBuilder {

    private final SpannableStringBuilder ssb;

    public TextBuilder(@NonNull CharSequence text) {
        ssb = new SpannableStringBuilder(text);
    }

    public TextBuilder setColor(@ColorInt int color, int start, int end) {
        ssb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return this;
    }

    public TextBuilder setSize(int textSize, int start, int end) {
        ssb.setSpan(new AbsoluteSizeSpan(textSize, true), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return this;
    }

    /**
     * 格式化指定范围内的文本粗细，斜体等样式，
     * @param style 指定文本样式 {@link android.graphics.Typeface}
     */
    public TextBuilder setTextStyle(int style, int start, int end) {
        ssb.setSpan(new StyleSpan(style), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return this;
    }

    public TextBuilder setUnderline(int start, int end) {
        ssb.setSpan(new UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return this;
    }

    public TextBuilder setClick(View.OnClickListener listener, int start, int end) {
        ssb.setSpan(new Clickable(listener), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return this;
    }

    public SpannableStringBuilder build() {
        return ssb;
    }

    /**
     * TextView 中部分文字可点击
     */
    private static class Clickable extends ClickableSpan {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        /**
         * 点击事件
         */
        @Override
        public void onClick(View v) {
            mListener.onClick(v);
            if (v instanceof TextView) {
                ((TextView)v).setHighlightColor(Color.TRANSPARENT);
            }
        }

        /**
         * 可以给TextView设置字体颜色,背景颜色等
         */
        @Override
        public void updateDrawState(TextPaint ds) {
        }
    }
}
