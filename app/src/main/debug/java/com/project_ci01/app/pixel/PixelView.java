package com.project_ci01.app.pixel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.base.utils.BitmapUtils;
import com.project_ci01.app.base.utils.FileUtils;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

    private RectF colorRectF; // 填色像素单元在整个View中的位置
    private Canvas unitColorCanvas; // 像素单元的颜色画布


    private RectF numberRectF; // 数字像素单元在整个View中的位置
    private Canvas unitNumberCanvas; // 像素单元的数字画布
    private RectF unitNumberRectF;
    private Paint unitNumberPaint; // 数字画笔
    private Paint unitBorderPaint; // 像素边框画笔
    private Paint unitBgPaint; // 像素背景画笔


    private float drawLeft; // 绘图的左上角的 x
    private float drawTop; // 绘图的左上角的 y

    private Region drawRegion; // 绘图区域

    /*========================*/
    private int selColor; // 当前选中的颜色
    private boolean swipeColor; // 为 true 表示滑动上色；false 时才允许拖动图片

    private Props props = Props.NONE; // 默认不使用道具

    private ImageEntityNew entity;
    private PixelList pixelList;
    private Map<Integer, List<PixelUnit>> colorMap;
    private Map<Integer, String> numberMap;
    private final Map<String, Bitmap[]> bitmapMap = new HashMap<>();
    private Map<Integer, List<List<PixelUnit>>> adjoinMap;

    private StoreHandler storeHandler;
    private HandlerThread storeHandlerThread;

    private OnPixelViewCallback callback;

    private int countByBrush; // 单次通过笔刷上色的像素点个数
    private int totalByBrush; // 单次可通过笔刷上色的像素点个数上线

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

    public void loadPixels(@NonNull ImageEntityNew entity) {
        this.entity = entity;
        storeHandler.sendLoadPixelsMsg();       
    }
    
    private void loadPixelsInternal() { // called in HandlerThread
        if (entity == null) {
            return;
        }
        long start = SystemClock.elapsedRealtime();

        PixelList tmpPixelList = PixelHelper.getPixelList(entity);
        if (tmpPixelList == null) {
            LogUtils.e(TAG, "--> loadPixelsInternal()   getPixelList  Failed!!!");
            return;
        }
        LogUtils.e(TAG, "--> loadPixelsInternal()   getPixelList  duration=" + (SystemClock.elapsedRealtime() - start));

        adjoinMap = PixelHelper.fetchAdjoinMapFromLocal(entity);
        if (adjoinMap != null) {
            List<PixelUnit> pixelsFromAdjoin = new ArrayList<>();
            for (Map.Entry<Integer, List<List<PixelUnit>>> entry : adjoinMap.entrySet()) {
                for (List<PixelUnit> pixels : entry.getValue()) {
                    pixelsFromAdjoin.addAll(pixels);
                }
            }
            Collections.sort(pixelsFromAdjoin, (o1, o2) -> {
                int value1 = o1.x * tmpPixelList.originHeight + o1.y;
                int value2 = o2.x * tmpPixelList.originHeight + o2.y;
                return value1 - value2;
            });
            if (pixelsFromAdjoin.size() == tmpPixelList.pixels.size()) {
                LogUtils.e(TAG, "--> loadPixelsInternal()   pixelsFromAdjoin.size() == pixelList.pixels.size()");
                tmpPixelList.pixels = pixelsFromAdjoin; // 使用 adjoinMap 中的像素集 进行操作
            } else {
                adjoinMap.clear();
                adjoinMap = null;
            }
        }

        if (adjoinMap == null) {
            adjoinMap = PixelHelper.getAdjoinMap(tmpPixelList);
            PixelHelper.storeAdjoinMap2Local(adjoinMap, entity);
        }
        LogUtils.e(TAG, "--> loadPixelsInternal()   getAdjoinMap  duration=" + (SystemClock.elapsedRealtime() - start));

        colorMap = PixelHelper.getColorMap(tmpPixelList);
        LogUtils.e(TAG, "--> loadPixelsInternal()   getColorMap  duration=" + (SystemClock.elapsedRealtime() - start));
        numberMap = PixelHelper.getNumberMap(tmpPixelList, colorMap);
        LogUtils.e(TAG, "--> loadPixelsInternal()   getNumberMap  duration=" + (SystemClock.elapsedRealtime() - start));
        int[] result = PixelHelper.countDrawnPixels(tmpPixelList);
        LogUtils.e(TAG, "--> loadPixelsInternal()   countDrawnPixels  duration=" + (SystemClock.elapsedRealtime() - start));
        totalByBrush = (int) (result[0] * 0.1); // 单次可通过笔刷上色的像素点个数上线 为总像素点个数的 10%
        post(this::notifyInited);
//            int colorCount = 0;
//            int allColorPixelCount = 0;
//            for (Map.Entry<Integer, List<PixelUnit>> entry : colorMap.entrySet()) {
//                colorCount++;
//                allColorPixelCount += entry.getValue().size();
//                int color = entry.getKey();
//                byte alpha = (byte) Color.alpha(color);
//                byte red = (byte) Color.red(color);
//                byte green = (byte) Color.green(color);
//                byte blue = (byte) Color.blue(color);
//                String hexColor = "#" + Hex.bytesToStringUppercase(new byte[]{alpha, red, green, blue});
//                LogUtils.e(TAG, "--> setImageEntityNew()   color=" + hexColor + ",  number=" + numberMap.get(color) + ",  count=" + entry.getValue().size());
//            }
//            LogUtils.e(TAG, "--> setImageEntityNew()   colorCount=" + colorCount); // 颜色种类个数
//            LogUtils.e(TAG, "--> setImageEntityNew()   allColorPixelCount=" + allColorPixelCount); // 除白色和透明色外的像素点个数
//            LogUtils.e(TAG, "--> setImageEntityNew()   pixels.size=" + pixelList.pixels.size()); // 所有像素点个数

        pixelList = tmpPixelList; // pixelList 必须最后赋值，保证子线程中的其他操作都处理完成，UI线程才能进行正常绘制
        postInvalidate();
        LogUtils.e(TAG, "--> loadPixelsInternal()   duration=" + (SystemClock.elapsedRealtime() - start));
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
        long start = SystemClock.elapsedRealtime();

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


        bitmapMap.clear(); // reset

        for (Map.Entry<Integer, List<PixelUnit>> entry : colorMap.entrySet()) {
            Integer color = entry.getKey();
            String number = numberMap.get(color);
            if (TextUtils.isEmpty(number)) {
                continue;
            }
            for (PixelUnit pixel : entry.getValue()) {
                if (PixelHelper.ignorePixel(pixel)) {
                    continue;
                }
                drawColorBitmap(canvas, pixelUnit, drawLeft, drawTop, number, pixel);
                drawNumberBitmap(canvas, pixelUnit, drawLeft, drawTop, number, pixel);
            }
        }

        lastPixelUnit = pixelUnit;

        // 更新存储
        if (storeHandler != null) {
            storeHandler.sendStoreMsg();
        }

        long duration = SystemClock.elapsedRealtime() - start;
        LogUtils.e(TAG, "--> onDraw()   duration=" + duration);
    }

    private void drawColorBitmap(@NonNull Canvas canvas, float pixelUnit, float drawLeft, float drawTop, String number, PixelUnit pixel) { // 绘制填色图

        if (!pixel.enableDraw) { // 不需要绘制的像素点不绘制
            return;
        }

        if (colorRectF == null) {
            colorRectF = new RectF();
        }

        float left = drawLeft + pixel.x * pixelUnit;
        float right = left + pixelUnit;
        float top = drawTop + pixel.y * pixelUnit;
        float bottom = top + pixelUnit;
        colorRectF.set(left, top, right, bottom);

        Bitmap[] bitmaps = bitmapMap.get(number);
        if (bitmaps == null) {
            bitmaps = new Bitmap[2];
            bitmapMap.put(number, bitmaps);
        }

        if (bitmaps[0] == null) {
            bitmaps[0] = getColorBitmap(pixelUnit, pixel);
        }

        canvas.drawBitmap(bitmaps[0], null, colorRectF, null);
    }

    private void drawNumberBitmap(@NonNull Canvas canvas, float pixelUnit, float drawLeft, float drawTop, String number, PixelUnit pixel) { // 绘制数字图


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


        Bitmap[] bitmaps = bitmapMap.get(number);
        if (bitmaps == null) {
            bitmaps = new Bitmap[2];
            bitmapMap.put(number, bitmaps);
        }

        if (bitmaps[1] == null) {
            bitmaps[1] = getNumberBitmap(pixelUnit, pixel, number);
        }

        canvas.drawBitmap(bitmaps[1], null, numberRectF, null);
    }

    private Bitmap getColorBitmap(float pixelUnit, PixelUnit pixel) {

        if (unitColorCanvas == null) {
            unitColorCanvas = new Canvas();
        }

        Bitmap colorBitmap = Bitmap.createBitmap((int) pixelUnit, (int) pixelUnit, Bitmap.Config.ARGB_8888);
        unitColorCanvas.setBitmap(colorBitmap);
        unitColorCanvas.drawColor(pixel.color);

        unitColorCanvas.setBitmap(null);

        return colorBitmap;
    }

    private Bitmap getNumberBitmap(float pixelUnit, PixelUnit pixel, String number) {

        if (unitBorderPaint == null) {
            unitBorderPaint = new Paint();
            unitBorderPaint.setStyle(Paint.Style.STROKE);
            unitBorderPaint.setAntiAlias(true);
            unitBorderPaint.setColor(Color.BLACK);
            unitBorderPaint.setStrokeWidth(0.1f);
        }

        if (unitBgPaint == null) {
            unitBgPaint = new Paint();
            unitBgPaint.setStyle(Paint.Style.FILL);
            unitBgPaint.setAntiAlias(true);
            unitBgPaint.setColor(Color.LTGRAY);
        }

        if (unitNumberPaint == null) {
            unitNumberPaint = new Paint();
            unitNumberPaint.setStyle(Paint.Style.FILL);
            unitNumberPaint.setAntiAlias(true);
            unitNumberPaint.setColor(Color.BLACK);
            unitNumberPaint.setTextAlign(Paint.Align.CENTER);
        }

        if (unitNumberRectF == null) {
            unitNumberRectF = new RectF();
        }

        if (unitNumberCanvas == null) {
            unitNumberCanvas = new Canvas();
        }

        Bitmap numberBitmap = Bitmap.createBitmap((int) pixelUnit, (int) pixelUnit, Bitmap.Config.ARGB_8888);
        unitNumberCanvas.setBitmap(numberBitmap);

        unitNumberRectF.set(0, 0, numberBitmap.getWidth(), numberBitmap.getHeight());

        if (selColor == pixel.color) { // 选中颜色的部分高亮
            unitBgPaint.setColor(Color.GRAY);
            unitBgPaint.setAlpha(255);
            unitNumberPaint.setFakeBoldText(true);
        } else {
            unitBgPaint.setColor(BitmapUtils.convertGrey(pixel.color));
            unitBgPaint.setAlpha((int) (255 * 0.3f));
            unitNumberPaint.setFakeBoldText(false);
        }

        unitNumberCanvas.drawRect(unitNumberRectF, unitBgPaint); // 画数字像素单元的边框
        unitNumberCanvas.drawRect(unitNumberRectF, unitBorderPaint); // 画数字像素单元的背景
        unitNumberPaint.setTextSize(pixelUnit * 0.5f);
        Paint.FontMetrics fontMetrics = unitNumberPaint.getFontMetrics();
        float fontHeight = fontMetrics.ascent - fontMetrics.descent;
        unitNumberCanvas.drawText(number, unitNumberRectF.centerX(), unitNumberRectF.centerY() - fontHeight / 2, unitNumberPaint);  // 画数字像素单元的内容数字

        unitNumberCanvas.setBitmap(null);

        return numberBitmap;
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
        String number = numberMap.get(pixelUnit.color);
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
            if (swipeColor && props == Props.BRUSH) {
                ++countByBrush; // 笔刷滑动上色时，累计数量
                if (countByBrush == totalByBrush) {
                    notifyPropsEnd(Props.BRUSH);
                    props = Props.NONE;
                    countByBrush = 0; // reset
                }
            }
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
        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(pixel.color);
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
            notifyPropsEnd(Props.BUCKET);
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

        List<PixelUnit> pixels = colorMap.get(pixel.color); // 同颜色的所有像素点
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
            notifyPropsEnd(Props.WAND);
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

        if (PixelHelper.ignoreColor(selColor)) { // 不处理白色和透明
            return;
        }

        List<PixelUnit> pixels = colorMap.get(selColor);
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

        boolean invalid = setSelColor(pixel.color);

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
        static final int MSG_LOAD_PIXELS = 101;

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
        
        void sendLoadPixelsMsg() {
            if (hasMessages(MSG_LOAD_PIXELS)) {
                removeMessages(MSG_LOAD_PIXELS);
            }
            sendEmptyMessage(MSG_LOAD_PIXELS);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_LOAD_PIXELS) {
                loadPixelsInternal();
                return;
            }
            if (msg.what == MSG_STORE) {
                if (pixelList == null || entity == null) {
                    return;
                }
                long startTs = SystemClock.elapsedRealtime();


                Map<Integer, int[]> mapResult = new HashMap<>();
                int[] countResult = PixelHelper.countDrawnPixels(pixelList, mapResult);

//                LogUtils.e(TAG, "--> MSG_STORE  countResult=" + Arrays.toString(countResult));
//                for (Map.Entry<Integer, int[]> entry : mapResult.entrySet()) {
//                    LogUtils.e(TAG, "--> MSG_STORE  color=" + entry.getKey() + ",  colorResult=" + Arrays.toString(entry.getValue()));
//                }
                // 计算状态，处理回调
                List<Integer> colors = new ArrayList<>();
                for (Map.Entry<Integer, int[]> entry : mapResult.entrySet()) {
                    int color = entry.getKey();
                    int[] colorResult = entry.getValue();
                    if (colorResult[0] == colorResult[1]) { // 总个数 == 已填色个数时表示填色完毕
                        colors.add(color);
                    }
                }
                if (!colors.isEmpty()) {
                    PixelView.this.post(() -> {
                        notifyColorCompleted(colors);
                    });
                }
                if (countResult[0] == countResult[1]) {
                    PixelView.this.post(PixelView.this::notifyAllCompleted);
                }


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
                FileUtils.writeObjectByZipJson(pixelList, entity.pixelsObjPath); // 存在时删除重新创建

                // 更新 colorImage
                PixelHelper.writeColorImage(entity.colorImagePath, pixelList, true); // 文件存在时删除重新创建
                
                // 更新 adjoinMap
                PixelHelper.storeAdjoinMap2Local(adjoinMap, entity);

                lastCountResult = countResult;

                long duration = SystemClock.elapsedRealtime() - startTs;
                LogUtils.e(TAG, "--> MSG_STORE  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
            }
        }
    }


    /*=============================== public method ============================*/

    public void setProps(@NonNull Props props) {
        if (this.props == props) { // 相同不处理
            return;
        }
        this.props = props;
    }

    public Map<Integer, List<PixelUnit>> getColorMap() {
        return colorMap;
    }

    public Map<Integer, String> getNumberMap() {
        return numberMap;
    }

    public PixelList getPixelList() {
        return pixelList;
    }

    /**
     * @return 是否绘制
     */
    public boolean setSelColor(int color) {
        if (selColor != color) {
            selColor = color;
            notifySelColorChanged(selColor);
            return true;
        }
        return false;
    }

    /*=============================== Callback ============================*/

    public void setOnPixelViewCallback(OnPixelViewCallback callback) {
        this.callback = callback;
    }

    private void notifyInited() {
        if (callback != null) {
            callback.onInited();
        }
    }

    private void notifySelColorChanged(int selColor) {
        if (callback != null) {
            callback.onSelColorChanged(selColor);
        }
    }

    private void notifyColorCompleted(@NonNull List<Integer> colors) {
        if (callback != null) {
            callback.onColorCompleted(colors);
        }
    }

    private void notifyAllCompleted() {
        if (callback != null) {
            callback.onAllCompleted();
        }
    }

    private void notifyPropsEnd(Props props) {
        if (callback != null) {
            callback.onPropsEnd(props);
        }
    }

    public interface OnPixelViewCallback {
        void onInited(); // 初始化完成
        void onSelColorChanged(int selColor); // 选中颜色改变
        void onColorCompleted(@NonNull List<Integer> colors); // 填色完毕的颜色集
        void onAllCompleted(); // 全部填色完毕
        void onPropsEnd(Props props); // 某种道具使用结束
    }
}
