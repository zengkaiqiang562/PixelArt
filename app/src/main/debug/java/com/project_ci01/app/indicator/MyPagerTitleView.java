package com.project_ci01.app.indicator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

import androidx.core.content.res.ResourcesCompat;

import com.blankj.utilcode.util.ConvertUtils;

import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * 带颜色渐变和缩放的指示器标题
 */
public class MyPagerTitleView extends ColorTransitionPagerTitleView {
    private float mMinScale = 0.75f;

    private int normalFontResId;
    private int selectFontResId;

    public MyPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);

        if (selectFontResId != 0) {
            Typeface selectFont = ResourcesCompat.getFont(getContext(), selectFontResId);
            setTypeface(selectFont);
        }

        int normalBgColor = Color.parseColor("#FFF7F0F2");
        int selectedBgColor = Color.parseColor("#FFFFB6CC");
        int color = ArgbEvaluatorHolder.eval(enterPercent, normalBgColor, selectedBgColor);
        int dp12 = ConvertUtils.dp2px(12);
        float [] corners = new float[] {dp12, dp12, dp12, dp12, dp12, dp12, dp12, dp12};
        RoundRectShape roundRectShape = new RoundRectShape(corners, null , null);
        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
        Paint drawablePaint = drawable.getPaint();
        drawablePaint.setColor(color);
        setBackground(drawable);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);

        if (normalFontResId != 0) {
            Typeface normalFont = ResourcesCompat.getFont(getContext(), normalFontResId);
            setTypeface(normalFont);
        }

        int normalBgColor = Color.parseColor("#FFF7F0F2");
        int selectedBgColor = Color.parseColor("#FFFFB6CC");
        int color = ArgbEvaluatorHolder.eval(leavePercent, selectedBgColor, normalBgColor);
        int dp12 = ConvertUtils.dp2px(12);
        float [] corners = new float[] {dp12, dp12, dp12, dp12, dp12, dp12, dp12, dp12};
        RoundRectShape roundRectShape = new RoundRectShape(corners, null , null);
        ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
        Paint drawablePaint = drawable.getPaint();
        drawablePaint.setColor(color);
        setBackground(drawable);
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

    public void setNormalFontResId(int normalFontResId) {
        this.normalFontResId = normalFontResId;
    }

    public void setSelectFontResId(int selectFontResId) {
        this.selectFontResId = selectFontResId;
    }



    @Override
    public int getContentLeft() {
        return getLeft();
    }

    @Override
    public int getContentTop() {
        return 0;
    }

    @Override
    public int getContentRight() {
        return getLeft() + getWidth();
    }

    @Override
    public int getContentBottom() {
        return getHeight() ;
    }
}
