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
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.util.Hex;
import com.project_m1142.app.base.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PixelView extends View implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "PixelView";

    private final Context context;
    private Bitmap srcBitmap;
    private int width;
    private int height;
    private Matrix matrix;
    private Canvas convertCanvas;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    /*========== 画笔 ==========*/
    private Paint numberPaint; // 数字画笔
    private Paint borderPaint; // 像素边框画笔
    private Paint bgPaint; // 像素背景画笔
    private Paint colorPaint; // 填色画笔
    /*========== 画笔 ==========*/

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
            srcBitmap = BitmapFactory.decodeStream(inputStream, null, options);
            matrix = new Matrix();

            //TODO test
            srcBitmap = convertBitmap(srcBitmap);
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
        LogUtils.e(TAG, "--> onDraw()  srcBitmap=" + srcBitmap);

        if (srcBitmap == null) {
            return;
        }

        LogUtils.e(TAG, "--> onDraw()  srcBitmap.isMutable=" + srcBitmap.isMutable());

        int srcBitmapWidth = srcBitmap.getWidth();
        int srcBitmapHeight = srcBitmap.getHeight();
        float drawLeft = (width - srcBitmapWidth) / 2f;
        float drawTop = (height - srcBitmapHeight) / 2f;

        canvas.drawBitmap(srcBitmap, drawLeft, drawTop, null);

        /*=================================*/
//        matrix.reset();
//        float transCenterX = (width - srcBitmapWidth*5f) / 2f;
//        float transCenterY = (height - srcBitmapHeight*5f) / 2f;
//        matrix.postScale(5f, 5f);
//        matrix.postTranslate(transCenterX, transCenterY);
//        // createBitmap(@NonNull Bitmap source, int x, int y, int width, int height, @Nullable Matrix m, boolean filter)
////        Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmapWidth, srcBitmapHeight, matrix, false);
//        canvas.drawBitmap(srcBitmap, matrix, null); // 不会对 srcBitmap 产生作用
        /*=================================*/



        /*=================================*/
//        canvas.drawBitmap(srcBitmap, drawLeft, drawTop, null);
        /*=================================*/
    }

    private Bitmap convertBitmap(@NonNull Bitmap bitmap) {
        if (convertCanvas == null) {
            convertCanvas = new Canvas();
        }

        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(0.1f);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(Color.LTGRAY);

        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.ascent - fontMetrics.descent;

        List<Pixel> allPixels = PixelHelper.getAllPixels(bitmap, 90);
        int pixelUnit = allPixels.get(0).unit;
        Bitmap convertBitmap = Bitmap.createBitmap(bitmap.getWidth() * pixelUnit, bitmap.getHeight() * pixelUnit, Bitmap.Config.ARGB_8888);
        convertCanvas.setBitmap(convertBitmap);
        Rect rect = new Rect();
        for (int index = 0; index < allPixels.size(); index++) {
            Pixel pixel = allPixels.get(index);
            LogUtils.e(TAG, "--> convertBitmap()  pixel.color=" + pixel.color);
            if (pixel.color == Color.WHITE || pixel.color == Color.TRANSPARENT) { // 不处理白色和透明
                continue;
            }
            int left = pixel.x * pixel.unit;
            int right = pixel.x * pixel.unit + pixel.unit;
            int top = pixel.y * pixel.unit;
            int bottom = pixel.y * pixel.unit + pixel.unit;
            rect.set(left, top, right, bottom);
//            paint.setColor(pixel.color);
            convertCanvas.drawRect(rect, paint);
            convertCanvas.drawRect(rect, strokePaint);

            textPaint.setTextSize(pixel.unit * 0.5f);
            convertCanvas.drawText("1", rect.centerX(), rect.centerY() - fontHeight / 2, textPaint);
        }
        convertCanvas.setBitmap(null);
        return convertBitmap;
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


        scaleGestureDetector.onTouchEvent(event);
        if (scaleGestureDetector.isInProgress()) {
            return true;
        }

        if (pointerCount == 1 && gestureDetector.onTouchEvent(event)) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    /*--------------------------- GestureDetector.OnGestureListener  start -----------------------------*/
    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        LogUtils.e(TAG, "--> OnGestureListener onDown()  action=" + e.getAction());
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
        return false;
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
        float scaleFactor = currentSpan / previousSpan;
//        float scaleFactor = detector.getScaleFactor();
        LogUtils.e(TAG, "--> OnScaleGestureListener onScale()  currentSpanX=" + currentSpanX + ",  currentSpanY=" + currentSpanY+ ",  currentSpan=" + currentSpan
                + ",  previousSpanX=" + previousSpanX + ",  previousSpanY=" + previousSpanY+ ",  previousSpan=" + previousSpan
                + ",  xFactor=" + xFactor + ",  yFactor=" + yFactor + ",  scaleFactor=" + scaleFactor
        );
        return false;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        LogUtils.e(TAG, "--> OnScaleGestureListener onScaleBegin()");
        return true;
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        LogUtils.e(TAG, "--> OnScaleGestureListener onScaleEnd()");
    }
    /*--------------------------- ScaleGestureDetector.OnScaleGestureListener  end -----------------------------*/
}
