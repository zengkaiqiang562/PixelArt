package com.youth.banner.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;

import com.youth.banner.R;


/**
 * Drawable指示器
 */
public class DrawableIndicator extends BaseIndicator {
//    private Bitmap normalBitmap;
//    private Bitmap selectedBitmap;

    private Drawable normalDrawable;
    private Drawable selectedDrawable;

    /**
     * 实例化Drawable指示器 ，也可以通过自定义属性设置
     * @param context
     * @param normalResId
     * @param selectedResId
     */
    public DrawableIndicator(Context context, @DrawableRes int normalResId, @DrawableRes int selectedResId) {
        super(context);
//        normalBitmap = BitmapFactory.decodeResource(getResources(), normalResId);
//        selectedBitmap = BitmapFactory.decodeResource(getResources(), selectedResId);

        normalDrawable = getResources().getDrawable(normalResId, null);
        selectedDrawable = getResources().getDrawable(selectedResId, null);
    }

    public DrawableIndicator(Context context) {
        this(context, null);
    }

    public DrawableIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DrawableIndicator);
        if (a != null) {
//            BitmapDrawable normal = (BitmapDrawable) a.getDrawable(R.styleable.DrawableIndicator_normal_drawable);
//            BitmapDrawable selected = (BitmapDrawable) a.getDrawable(R.styleable.DrawableIndicator_selected_drawable);
//            normalBitmap = normal.getBitmap();
//            selectedBitmap = selected.getBitmap();

            normalDrawable = a.getDrawable(R.styleable.DrawableIndicator_normal_drawable);
            selectedDrawable = a.getDrawable(R.styleable.DrawableIndicator_selected_drawable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = config.getIndicatorSize();
        if (count <= 1 || normalDrawable == null || selectedDrawable == null) {
            return;
        }
//        setMeasuredDimension(selectedBitmap.getWidth() * (count - 1) + selectedBitmap.getWidth() + config.getIndicatorSpace() * (count - 1),
//                Math.max(normalBitmap.getHeight(), selectedBitmap.getHeight()));

        setMeasuredDimension(selectedDrawable.getIntrinsicWidth() * (count - 1) + selectedDrawable.getIntrinsicWidth() + config.getIndicatorSpace() * (count - 1),
                Math.max(normalDrawable.getIntrinsicHeight(), selectedDrawable.getIntrinsicHeight()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = config.getIndicatorSize();
        if (count <= 1 || normalDrawable == null || selectedDrawable == null) {
            return;
        }

        float left = 0;
        for (int i = 0; i < count; i++) {
//            canvas.drawBitmap(config.getCurrentPosition() == i ? selectedBitmap : normalBitmap, left, 0, mPaint);
//            left += normalBitmap.getWidth() + config.getIndicatorSpace();

            if (config.getCurrentPosition() == i) {
                selectedDrawable.setBounds((int) left, 0, (int) (left + selectedDrawable.getIntrinsicWidth()), selectedDrawable.getIntrinsicHeight());
                selectedDrawable.draw(canvas);
            } else {
                normalDrawable.setBounds((int) left, 0, (int) (left + normalDrawable.getIntrinsicWidth()), normalDrawable.getIntrinsicHeight());
                normalDrawable.draw(canvas);
            }
            left += normalDrawable.getIntrinsicWidth() + config.getIndicatorSpace();
        }
    }


}
