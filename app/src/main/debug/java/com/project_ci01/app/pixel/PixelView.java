package com.project_ci01.app.pixel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntity;
import com.project_ci01.app.base.utils.BitmapUtils;
import com.project_ci01.app.base.utils.FileUtils;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;

import java.util.List;
import java.util.Map;

public class PixelView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "PixelView";

    private final Context context;
    private int width;
    private int height;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private float curFactor = 1.0f; // 默认不缩放

    private float transX = 0f; // 水平平移量
    private float transY = 0f; // 垂直平移量

    private boolean firstScroll; // Down事件后的第一次滚动
    private boolean isScaleEvent; // 是否为缩放事件

    private float lastPixelUnit;

    /*========== 画笔 ==========*/
    private Paint numberPaint; // 数字画笔
    private Paint borderPaint; // 像素边框画笔
    private Paint bgPaint; // 像素背景画笔
    private Paint colorPaint; // 填色画笔

    private Paint maskPaint; // 遮罩画笔
    /*========== 画笔 ==========*/

    private RectF colorRectF; // 填色像素单元
    private RectF numberRectF; // 数字像素单元
    private RectF maskRectF; // 遮罩像素单元

    private PixelList pixelList;
    private float drawLeft; // 绘图的左上角的 x
    private float drawTop; // 绘图的左上角的 y

    private Region drawRegion; // 绘图区域

    /*========================*/
    private int selColor; // 当前选中的颜色
    private boolean swipeColor; // 为 true 表示滑动上色；false 时才允许拖动图片

    private Props props = Props.NONE; // 默认不使用道具

    private ImageEntity entity;

    private StoreHandler storeHandler;
    private HandlerThread storeHandlerThread;

    public PixelView(Context context) {
        this(context, null);
    }

    public PixelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PixelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setOnDoubleTapListener(this);
        gestureDetector.setIsLongpressEnabled(false); // 禁止长按，因为长按事件触发后，onScroll 不会再回调
        scaleGestureDetector = new ScaleGestureDetector(context, this);

        // storeHandler
        storeHandlerThread = new HandlerThread("thread_store");
        storeHandlerThread.start();
        storeHandler = new StoreHandler(storeHandlerThread.getLooper());
    }

    public void release() {
        storeHandler.removeCallbacksAndMessages(null);
        storeHandlerThread.quitSafely();
        storeHandler = null;
        storeHandlerThread = null;
    }

    public void setImageEntity(@NonNull ImageEntity entity) {
        this.entity = entity;
        pixelList = PixelManager.getInstance().getPixelList(entity);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        LogUtils.e(TAG, "--> onMeasure()  measuredWidth=" + measuredWidth + ", measuredHeight=" + measuredHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        int left = getLeft();
        int top = getTop();
        int right = getRight();
        int bottom = getBottom();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        LogUtils.e(TAG, "--> onSizeChanged()  oldw=" + oldw + ", oldh=" + oldh + ", width=" + width + ", height=" + height
                + ", left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom
                + ", paddingLeft=" + paddingLeft + ", paddingTop=" + paddingTop + ", paddingRight=" + paddingRight + ", paddingBottom=" + paddingBottom
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        LogUtils.e(TAG, "--> onDraw()  pixelList=" + pixelList);

        if (pixelList == null) {
            return;
        }

        float pixelUnit = pixelList.curUnitSize * curFactor;
        if (lastPixelUnit > 0) {
            transX = transX * pixelUnit / lastPixelUnit; // 缩放调整
            transY = transY * pixelUnit / lastPixelUnit; // 缩放调整
        }

        drawLeft = (width - pixelList.originWidth * pixelUnit) / 2f + transX;
        drawTop = (height - pixelList.originHeight * pixelUnit) / 2f + transY;
        if (drawRegion == null) {
            drawRegion = new Region();
        }
        drawRegion.set((int) drawLeft, (int) drawTop, (int) (drawLeft + pixelList.originWidth * pixelUnit), (int) (drawTop + pixelList.originHeight * pixelUnit));


        for (Map.Entry<Integer, List<PixelUnit>> entry : pixelList.colorMap.entrySet()) {
            Integer color = entry.getKey();
            String number = pixelList.numberMap.get(color);
            if (TextUtils.isEmpty(number)) {
                continue;
            }
            for (PixelUnit pixel : entry.getValue()) {
                if (PixelHelper.ignorePixel(pixel)) {
                    continue;
                }
                drawColorBitmap(canvas, pixelUnit, drawLeft, drawTop, pixel);
//                drawMaskBitmap(canvas, pixelUnit, drawLeft, drawTop, pixel);
                drawNumberBitmap(canvas, pixelUnit, drawLeft, drawTop, number, pixel);
            }
        }

        lastPixelUnit = pixelUnit;

        // 更新存储
        if (storeHandler != null) {
            storeHandler.sendStoreMsg();
        }
    }

    private void drawColorBitmap(@NonNull Canvas canvas, float pixelUnit, float drawLeft, float drawTop, PixelUnit pixel) { // 绘制填色图
        if (colorPaint == null) {
            colorPaint = new Paint();
            colorPaint.setStyle(Paint.Style.FILL);
            colorPaint.setAntiAlias(true);
        }
        if (colorRectF == null) {
            colorRectF = new RectF();
        }

        if (!pixel.enableDraw) { // 不需要绘制的像素点不绘制
            return;
        }

        float left = drawLeft + pixel.x * pixelUnit;
        float right = left + pixelUnit;
        float top = drawTop + pixel.y * pixelUnit;
        float bottom = top + pixelUnit;
        colorRectF.set(left, top, right, bottom);
        colorPaint.setColor(pixel.color);
        canvas.drawRect(colorRectF, colorPaint);
    }

    private void drawMaskBitmap(@NonNull Canvas canvas, float pixelUnit, float drawLeft, float drawTop, PixelUnit pixel) { // 绘制遮罩
        if (maskPaint == null) {
            maskPaint = new Paint();
            maskPaint.setStyle(Paint.Style.FILL);
            maskPaint.setAntiAlias(true);
            maskPaint.setColor(Color.WHITE);
        }
        if (maskRectF == null) {
            maskRectF = new RectF();
        }

        if (pixel.enableDraw) { // 需要绘制的像素点不再遮罩
            return;
        }
        float left = drawLeft + pixel.x * pixelUnit;
        float right = left + pixelUnit;
        float top = drawTop + pixel.y * pixelUnit;
        float bottom = top + pixelUnit;
        maskRectF.set(left, top, right, bottom);
        canvas.drawRect(maskRectF, maskPaint);
    }

    private void drawNumberBitmap(@NonNull Canvas canvas, float pixelUnit, float drawLeft, float drawTop, String number, PixelUnit pixel) { // 绘制数字图
        if (borderPaint == null) {
            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setAntiAlias(true);
            borderPaint.setColor(Color.BLACK);
            borderPaint.setStrokeWidth(0.1f);
        }

        if (bgPaint == null) {
            bgPaint = new Paint();
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setAntiAlias(true);
            bgPaint.setColor(Color.LTGRAY);
        }

        if (numberPaint == null) {
            numberPaint = new Paint();
            numberPaint.setStyle(Paint.Style.FILL);
            numberPaint.setAntiAlias(true);
            numberPaint.setColor(Color.BLACK);
            numberPaint.setTextAlign(Paint.Align.CENTER);
        }

        if (numberRectF == null) {
            numberRectF = new RectF();
        }

        if (pixel.enableDraw) { // 需要绘制的像素点不再用数字遮盖
            return;
        }
        float left = drawLeft + pixel.x * pixelUnit;
        float right = left + pixelUnit;
        float top = drawTop + pixel.y * pixelUnit;
        float bottom = top + pixelUnit;
        numberRectF.set(left, top, right, bottom);

        if (selColor == pixel.color) { // 选中颜色的部分高亮
            bgPaint.setColor(Color.GRAY);
            bgPaint.setAlpha(255);
            numberPaint.setFakeBoldText(true);
        } else {
//                    bgPaint.setColor(Color.LTGRAY);
            bgPaint.setColor(BitmapUtils.convertGrey(pixel.color));
            bgPaint.setAlpha((int) (255 * 0.3f));
            numberPaint.setFakeBoldText(false);
        }

        canvas.drawRect(numberRectF, bgPaint); // 画数字像素单元的边框
        canvas.drawRect(numberRectF, borderPaint); // 画数字像素单元的背景
        numberPaint.setTextSize(pixelUnit * 0.5f);
        Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
        float fontHeight = fontMetrics.ascent - fontMetrics.descent;
        canvas.drawText(number, numberRectF.centerX(), numberRectF.centerY() - fontHeight / 2, numberPaint);  // 画数字像素单元的内容数字
    }

    /**
     * 根据坐标 x,y 查找像素点
     */
    @Nullable
    private PixelUnit findPixel(float x, float y) {
        if (pixelList == null || drawRegion == null) {
            return null;
        }
        boolean contains = drawRegion.contains((int) x, (int) y);
        LogUtils.e(TAG, "--> findPixel()  contains=" + contains);
        if (!contains) {
            return null;
        }
        // 在绘图区域内，再判断具体是哪个像素点
        float left = x - drawLeft; // 相对于绘图区域的left
        float top = y - drawTop; // 相对于绘图区域的top
        int column = (int) (left / lastPixelUnit); // 第几列
        int row = (int) (top / lastPixelUnit); // 第几行
        // TODO 注意当不同图片尺寸时，这里是否会出现 索引越界
        int pixelIndex = row + column * pixelList.originHeight; // pixelList 集合是按列遍历的， 一列有 pixelList.originHeight 个像素
        LogUtils.e(TAG, "--> findPixel()  column=" + column + "  row=" + row+ "  pixelIndex=" + pixelIndex + "  pixelList.pixels.size=" + pixelList.pixels.size());
        PixelUnit pixelUnit = pixelList.pixels.get(pixelIndex);
        String number = pixelList.numberMap.get(pixelUnit.color);
        LogUtils.e(TAG, "--> findPixel()  number=" + number + "  pixelUnit=" + pixelUnit);
        return pixelUnit;
    }

    /**
     * @return true 能绘制；false 其他没有绘制的情况（如没找到像素点，or 像素点已绘制，or 像素点颜色不是选中颜色）
     */
    private boolean drawPixel(float x, float y) {
        PixelUnit pixel = findPixel(x, y);
        if (pixel == null || PixelHelper.ignorePixel(pixel)) {
            return false;
        }
        if (!pixel.enableDraw && (selColor == pixel.color || props == Props.BRUSH)) { // 未绘制，且 （匹配选中颜色 or 正在使用笔刷）时才能进行绘制
            pixel.enableDraw = true;
            invalidate();
            return true;
        }
        return false;
    }

    /**
     * 通过道具绘制，
     * 返回true 已绘制，不再处理
     * 返回false 没有使用道具绘制，继续处理
     */
    private boolean propsDraw(float x, float y) {
        switch (props) {
            case NONE:
                return false;
            case BUCKET:
                bucketDraw(x, y);
                return true;
            case WAND:
                wandDraw(x, y);
                return true;
        }
        return false;
    }

    /**
     * 通过颜料桶绘制：将所点击像素点相邻的同一颜色像素点全部正确上色
     * @see Props#BUCKET
     */
    private void bucketDraw(float x, float y) {
        if (pixelList == null) {
            return;
        }
        PixelUnit pixel = findPixel(x, y);
        if (pixel == null || PixelHelper.ignorePixel(pixel)) {
            return;
        }
        List<List<PixelUnit>> adjoinOuters = pixelList.adjoinMap.get(pixel.color);
        if (adjoinOuters == null) {
            return;
        }
        boolean handle = false;
        for (List<PixelUnit> adjoinInners : adjoinOuters) {
            if (!adjoinInners.contains(pixel)) {
                continue;
            }
            for (PixelUnit adjoinPixel : adjoinInners) {
                if (!adjoinPixel.enableDraw) {
                    adjoinPixel.enableDraw = true;
                    handle = true;
                }
            }
        }
        if (handle) {
            invalidate();
            // TODO 消耗掉道具
            props = Props.NONE;
        }
    }

    /**
     * 通过魔棒绘制
     * @see Props#WAND
     */
    private void wandDraw(float x, float y) {
        if (pixelList == null) {
            return;
        }
        PixelUnit pixel = findPixel(x, y);
        if (pixel == null || PixelHelper.ignorePixel(pixel)) {
            return;
        }

        List<PixelUnit> pixels = pixelList.colorMap.get(pixel.color); // 同颜色的所有像素点
        if (pixels == null) {
            return;
        }

        boolean handle = false;
        for (PixelUnit drawPixel : pixels) {
            if (!drawPixel.enableDraw) {
                drawPixel.enableDraw = true;
                handle = true;
            }
        }

        if (handle) {
            invalidate();
            // TODO 消耗掉道具
            props = Props.NONE;
        }
    }

    /**
     * 通过选中颜色，查找同色的未绘制的像素点，并将该像素点移动到视图中间显示
     * 从上至下，从左至右 查找（即按像素点的遍历顺序查找）
     */
    public void centerUndrawPixel() {
        if (pixelList == null) {
            return;
        }

        if (selColor == Color.WHITE || selColor == Color.TRANSPARENT) { // 不处理白色和透明
            return;
        }

        List<PixelUnit> pixels = pixelList.colorMap.get(selColor);
        if (pixels == null) {
            return;
        }

        PixelUnit undrawPixel = null;
        for (PixelUnit pixel : pixels) {
            if (!pixel.enableDraw) {
                undrawPixel = pixel;
                break; // 找到第一个就结束循环
            }
        }
        if (undrawPixel == null) {
            return;
        }

        // 将 undrawPixel 移动到视图中间显示
//        pixelList.curUnitSize = (int) (pixelList.curUnitSize * curFactor); // 保存上次缩放时的状态
        pixelList.curUnitSize = 90; // 固定显示的大小
        curFactor = 1.0f;
        lastPixelUnit = pixelList.curUnitSize * curFactor;
        int left = undrawPixel.x * pixelList.curUnitSize; // undrawPixel 相对于图片左边的距离
        int top = undrawPixel.y * pixelList.curUnitSize; // undrawPixel 相对于图片顶部的距离
        int centerLeft = pixelList.originWidth * pixelList.curUnitSize / 2; // 图片中心点相对于图片左边的距离
        int centerTop = pixelList.originHeight * pixelList.curUnitSize / 2; // 图片中心点相对于图片顶部边的距离
        transX = centerLeft - left; // 固定平移的位置
        transY = centerTop - top; // 固定平移的位置
        invalidate();
    }



    /*===================== 对外提供的方法 =======================*/
    public void setProps(@NonNull Props props) {
        this.props = props;
    }


    /*==================== 触摸事件 & 手势 ========================*/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (pixelList == null) {
            return super.onTouchEvent(event);
        }

        int pointerCount = event.getPointerCount();

        /*
        public static final int ACTION_DOWN             = 0;
        public static final int ACTION_UP               = 1;
        public static final int ACTION_MOVE             = 2;
        public static final int ACTION_CANCEL           = 3;
        public static final int ACTION_OUTSIDE          = 4;
         */
//        LogUtils.e(TAG, "-->  onTouchEvent()  action=" + event.getAction() + "  pointerCount=" + pointerCount);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isScaleEvent = false; // reset
            swipeColor = false; // reset
        }

        if (pointerCount >= 2) {
            scaleGestureDetector.onTouchEvent(event);
        }
        if (scaleGestureDetector.isInProgress()) {
            isScaleEvent = true;
            return true;
        }

        if (pointerCount == 1 && !isScaleEvent && gestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    /*--------------------------- GestureDetector.OnGestureListener  start -----------------------------*/
    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnGestureListener onDown()  action=" + e.getAction());
        firstScroll = true;
        return true; // 消费掉
    }

    @Override
    public void onShowPress(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnGestureListener onShowPress()  action=" + e.getAction());
        swipeColor = true; // 有按压动作时，判定为滑动上色
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) { // onDown 消费掉才会回调
        float x = e.getX();
        float y = e.getY();
        if (propsDraw(x, y)) { // 道具绘制
            return true;
        }
        return drawPixel(x, y); // 绘制像素点时消费掉
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        LogUtils.e(TAG, "--> OnGestureListener onScroll()  e1.action=" + e1.getAction() + ", e2.action=" + e2.getAction()
                + ", distanceX=" + distanceX + ", distanceY=" + distanceY);
        if (firstScroll) { // 第一次滚动不处理
            firstScroll = false;
            return false;
        }

        if (swipeColor) { // 滑动上色
            float x = e2.getX();
            float y = e2.getY();
            drawPixel(x, y);
        } else { // 拖动图片
            transX += -distanceX;
            transY += -distanceY;
            invalidate();
        }

        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnGestureListener onLongPress()  action=" + e.getAction());
    }

    @Override
    public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        LogUtils.e(TAG, "--> OnGestureListener onFling()  e1.action=" + e1.getAction() + ", e2.action=" + e2.getAction()
                + ", velocityX=" + velocityX + ", velocityY=" + velocityY);
        return false;
    }
    /*--------------------------- GestureDetector.OnGestureListener  end -----------------------------*/


    /*--------------------------- GestureDetector.OnDoubleTapListener  end -----------------------------*/

    @Override
    public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnDoubleTapListener onSingleTapConfirmed()  action=" + e.getAction());
        return false;
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnDoubleTapListener onDoubleTap()  action=" + e.getAction());
        float x = e.getX();
        float y = e.getY();
        PixelUnit pixel = findPixel(x, y);
        if (pixel == null || PixelHelper.ignorePixel(pixel)) {
            return false;
        }
        boolean invalid = false;
        if (selColor != pixel.color) { // 选择当前绘制的颜色类别
            selColor = pixel.color;
            invalid = true;
        }
        if (!pixel.enableDraw) { // 双击事件也进行绘制
            pixel.enableDraw = true;
            invalid = true;
        }
        if (invalid) {
            invalidate();
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnDoubleTapListener onDoubleTapEvent()  action=" + e.getAction());
        return false;
    }
    /*--------------------------- GestureDetector.OnDoubleTapListener  end -----------------------------*/



    /*--------------------------- ScaleGestureDetector.OnScaleGestureListener  start -----------------------------*/

    @Override
    public boolean onScale(@NonNull ScaleGestureDetector detector) { // onScaleBegin 消费掉才会回调
//        float currentSpanX = detector.getCurrentSpanX();
//        float currentSpanY = detector.getCurrentSpanY();
        float currentSpan = detector.getCurrentSpan();
//        float previousSpanX = detector.getPreviousSpanX();
//        float previousSpanY = detector.getPreviousSpanY();
        float previousSpan = detector.getPreviousSpan();
//        float xFactor = currentSpanX / previousSpanX;
//        float yFactor = currentSpanY / previousSpanY;
        float factor = currentSpan / previousSpan;

////        float scaleFactor = detector.getScaleFactor();
////        LogUtils.e(TAG, "--> OnScaleGestureListener onScale()  currentSpanX=" + currentSpanX + ",  currentSpanY=" + currentSpanY+ ",  currentSpan=" + currentSpan
////                + ",  previousSpanX=" + previousSpanX + ",  previousSpanY=" + previousSpanY+ ",  previousSpan=" + previousSpan
////                + ",  xFactor=" + xFactor + ",  yFactor=" + yFactor + ",  factor=" + factor
////        );
//        LogUtils.e(TAG, "--> OnScaleGestureListener onScale()    factor=" + factor);
        curFactor = factor;

        invalidate();
        return false;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        LogUtils.e(TAG, "--> OnScaleGestureListener onScaleBegin()");
        if (pixelList == null) {
            return false;
        }
        pixelList.curUnitSize = (int) (pixelList.curUnitSize * curFactor); // 保存上次缩放时的状态
        return true;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        LogUtils.e(TAG, "--> OnScaleGestureListener onScaleEnd()");
    }
    /*--------------------------- ScaleGestureDetector.OnScaleGestureListener  end -----------------------------*/


    private class StoreHandler extends Handler {

        static final int MSG_STORE = 100;

        int[] lastCountResult;

        StoreHandler(Looper looper) {
            super(looper);
        }

        void sendStoreMsg() {
            if (hasMessages(MSG_STORE)) {
                removeMessages(MSG_STORE);
            }
            sendEmptyMessageDelayed(MSG_STORE, 200); // 200ms 没有绘制操作更新一次
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_STORE) {
                if (pixelList == null || entity == null) {
                    return;
                }
                long startTs = SystemClock.elapsedRealtime();


                int[] countResult = new int[2];
                PixelHelper.countDrawnPixels(pixelList, countResult);

                if (lastCountResult == null) { // 第一次 onDraw 时不更新
                    LogUtils.e(TAG, "--> MSG_STORE  First onDraw !!!");
                    lastCountResult = countResult;
                    return;
                }

                if (lastCountResult[0] == countResult[0] && lastCountResult[1] == countResult[1]) {
                    // 只是平移或缩放，没有像素点被填色，此时不更新
                    LogUtils.e(TAG, "--> MSG_STORE  NO Pixel has been Drawn !!!");
                    return;
                }

                // 更新数据库
                entity.completed = countResult[0] == countResult[1];
                if (countResult[1] > 0) { // 如果从来没绘制过像素点就不更新 colorTime
                    entity.colorTime = System.currentTimeMillis();
                }
                ImageDbManager.getInstance().updateImage(entity);

                // 更新 pixelList
                FileUtils.writeObject(pixelList, entity.pixelsObjPath, true); // 存在时删除重新创建

                // 更新 colorImage
                PixelManager.getInstance().writeColorImage(entity.colorImagePath, pixelList, true); // 文件存在时删除重新创建

                lastCountResult = countResult;

                long duration = SystemClock.elapsedRealtime() - startTs;
                LogUtils.e(TAG, "--> MSG_STORE  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
            }
        }
    }
}
