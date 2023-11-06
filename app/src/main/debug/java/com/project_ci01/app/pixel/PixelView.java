package com.project_ci01.app.pixel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.base.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class PixelView extends View implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "PixelView";

    private final Context context;
    private int width;
    private int height;
    private Matrix matrix;

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
    /*========== 画笔 ==========*/


    /*========== 画布 和 要画的图 和 像素单元 ==========*/
//    private Canvas colorCanvas; // 填色画布
//    private Bitmap colorBitmap; // 填色图
    private RectF colorRectF; // 填色像素单元
//    private Canvas numberCanvas; // 数字画布
//    private Bitmap numberBitmap; // 数字图
    private RectF numberRectF; // 数字像素单元
    /*========== 画布 ==========*/

    private PixelList pixelList;

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
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        AssetManager assetManager = context.getAssets();
        String assetFilePath = "images/cartoon/01.png";
        try {
            InputStream inputStream = assetManager.open(assetFilePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            LogUtils.e(TAG, "--> init()  bitmap=" + bitmap);
            if (bitmap != null) {
            LogUtils.e(TAG, "--> init()  bitmap.isMutable=" + bitmap.isMutable());
                pixelList = PixelHelper.getAllPixels(bitmap, 30); // 每个像素点扩大30倍（即原图扩大30倍）
            }

            matrix = new Matrix();

        } catch (IOException e) {
            Log.e(TAG, "--> init()  assetManager.open Failed !!!   assetFilePath=" + assetFilePath);
        }
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
        LogUtils.e(TAG, "--> onDraw()  pixelList=" + pixelList);

        if (pixelList == null) {
            return;
        }

        float pixelUnit = pixelList.curUnitSize * curFactor;
        if (lastPixelUnit > 0) {
            transX = transX * pixelUnit / lastPixelUnit; // 缩放调整
            transY = transY * pixelUnit / lastPixelUnit; // 缩放调整
        }
        drawColorBitmap(canvas, pixelUnit);
        drawNumberBitmap(canvas, pixelUnit);
        lastPixelUnit = pixelUnit;

//        int numberBitmapWidth = numberBitmap.getWidth();
//        int numberBitmapHeight = numberBitmap.getHeight();
//        float drawLeft = (width - numberBitmapWidth) / 2f;
//        float drawTop = (height - numberBitmapHeight) / 2f;
//        LogUtils.e(TAG, "--> onDraw()  width=" + width);
//        LogUtils.e(TAG, "--> onDraw()  height=" + height);
//        LogUtils.e(TAG, "--> onDraw()  numberBitmapWidth=" + numberBitmapWidth);
//        LogUtils.e(TAG, "--> onDraw()  numberBitmapHeight=" + numberBitmapHeight);
//        LogUtils.e(TAG, "--> onDraw()  drawLeft=" + drawLeft);
//        LogUtils.e(TAG, "--> onDraw()  drawTop=" + drawTop);
//        Rect srcRect = new Rect();
//        srcRect.set(0, 0, width, height);
//        RectF dstRect = new RectF();
//        dstRect.set(0, 0, width, height);
//        canvas.drawBitmap(numberBitmap, srcRect, dstRect, null);


//        LogUtils.e(TAG, "--> onDraw()  srcBitmap.isMutable=" + srcBitmap.isMutable());
//
//        int srcBitmapWidth = srcBitmap.getWidth();
//        int srcBitmapHeight = srcBitmap.getHeight();
//        float drawLeft = (width - srcBitmapWidth) / 2f;
//        float drawTop = (height - srcBitmapHeight) / 2f;
//
//        canvas.drawBitmap(srcBitmap, drawLeft, drawTop, null);

//        /*=================================*/
//        matrix.reset();
//        float transCenterX = (width - numberBitmap.getWidth()*scaleFactor) / 2f;
//        float transCenterY = (height - numberBitmap.getHeight()*scaleFactor) / 2f;
//        matrix.postScale(scaleFactor, scaleFactor);
//        matrix.postTranslate(transCenterX, transCenterY);
//        // createBitmap(@NonNull Bitmap source, int x, int y, int width, int height, @Nullable Matrix m, boolean filter)
////        Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmapWidth, srcBitmapHeight, matrix, false);
//        canvas.drawBitmap(numberBitmap, matrix, null); // 不会对 srcBitmap 产生作用
//        /*=================================*/



        /*=================================*/
//        canvas.drawBitmap(srcBitmap, drawLeft, drawTop, null);
        /*=================================*/
    }


    private void drawColorBitmap(@NonNull Canvas canvas, float pixelUnit) { // 绘制填色图
        if (colorPaint == null) {
            colorPaint = new Paint();
            colorPaint.setStyle(Paint.Style.FILL);
            colorPaint.setAntiAlias(true);
        }
        if (colorRectF == null) {
            colorRectF = new RectF();
        }

        float drawLeft = (width - pixelList.originWidth * pixelUnit) / 2f + transX; // 绘图的左上角的 x
        float drawTop = (height - pixelList.originHeight * pixelUnit) / 2f + transY; // 绘图的左上角的 y

        for (Map.Entry<Integer, List<PixelUnit>> entry : pixelList.colorMap.entrySet()) {
            Integer color = entry.getKey();
            String number = pixelList.numberMap.get(color);
            if (TextUtils.isEmpty(number)) {
                continue;
            }
            for (PixelUnit pixel : entry.getValue()) {
                if (pixel.color == Color.WHITE || pixel.color == Color.TRANSPARENT) { // 不处理白色和透明
                    continue;
                }
                float left = drawLeft + pixel.x * pixelUnit;
                float right = left + pixelUnit;
                float top = drawTop + pixel.y * pixelUnit;
                float bottom = top + pixelUnit;
                colorRectF.set(left, top, right, bottom);
                colorPaint.setColor(color);
                canvas.drawRect(colorRectF, colorPaint);
            }
        }
    }

    private void drawNumberBitmap(@NonNull Canvas canvas, float pixelUnit) { // 绘制数字图
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
            numberPaint.setColor(Color.WHITE);
            numberPaint.setTextAlign(Paint.Align.CENTER);
        }

        if (numberRectF == null) {
            numberRectF = new RectF();
        }

        float drawLeft = (width - pixelList.originWidth * pixelUnit) / 2f + transX; // 绘图的左上角的 x
        float drawTop = (height - pixelList.originHeight * pixelUnit) / 2f + transY; // 绘图的左上角的 y

        for (Map.Entry<Integer, List<PixelUnit>> entry : pixelList.colorMap.entrySet()) {
            Integer color = entry.getKey();
            String number = pixelList.numberMap.get(color);
            if (TextUtils.isEmpty(number)) {
                continue;
            }
            for (PixelUnit pixel : entry.getValue()) {
                if (pixel.color == Color.WHITE || pixel.color == Color.TRANSPARENT) { // 不处理白色和透明
                    continue;
                }
                float left = drawLeft + pixel.x * pixelUnit;
                float right = left + pixelUnit;
                float top = drawTop + pixel.y * pixelUnit;
                float bottom = top + pixelUnit;
                numberRectF.set(left, top, right, bottom);

                canvas.drawRect(numberRectF, bgPaint); // 画数字像素单元的边框
                canvas.drawRect(numberRectF, borderPaint); // 画数字像素单元的背景
                numberPaint.setTextSize(pixelUnit * 0.5f);
                Paint.FontMetrics fontMetrics = numberPaint.getFontMetrics();
                float fontHeight = fontMetrics.ascent - fontMetrics.descent;
                canvas.drawText(number, numberRectF.centerX(), numberRectF.centerY() - fontHeight / 2, numberPaint);  // 画数字像素单元的内容数字
            }
        }
    }

    /*==================== 触摸事件 & 手势 ========================*/
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
        }

        scaleGestureDetector.onTouchEvent(event);
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
    }

    @Override
    public boolean onSingleTapUp(@NonNull MotionEvent e) { // onDown 消费掉才会回调
        LogUtils.e(TAG, "--> OnGestureListener onSingleTapUp()  action=" + e.getAction());
        return false;
    }

    @Override
    public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        LogUtils.e(TAG, "--> OnGestureListener onScroll()  e1.action=" + e1.getAction() + ", e2.action=" + e2.getAction()
                + ", distanceX=" + distanceX + ", distanceY=" + distanceY);
        if (firstScroll) { // 第一次滚动不处理
            firstScroll = false;
            return false;
        }
        transX += -distanceX;
        transY += -distanceY;
        invalidate();
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

    /*--------------------------- ScaleGestureDetector.OnScaleGestureListener  start -----------------------------*/

    @Override
    public boolean onScale(@NonNull ScaleGestureDetector detector) { // onScaleBegin 消费掉才会回调
        float currentSpanX = detector.getCurrentSpanX();
        float currentSpanY = detector.getCurrentSpanY();
        float currentSpan = detector.getCurrentSpan();
        float previousSpanX = detector.getPreviousSpanX();
        float previousSpanY = detector.getPreviousSpanY();
        float previousSpan = detector.getPreviousSpan();
        float xFactor = currentSpanX / previousSpanX;
        float yFactor = currentSpanY / previousSpanY;
        float factor = currentSpan / previousSpan;

//        float scaleFactor = detector.getScaleFactor();
//        LogUtils.e(TAG, "--> OnScaleGestureListener onScale()  currentSpanX=" + currentSpanX + ",  currentSpanY=" + currentSpanY+ ",  currentSpan=" + currentSpan
//                + ",  previousSpanX=" + previousSpanX + ",  previousSpanY=" + previousSpanY+ ",  previousSpan=" + previousSpan
//                + ",  xFactor=" + xFactor + ",  yFactor=" + yFactor + ",  factor=" + factor
//        );
        LogUtils.e(TAG, "--> OnScaleGestureListener onScale()    factor=" + factor);
        curFactor = factor;

        invalidate();
        return false;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        LogUtils.e(TAG, "--> OnScaleGestureListener onScaleBegin()");
        pixelList.curUnitSize = (int) (pixelList.curUnitSize * curFactor); // 保存上次缩放时的状态
        return true;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        LogUtils.e(TAG, "--> OnScaleGestureListener onScaleEnd()");
    }
    /*--------------------------- ScaleGestureDetector.OnScaleGestureListener  end -----------------------------*/
}
