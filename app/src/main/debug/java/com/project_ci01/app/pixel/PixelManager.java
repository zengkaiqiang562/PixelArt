package com.project_ci01.app.pixel;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;

import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.project_ci01.app.base.manage.LifecyclerManager;
import com.project_ci01.app.base.utils.FileUtils;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;
import com.project_ci01.app.base.utils.StringUtils;
import com.project_ci01.app.dao.FromType;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
//            loadLocalHomeImages();
//            loadLocalDailyImages();
            parseInfoBean();
        });
    }

    private void parseInfoBean() {
        try {
            long start = SystemClock.elapsedRealtime();
            InputStream inputStream = assetManager.open("index.json");
            Gson gson = new Gson();
            Map<String, List<String>> infoPathMap = gson.fromJson(new InputStreamReader(inputStream), new TypeToken<>(){});
            List<String> infoPaths = infoPathMap.get("info_path");
            if (infoPaths == null) return;
            InfoBean infoBean;
            for (String infoPath : infoPaths) {
                inputStream = assetManager.open(infoPath);
                infoBean = gson.fromJson(new InputStreamReader(inputStream), InfoBean.class);
                parseListBean(infoBean.category, infoBean.defaultListBean.folder, infoBean.defaultListBean.images);
                parseListBean(infoBean.category, infoBean.updateListBean.folder, infoBean.updateListBean.images);
            }
            LogUtils.e(TAG, "--> parseInfoBean()   duration=" + (SystemClock.elapsedRealtime() - start));
        } catch (Exception e) {
            Log.e(TAG, "--> realLoad()  Failed!!! e=" + e);
            e.printStackTrace();
        }
    }

    private void parseListBean(String category, String folder, List<ImageBean> imageBeans) {
        if (imageBeans.isEmpty()) return;
        for (ImageBean imageBean : imageBeans) {
            ImageEntityNew imageEntityNew = new ImageEntityNew();
            imageEntityNew.createTime = TimeUtils.string2Millis(imageBean.date, "yyyy-MM-dd HH:mm:ss");
            imageEntityNew.imageId = imageBean.image_id;
            imageEntityNew.fileName = imageBean.fileName;
            imageEntityNew.description = imageBean.description;
            imageEntityNew.permission = imageBean.permission;
            imageEntityNew.display = imageBean.display;
            imageEntityNew.category = category;
            imageEntityNew.fromType = FromType.LOCAL.typeName;
            imageEntityNew.filePath = folder + imageBean.fileName;
            imageEntityNew.storeDir = context.getCacheDir() + File.separator
                    + FromType.LOCAL.typeName + File.separator + category + File.separator + imageBean.image_id + File.separator
                    + StringUtils.trimSuffix(imageBean.fileName);
//            imageEntityNew.originImagePath = imageEntityNew.storeDir + File.separator + imageBean.fileName;
            imageEntityNew.colorImagePath = imageEntityNew.storeDir + File.separator + "color_image";
            imageEntityNew.pixelsObjPath = imageEntityNew.storeDir + File.separator + "pixel_list";
            imageEntityNew.colorTime = 0;
            imageEntityNew.completed = false;
            loadData(imageEntityNew);
        }
    }

    private void loadData(ImageEntityNew imageEntityNew) {
        sFixExecutor.execute(() -> {
            long startTs = SystemClock.elapsedRealtime();
            try {
                // originImage
                InputStream inputStream = assetManager.open(imageEntityNew.filePath);
//                File originImage = new File(entity.originImagePath);
//                if (!originImage.exists()) {
//                    FileIOUtils.writeFileFromIS(originImage, inputStream);
//                }

                PixelList pixelList;
                File filPixelsObj = new File(imageEntityNew.pixelsObjPath);
                if (!filPixelsObj.exists()) {
                    // pixelList
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inMutable = true;
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                    if (bitmap == null) {
                        LogUtils.e(TAG, "--> loadData() bitmap == null!!!  filePath=" + imageEntityNew.filePath);
                        return;
                    }
                    pixelList = PixelHelper.getAllPixels(bitmap, 5);
                    FileUtils.writeObjectByZipJson(pixelList, imageEntityNew.pixelsObjPath);
                } else {
                    pixelList = PixelHelper.getPixelList(imageEntityNew);
                }

                if (pixelList == null) {
                    LogUtils.e(TAG, "--> loadData() pixelList == null!!!  pixelsObjPath=" + imageEntityNew.pixelsObjPath);
                    return;
                }

                // colorImage
                File fileColorImage = new File(imageEntityNew.colorImagePath);
                if (!fileColorImage.exists()) { // 文件存在时不处理
                    PixelHelper.writeColorImage(imageEntityNew.colorImagePath, pixelList, false); // 文件存在时不处理
                }

                //save to database
                if (ImageDbManager.getInstance().countByStoreDirSync(imageEntityNew.storeDir) == 0) { // 不存在时才添加
                    ImageDbManager.getInstance().addImage(imageEntityNew);
                }

            } catch (IOException e) {
                Log.e(TAG, "--> loadData()  Failed!!! e=" + e);
                e.printStackTrace();
            }
            long duration = SystemClock.elapsedRealtime() - startTs;
            LogUtils.e(TAG, "--> loadData()  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
        });
    }

//    private void loadLocalHomeImages() {
//        try {
//            InputStream inputStream = assetManager.open("home.json");
//            Gson gson = new Gson();
//            LocalInfo localInfo = gson.fromJson(new InputStreamReader(inputStream), LocalInfo.class);
//
//            for (LocalCat cat : localInfo.cats) {
//                for (String fileName : cat.images) {
//
//                    String category = Category.convert(cat.category).catName;
//                    String fromType = FromType.LOCAL.typeName;
//                    String storeDir = context.getCacheDir() + File.separator
//                            + fromType + File.separator + category + File.separator
//                            + StringUtils.trimSuffix(fileName);
//                    File storeDirFile = new File(storeDir);
//
//                    if (!storeDirFile.exists()) {
//                        boolean result = storeDirFile.mkdirs();
//                        LogUtils.e(TAG, "--> loadLocalHomeImages() storeDir.mkdirs  result=" + result);
//                    }
//
//                    sFixExecutor.execute(() -> {
//                        ImageEntity entity = new ImageEntity(context, System.currentTimeMillis(), fileName, fromType, category);
//                        String assetFile = cat.folder + fileName;
//                        parse(entity, assetFile);
//                    });
//                }
//            }
//
//        } catch (IOException e) {
//            Log.e(TAG, "--> loadLocalHomeImages()  Failed!!! e=" + e);
//            e.printStackTrace();
//        }
//    }
//
//    private void loadLocalDailyImages() {
//        try {
//            InputStream inputStream = assetManager.open("daily.json");
//            Gson gson = new Gson();
//            DailyInfo dailyInfo = gson.fromJson(new InputStreamReader(inputStream), DailyInfo.class);
//
//            for (DailyMonth month : dailyInfo.months) {
//                for (DailyImage image : month.images) {
//
//                    String category = Category.DAILY.catName;
//                    String fromType = FromType.LOCAL.typeName;
//                    String fileName = image.path.substring(image.path.lastIndexOf("/") + 1);
//                    String storeDir = context.getCacheDir() + File.separator
//                            + fromType + File.separator + category + File.separator + month.month + File.separator
//                            + StringUtils.trimSuffix(fileName);
//                    File storeDirFile = new File(storeDir);
//
//                    if (!storeDirFile.exists()) {
//                        boolean result = storeDirFile.mkdirs();
//                        LogUtils.e(TAG, "--> loadLocalDailyImages() storeDir.mkdirs  result=" + result);
//                    }
//
//                    long createTime = TimeUtils.string2Millis(image.date, "yyyyMMdd");
//
//                    sFixExecutor.execute(() -> {
//                        ImageEntity entity = new ImageEntity(context, createTime, fileName, fromType, category, month.month);
//                        String assetFile = image.path;
//                        parse(entity, assetFile);
//                    });
//                }
//            }
//
//        } catch (IOException e) {
//            Log.e(TAG, "--> loadLocalDailyImages()  Failed!!! e=" + e);
//            e.printStackTrace();
//        }
//    }
//
//    private void parse(ImageEntity entity, String assetFile) {
//        long startTs = SystemClock.elapsedRealtime();
//        try {
//            // originImage
//            InputStream inputStream = assetManager.open(assetFile);
//            File originImage = new File(entity.originImagePath);
//            if (!originImage.exists()) {
//                FileIOUtils.writeFileFromIS(originImage, inputStream);
//            }
//
//            PixelList pixelList = null;
//            File filPixelsObj = new File(entity.pixelsObjPath);
//            if (!filPixelsObj.exists()) {
//                // pixelList
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inMutable = true;
//                Bitmap bitmap = BitmapFactory.decodeFile(entity.originImagePath, options);
//                if (bitmap == null) {
//                    LogUtils.e(TAG, "--> parse() bitmap == null!!!  assetFile=" + assetFile);
//                    return;
//                }
//                pixelList = PixelHelper.getAllPixels(bitmap, 5);
//                FileUtils.writeObjectByZipJson(pixelList, entity.pixelsObjPath);
//            }
//
//
//            // colorImage
//            File fileColorImage = new File(entity.colorImagePath);
//            if (!fileColorImage.exists()) { // 文件存在时不处理
//                if (pixelList == null) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inMutable = true;
//                    Bitmap bitmap = BitmapFactory.decodeFile(entity.originImagePath, options);
//                    if (bitmap == null) {
//                        LogUtils.e(TAG, "--> parse() bitmap == null!!!  assetFile=" + assetFile);
//                        return;
//                    }
//                    pixelList = PixelHelper.getAllPixels(bitmap, 5);
//                }
//                PixelHelper.writeColorImage(entity.colorImagePath, pixelList, false); // 文件存在时不处理
//            }
//
//            //save to database
//            if (ImageDbManager.getInstance().countByStoreDirSync(entity.storeDir) == 0) { // 不存在时才添加
//                ImageDbManager.getInstance().addImage(entity);
//            }
//
//        } catch (IOException e) {
//            Log.e(TAG, "--> parse()  Failed!!! e=" + e);
//            e.printStackTrace();
//        }
//        long duration = SystemClock.elapsedRealtime() - startTs;
//        LogUtils.e(TAG, "--> parse()  duration=" + MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss SSS"));
//    }


    /*======================== Local Image ==========================*/
    public static class InfoBean {
        public String category;
        @SerializedName("default")
        public ImageListBean defaultListBean;
        @SerializedName("update")
        public ImageListBean updateListBean;
    }

    public static class ImageListBean {
        public String folder;
        public List<ImageBean> images;
    }

    public static class ImageBean {
        public String date; // yyyy-MM-dd HH:mm:ss
        public int image_id;
        public String fileName;
        public String description;
        public List<String> permission;
        public List<String> display;
    }



//    /*======================== Home ==========================*/
//
//    public static class LocalInfo {
//        public List<LocalCat> cats;
//    }
//
//    public static class LocalCat {
//        public String folder;
//        public String category;
//        public List<String> images;
//
//        @Override
//        public String toString() {
//            return "LocalCat{" +
//                    "folder='" + folder + '\'' +
//                    ", category='" + category + '\'' +
//                    ", images=" + images +
//                    '}';
//        }
//    }
//
//    /*========================= Daily =========================*/
//
//    public static class DailyInfo {
//        public List<DailyMonth> months;
//    }
//
//    public static class DailyMonth {
//        public String month;
//        public List<DailyImage> images;
//    }
//
//    public static class DailyImage {
//        public String date;
//        public String path;
//    }
}

