package com.project_ci01.app.pixel;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.FromType;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.base.manage.LifecyclerManager;
import com.project_m1142.app.base.utils.BitmapUtils;
import com.project_m1142.app.base.utils.FileUtils;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.utils.MyTimeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class PixelManager {
    private static final String TAG = "PixelManager";
    private volatile static PixelManager instance;

    private final ExecutorService sFixExecutor = Executors.newFixedThreadPool(3);

    private final Context context;
    private final AssetManager assetManager;

    public static PixelManager getInstance() {
        if(instance == null) {
            synchronized(PixelManager.class) {
                if(instance == null) {
                    instance = new PixelManager();
                }
            }
        }
        return instance;
    }

    private PixelManager() {
        context = LifecyclerManager.INSTANCE.getApplication().getApplicationContext();
        assetManager = context.getResources().getAssets();
    }

    @Nullable
    public Bitmap getColorBitmap(@NonNull ImageEntity entity) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(entity.colorImagePath,  options);
        Log.e(TAG, "--> getColorBitmap()  bitmap=" + bitmap);
        return bitmap;
    }

    @Nullable
    public PixelList getPixelList(@NonNull ImageEntity entity) {
        Object obj = FileUtils.readObject(entity.pixelsObjPath);
        if (obj instanceof PixelList) {
            return (PixelList) obj;
        }
        return null;
    }


    public String storeDir(String fromType, String category, String fileName) {
        String noSuffixFileName = Pattern.compile("\\.[a-zA-Z0-9_]+$").matcher(fileName).replaceAll("");
        return context.getCacheDir() + File.separator + fromType + File.separator + category + File.separator + noSuffixFileName;
    }

    public void loadLocalImages() {
        sFixExecutor.submit(() -> {
            long startTs = SystemClock.elapsedRealtime();
            try {
                InputStream inputStream = assetManager.open("info.json");
                Gson gson = new Gson();
                LocalInfo localInfo = gson.fromJson(new InputStreamReader(inputStream), LocalInfo.class);

                for (LocalCat cat : localInfo.cats) {
                    for (String fileName : cat.images) {

                        String category = Category.convert(cat.category).catName;
                        String fromType = FromType.LOCAL.typeName;
                        File storeDir = new File(storeDir(fromType, category, fileName));

                        if (!storeDir.exists()) {
                            boolean result = storeDir.mkdirs();
                            LogUtils.e(TAG, "--> loadLocalImages() storeDir.mkdirs  result=" + result);
                        }

                        sFixExecutor.execute(() -> {
                            parseLocal(cat.folder, fileName, fromType, category);
                        });
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, "--> loadLocalImages()  Failed!!! e=" + e);
                e.printStackTrace();
            }
            long duration = SystemClock.elapsedRealtime() - startTs;
            LogUtils.e(TAG, "--> loadLocalImages()  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
        });
    }

    private void parseLocal(String folder, String fileName, String fromType, String category) {
        long startTs = SystemClock.elapsedRealtime();

        ImageEntity entity = new ImageEntity(System.currentTimeMillis(), fileName, fromType, category);

        try {
            // originImage
            String assetFile = folder + fileName;
            InputStream inputStream = assetManager.open(assetFile);
            File originImage = new File(entity.originImagePath);
            if (!originImage.exists()) {
                FileIOUtils.writeFileFromIS(originImage, inputStream);
            }

            // pixelList
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(entity.originImagePath, options);
            if (bitmap == null) {
                LogUtils.e(TAG, "--> loadLocalImages() bitmap == null!!!  assetFile=" + assetFile);
                return;
            }
            PixelList pixelList = getAllPixels(bitmap, 5);
            FileUtils.writeObject(pixelList, entity.pixelsObjPath, false); // 存在时不处理

            // colorImage
            writeColorImage(entity.colorImagePath, pixelList, false); // 文件存在时不处理

            //save to database
            ImageDbManager.getInstance().updateImage(entity);

        } catch (IOException e) {
            Log.e(TAG, "--> parseLocal()  Failed!!! e=" + e);
            e.printStackTrace();
        }
        long duration = SystemClock.elapsedRealtime() - startTs;
        LogUtils.e(TAG, "--> parseLocal()  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
    }

    public void writeColorImage(String colorImagePath, @NonNull PixelList pixelList, boolean delIfExist) {
        File file = new File(colorImagePath);
        if (file.exists() && !delIfExist) { // 文件存在时不处理
            return;
        }
        Canvas canvas = new Canvas();
        Rect rect = new Rect();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        Bitmap bitmap = Bitmap.createBitmap(pixelList.stdWidth(), pixelList.stdHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        int pixelUnit = pixelList.stdUnitSize;
        for (Map.Entry<Integer, List<PixelUnit>> entry : pixelList.colorMap.entrySet()) {
            for (PixelUnit pixel : entry.getValue()) {
                if (PixelHelper.ignorePixel(pixel)) {
                    continue;
                }
                int left = pixel.x * pixelUnit;
                int right = pixel.x * pixelUnit + pixelUnit;
                int top = pixel.y * pixelUnit;
                int bottom = pixel.y * pixelUnit + pixelUnit;
                rect.set(left, top, right, bottom);
                if (pixel.enableDraw) {
                    paint.setColor(pixel.color);
                } else {
                    float[] hsv = new float[3];
                    Color.colorToHSV(pixel.color, hsv);
                    hsv[1] = 0f;
                    paint.setColor(Color.HSVToColor(hsv));
//                    paint.setAlpha((int) (255 * 0.7f));
                }
                canvas.drawRect(rect, paint);
            }
        }
        FileUtils.saveBitmap(bitmap, colorImagePath);
        bitmap.recycle();
        canvas.setBitmap(null);
    }

    /*==================================================*/

    public static PixelList getAllPixels(@NonNull Bitmap bitmap, int unit) { // bitmap 像素点太多时，会内存溢出，所以 bitmap 一定是很小很小的原图
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        /*
        int column = (int) (left / lastPixelUnit); // 第几列
        int row = (int) (top / lastPixelUnit); // 第几行
        int pixelIndex = row + column * pixelList.originHeight; // pixelList 集合是按列遍历的， 一列有 pixelList.originHeight 个像素
        当前索引 index，与之相邻的索引有：
            index = x * bitmapHeight + y
            x = index / bitmapHeight 取整
            y = index % bitmapHeight 取余

            x-1, y // 左    -> indexL = (x-1) * bitmapHeight + y
            x+1, y // 右    -> indexR = (x+1) * bitmapHeight + y
            x, y-1 // 上    -> indexT = x * bitmapHeight + (y-1)
            x, y+1 // 下    -> indexB = x * bitmapHeight + (y+1)
            x-1,y-1 // 左上 -> indexLT = (x-1) * bitmapHeight + (y-1)
            x+1,y-1 // 右上 -> indexRT = (x+1) * bitmapHeight + (y-1)
            x-1,y+1 // 左下 -> indexLB = (x-1) * bitmapHeight + (y+1)
            x+1,y+1 // 右下 -> indexRB = (x+1) * bitmapHeight + (y+1)

            向下垂直，再向右水平遍历，只需考虑已遍历过的点，即：左，上，左上，左下
         */

        Map<Integer, List<List<PixelUnit>>> adjoinMap = new HashMap<>(); // key 为 color，value 为一组组的相邻同色集


        List<PixelUnit> pixels = new ArrayList<>();
        Map<Integer, List<PixelUnit>> colorMap = new HashMap<>();
        PixelUnit pixel;
        for (int x = 0; x < bitmapWidth; x++) { // 按列遍历（先垂直遍历[内]，再水平遍历[外]）
            for (int y = 0; y < bitmapHeight; y++) {

                int color = bitmap.getPixel(x, y);
                pixel = new PixelUnit(x, y, color, false);

                // 添加到总集合
                pixels.add(pixel);


                // 添加到按颜色分类的集合
                if (color != Color.WHITE && color != Color.TRANSPARENT) { // 不处理白色和透明
                    List<PixelUnit> colorPixels = colorMap.get(color);
                    if (colorPixels == null) {
                        colorPixels = new ArrayList<>();
                        colorMap.put(color, colorPixels);
                    }
                    colorPixels.add(pixel);
                }


                // 相邻同色像素点放一起
                if (color != Color.WHITE && color != Color.TRANSPARENT) { // 不处理白色和透明
                    boolean adjoinL = false; // 是否左邻同色
                    if ((x - 1) >= 0) { // 存在 左 邻像素点
                        int indexL = (x-1) * bitmapHeight + y;
                        PixelUnit pixelL = pixels.get(indexL);
                        if (color == pixelL.color) { // 左邻同色
                            adjoinL = true;
                            List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                            if (adjoinOuters != null) {
                                for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                    if (adjoinInners.contains(pixelL) && !adjoinInners.contains(pixel)) {
                                        adjoinInners.add(pixel); // 存入相邻集
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    boolean adjoinLT = false; // 是否 左上 邻同色
                    if ((x - 1) >= 0 && (y - 1) >= 0) { // 存在 左上 邻像素点
                        int indexLT = (x-1) * bitmapHeight + (y-1);
                        PixelUnit pixelLT = pixels.get(indexLT);
                        if (color == pixelLT.color) { // 左上 邻同色
                            adjoinLT = true;
                            List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                            if (adjoinOuters != null) {
                                for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                    if (adjoinInners.contains(pixelLT) && !adjoinInners.contains(pixel)) {
                                        adjoinInners.add(pixel); // 存入相邻集
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    boolean adjoinT = false; // 是否 上 邻同色
                    if ((y - 1) >= 0) { // 存在 上 邻像素点
                        int indexT = x * bitmapHeight + (y-1);
                        PixelUnit pixelT = pixels.get(indexT);
                        if (color == pixelT.color) { // 上 邻同色
                            adjoinT = true;
                            List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                            if (adjoinOuters != null) {
                                for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                    if (adjoinInners.contains(pixelT) && !adjoinInners.contains(pixel)) {
                                        adjoinInners.add(pixel); // 存入相邻集
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    boolean adjoinLB = false; // 是否 左下 邻同色
                    if ((x - 1) >= 0 && (y + 1) < bitmapHeight) { // 存在 左下 邻像素点
                        int indexLB = (x-1) * bitmapHeight + (y+1);
                        PixelUnit pixelLB = pixels.get(indexLB);
                        if (color == pixelLB.color) { // 上 邻同色
                            adjoinLB = true;
                            List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                            if (adjoinOuters != null) {
                                for (List<PixelUnit> adjoinInners : adjoinOuters) {
                                    if (adjoinInners.contains(pixelLB) && !adjoinInners.contains(pixel)) {
                                        adjoinInners.add(pixel); // 存入相邻集
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // 1. 如果左下邻和左上邻都同色，可能存在 左下 和 左上 不在同一个相邻组的情况，此时需要合并相邻组
                    // 2. 如果左下邻和上邻都同色，可能存在 左下 和 上 不在同一个相邻组的情况，此时需要合并相邻组
                    if (adjoinLB && (adjoinLT || adjoinT)) {
                        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color);
                        if (adjoinOuters != null) {
                            List<PixelUnit> newAdjoinInners = new ArrayList<>();
                            for (int i = adjoinOuters.size() - 1; i >= 0; i--) {
                                List<PixelUnit> adjoinInners = adjoinOuters.get(i);
                                if (adjoinInners.contains(pixel)) {
                                    newAdjoinInners.addAll(adjoinInners);
                                    newAdjoinInners.remove(pixel); // 先移除，最后添加，不然可能 newAdjoinInners 有多个 pixel
                                    adjoinOuters.remove(i);
                                }
                            }
                            newAdjoinInners.add(pixel);
                            adjoinOuters.add(newAdjoinInners);
                        }
                    }

                    if (!adjoinL && !adjoinLT && !adjoinT && !adjoinLB) { // 不存在相邻，则创建一个新的相邻组
                        List<List<PixelUnit>> adjoinOuters = adjoinMap.get(color); // 同颜色的所有相邻组的集合
                        if (adjoinOuters == null) {
                            adjoinOuters = new ArrayList<>();
                            adjoinMap.put(color, adjoinOuters);
                        }
                        List<PixelUnit> adjoinInners = new ArrayList<>(); // 新的相邻组
                        adjoinInners.add(pixel);
                        adjoinOuters.add(adjoinInners);
                    }
                }
            }
        }

        Map<Integer, String> numberMap = new HashMap<>(); // 颜色-Number 键值对
        List<Map.Entry<Integer, List<PixelUnit>>> colorEntries = new ArrayList<>(colorMap.entrySet());
        Collections.sort(colorEntries, (o1, o2) -> { // 同颜色像素点多的排在前面
            int size1 = o1.getValue().size();
            int size2 = o2.getValue().size();
            return size2 - size1;
        });

        int number = 1;
        for (int index = 0; index < colorEntries.size(); index++) {
            Integer color = colorEntries.get(index).getKey();
            if (color == Color.WHITE || color == Color.TRANSPARENT) { // 不处理白色和透明
                continue;
            }
            numberMap.put(color, String.valueOf(number));
            number++;
        }

        LogUtils.e(TAG, "--> getAllPixels()  numberMap.size=" + numberMap.size());

        return new PixelList(adjoinMap, colorMap, numberMap, pixels, unit, unit, bitmapWidth, bitmapHeight);
    }

    /*==================================================*/

    public static class LocalInfo {
        public List<LocalCat> cats;
    }

    public static class LocalCat {
        public String folder;
        public String category;
        public List<String> images;

        @Override
        public String toString() {
            return "LocalCat{" +
                    "folder='" + folder + '\'' +
                    ", category='" + category + '\'' +
                    ", images=" + images +
                    '}';
        }
    }
}

