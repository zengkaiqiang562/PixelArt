package com.project_ci01.app.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.blankj.utilcode.util.ConvertUtils;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;


/**
 * 包裹住内容区域的指示器，类似天天快报的切换效果，需要和IMeasurablePagerTitleView配合使用
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class MyPagerIndicator extends View implements IPagerIndicator {
    private float mVerticalPadding;
    private float mHorizontalPadding;
    private int mFillColor;
    private float mRoundRadius;
    private Interpolator mStartInterpolator = new LinearInterpolator();
    private Interpolator mEndInterpolator = new LinearInterpolator();

    private List<PositionData> mPositionDataList;
//    private Paint mPaint;
    private Paint mStrokePaint;

    private RectF mRect = new RectF();
    private boolean mRoundRadiusSet;

    public MyPagerIndicator(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setStyle(Paint.Style.FILL);
//        mPaint.setColor(Color.parseColor("#FFFFB6CC"));

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setColor(Color.parseColor("#FF2E2A2B"));
        mStrokePaint.setStrokeWidth(ConvertUtils.dp2px(2));

//        mVerticalPadding = UIUtil.dip2px(context, 6);
//        mHorizontalPadding = UIUtil.dip2px(context, 10);
        float density = context.getResources().getDisplayMetrics().density;
        float dp05 = 0.8f * density + 0.5f;
        mVerticalPadding = -dp05;
        mHorizontalPadding = -dp05;
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        mPaint.setColor(mFillColor);
//        canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mPaint);
        canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mStrokePaint);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData next = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position + 1);

        mRect.left = current.mContentLeft - mHorizontalPadding + (next.mContentLeft - current.mContentLeft) * mEndInterpolator.getInterpolation(positionOffset);
        mRect.top = current.mContentTop - mVerticalPadding;
        mRect.right = current.mContentRight + mHorizontalPadding + (next.mContentRight - current.mContentRight) * mStartInterpolator.getInterpolation(positionOffset);
        mRect.bottom = current.mContentBottom + mVerticalPadding;

//        if (!mRoundRadiusSet) {
//            mRoundRadius = mRect.height() / 2;
//        }
        mRoundRadius = ConvertUtils.dp2px(12);

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }

//    public Paint getPaint() {
//        return mPaint;
//    }

    public int getFillColor() {
        return mFillColor;
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    public float getRoundRadius() {
        return mRoundRadius;
    }

    public void setRoundRadius(float roundRadius) {
        mRoundRadius = roundRadius;
        mRoundRadiusSet = true;
    }

    public Interpolator getStartInterpolator() {
        return mStartInterpolator;
    }

    public void setStartInterpolator(Interpolator startInterpolator) {
        mStartInterpolator = startInterpolator;
        if (mStartInterpolator == null) {
            mStartInterpolator = new LinearInterpolator();
        }
    }

    public Interpolator getEndInterpolator() {
        return mEndInterpolator;
    }

    public void setEndInterpolator(Interpolator endInterpolator) {
        mEndInterpolator = endInterpolator;
        if (mEndInterpolator == null) {
            mEndInterpolator = new LinearInterpolator();
        }
    }
}
