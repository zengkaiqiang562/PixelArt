package com.project_ci01.app.pixel;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project_ci01.app.base.utils.BitmapUtils;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.FromType;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntity;
import com.project_ci01.app.base.manage.LifecyclerManager;
import com.project_ci01.app.base.utils.FileUtils;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;
import com.project_ci01.app.base.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public void loadLocalImages() {
        sFixExecutor.submit(() -> {
//            long startTs = SystemClock.elapsedRealtime();
//
//            long duration = SystemClock.elapsedRealtime() - startTs;
//            LogUtils.e(TAG, "--> loadLocalImages()  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
            loadLocalHomeImages();
            loadLocalDailyImages();
        });
    }

    private void loadLocalHomeImages() {
        try {
            InputStream inputStream = assetManager.open("home.json");
            Gson gson = new Gson();
            LocalInfo localInfo = gson.fromJson(new InputStreamReader(inputStream), LocalInfo.class);

            for (LocalCat cat : localInfo.cats) {
                for (String fileName : cat.images) {

                    String category = Category.convert(cat.category).catName;
                    String fromType = FromType.LOCAL.typeName;
                    String storeDir = context.getCacheDir() + File.separator
                            + fromType + File.separator + category + File.separator
                            + StringUtils.trimSuffix(fileName);
                    File storeDirFile = new File(storeDir);

                    if (!storeDirFile.exists()) {
                        boolean result = storeDirFile.mkdirs();
                        LogUtils.e(TAG, "--> loadLocalHomeImages() storeDir.mkdirs  result=" + result);
                    }

                    sFixExecutor.execute(() -> {
                        ImageEntity entity = new ImageEntity(context, System.currentTimeMillis(), fileName, fromType, category);
                        String assetFile = cat.folder + fileName;
                        parse(entity, assetFile);
                    });
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "--> loadLocalHomeImages()  Failed!!! e=" + e);
            e.printStackTrace();
        }
    }

    private void loadLocalDailyImages() {
        try {
            InputStream inputStream = assetManager.open("daily.json");
            Gson gson = new Gson();
            DailyInfo dailyInfo = gson.fromJson(new InputStreamReader(inputStream), DailyInfo.class);

            for (DailyMonth month : dailyInfo.months) {
                for (DailyImage image : month.images) {

                    String category = Category.DAILY.catName;
                    String fromType = FromType.LOCAL.typeName;
                    String fileName = image.path.substring(image.path.lastIndexOf("/") + 1);
                    String storeDir = context.getCacheDir() + File.separator
                            + fromType + File.separator + category + File.separator + month.month + File.separator
                            + StringUtils.trimSuffix(fileName);
                    File storeDirFile = new File(storeDir);

                    if (!storeDirFile.exists()) {
                        boolean result = storeDirFile.mkdirs();
                        LogUtils.e(TAG, "--> loadLocalDailyImages() storeDir.mkdirs  result=" + result);
                    }

                    long createTime = TimeUtils.string2Millis(image.date, "yyyyMMdd");

                    sFixExecutor.execute(() -> {
                        ImageEntity entity = new ImageEntity(context, createTime, fileName, fromType, category, month.month);
                        String assetFile = image.path;
                        parse(entity, assetFile);
                    });
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "--> loadLocalDailyImages()  Failed!!! e=" + e);
            e.printStackTrace();
        }
    }

    private void parse(ImageEntity entity, String assetFile) {
        long startTs = SystemClock.elapsedRealtime();
        try {
            // originImage
            InputStream inputStream = assetManager.open(assetFile);
            File originImage = new File(entity.originImagePath);
            if (!originImage.exists()) {
                FileIOUtils.writeFileFromIS(originImage, inputStream);
            }

            PixelList pixelList = null;
            File filPixelsObj = new File(entity.pixelsObjPath);
            if (!filPixelsObj.exists()) {
                // pixelList
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bitmap = BitmapFactory.decodeFile(entity.originImagePath, options);
                if (bitmap == null) {
                    LogUtils.e(TAG, "--> parse() bitmap == null!!!  assetFile=" + assetFile);
                    return;
                }
                pixelList = PixelHelper.getAllPixels(bitmap, 5);
                FileUtils.writeObjectByZipJson(pixelList, entity.pixelsObjPath);
            }


            // colorImage
            File fileColorImage = new File(entity.colorImagePath);
            if (!fileColorImage.exists()) { // 文件存在时不处理
                if (pixelList == null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(entity.originImagePath, options);
                    if (bitmap == null) {
                        LogUtils.e(TAG, "--> parse() bitmap == null!!!  assetFile=" + assetFile);
                        return;
                    }
                    pixelList = PixelHelper.getAllPixels(bitmap, 5);
                }
                PixelHelper.writeColorImage(entity.colorImagePath, pixelList, false); // 文件存在时不处理
            }

            //save to database
            if (ImageDbManager.getInstance().countByStoreDirSync(entity.storeDir) == 0) { // 不存在时才添加
                ImageDbManager.getInstance().addImage(entity);
            }

        } catch (IOException e) {
            Log.e(TAG, "--> parse()  Failed!!! e=" + e);
            e.printStackTrace();
        }
        long duration = SystemClock.elapsedRealtime() - startTs;
        LogUtils.e(TAG, "--> parse()  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
    }


    /*======================== Home ==========================*/

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

    /*========================= Daily =========================*/

    public static class DailyInfo {
        public List<DailyMonth> months;
    }

    public static class DailyMonth {
        public String month;
        public List<DailyImage> images;
    }

    public static class DailyImage {
        public String date;
        public String path;
    }
}

