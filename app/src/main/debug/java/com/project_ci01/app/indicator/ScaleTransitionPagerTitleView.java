package com.project_ci01.app.indicator;

import android.content.Context;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * 带颜色渐变和缩放的指示器标题
 */
public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {
    private float mMinScale = 0.75f;

    private int normalFontResId;
    private int selectFontResId;

    public ScaleTransitionPagerTitleView(Context context) {
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
}
