package com.project_ci01.app.refresh;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.project_ci01.app.R;
import com.scwang.smart.refresh.layout.api.RefreshFooter;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.simple.SimpleComponent;

public class MyRefreshFooter extends SimpleComponent implements RefreshFooter {

    private static final String TAG = "MyRefreshFooter";

    public MyRefreshFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_refresh_footer_my, this);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle = SpinnerStyle.Translate;
    }

    /**
     * 【仅限框架内调用】尺寸定义完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
     * @param kernel RefreshKernel
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        super.onInitialized(kernel, height, maxDragHeight);
    }

    /**
     * 【仅限框架内调用】手指拖动下拉（会连续多次调用，添加isDragging并取代之前的onPulling、onReleasing）
     * @param isDragging true 手指正在拖动 false 回弹动画
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+maxDragHeight)
     * @param height 高度 HeaderHeight or FooterHeight (offset 可以超过 height 此时 percent 大于 1)
     * @param maxDragHeight 最大拖动高度 offset 可以超过 height 参数 但是不会超过 maxDragHeight
     */
    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
////        LogUtil.e("zkq", TAG + " -----> onMoving   isDragging : " + isDragging
////                + "  percent : " + percent
////                + "  offset : " + offset
////                + "  height : " + height
////                + "  maxDragHeight : " + maxDragHeight);
//
//        if (lottieAnimationView.isAnimating()) {
//            lottieAnimationView.cancelAnimation();
//        }
//        if (percent > 1) {
//            percent = 1;
//        }
//        lottieAnimationView.setProgress(percent * sCountOfScaleFrame / sDefaultMaxFrame);
    }

    /**
     * 【仅限框架内调用】释放时刻（调用一次，将会触发加载）
     * @param refreshLayout RefreshLayout
     * @param height 高度 HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
    }

    /**
     * 【仅限框架内调用】开始动画
     * @param refreshLayout RefreshLayout
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        if (lottieAnimationView.isAnimating()) {
//            lottieAnimationView.cancelAnimation();
//        }
//
//        //帧索引从0开始
//        lottieAnimationView.setMinFrame(sCountOfScaleFrame - 1);
//        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
//        lottieAnimationView.playAnimation();
    }

    /**
     * 【仅限框架内调用】动画结束
     * @param refreshLayout RefreshLayout
     * @param success 数据是否成功刷新或加载
     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
//        int curFrame = lottieAnimationView.getFrame();
//        float maxFrame = lottieAnimationView.getMaxFrame();
//        LogUtil.e("zkq", TAG + " -----> onFinish   curFrame : " + curFrame
//                + "  maxFrame : " + maxFrame);
//
//        lottieAnimationView.setRepeatCount(0);

        //该Lottie动画的FPS 为 32，即 播放速率为 32帧/秒，以下计算剩余动画时间，从而延时结束加载动画
//        return (int) (Math.abs(maxFrame - curFrame) * (1.0f / sFPS * 1000));
        return 0;
    }
}
