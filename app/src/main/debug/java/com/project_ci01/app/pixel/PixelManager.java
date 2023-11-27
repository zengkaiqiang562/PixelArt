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
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.FromType;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            Map<String, List<ImageEntityNew>> defaultListMap = new HashMap<>();
            Map<String, List<ImageEntityNew>> updateListMap = new HashMap<>();
            List<ImageEntityNew> entities;
            for (String infoPath : infoPaths) {
                inputStream = assetManager.open(infoPath);
                infoBean = gson.fromJson(new InputStreamReader(inputStream), InfoBean.class);
                entities = parseListBean(infoBean.category, infoBean.defaultListBean.folder, infoBean.defaultListBean.images);
                if (!entities.isEmpty()) {
                    defaultListMap.put(infoBean.category, entities);
                }
                entities = parseListBean(infoBean.category, infoBean.updateListBean.folder, infoBean.updateListBean.images);
                if (!entities.isEmpty()) {
                    updateListMap.put(infoBean.category, entities);
                }
            }

            // update 列表的更新时间填充，一天更新两张不同种类的图片
            List<ImageEntityNew> updateList = handUpdateList(updateListMap);

            // 区分曝光位置图片和 Daily 图片，优先处理
            List<ImageEntityNew> defaultRecommandList = new ArrayList<>();
            List<ImageEntityNew> defaultUnrecommandList = new ArrayList<>();
            List<ImageEntityNew> defaultDailyList = new ArrayList<>();
            for (Map.Entry<String, List<ImageEntityNew>> entry : defaultListMap.entrySet()) {
                if (Category.DAILY.catName.equals(entry.getKey())) {
                    defaultDailyList.addAll(entry.getValue());
                    continue;
                }
                for (ImageEntityNew imageEntityNew : entry.getValue()) {
                    if (imageEntityNew.display == null || imageEntityNew.display.isEmpty()) {
                        defaultUnrecommandList.add(imageEntityNew);
                    } else {
                        defaultRecommandList.add(imageEntityNew);
                    }
                }
            }

            for (ImageEntityNew imageEntityNew : defaultRecommandList) {
                loadData(imageEntityNew);
            }

            for (ImageEntityNew imageEntityNew : defaultDailyList) {
                loadData(imageEntityNew);
            }

            for (ImageEntityNew imageEntityNew : defaultUnrecommandList) {
                loadData(imageEntityNew);
            }

            for (ImageEntityNew imageEntityNew : updateList) {
                loadData(imageEntityNew);
            }


            LogUtils.e(TAG, "--> parseInfoBean()   duration=" + (SystemClock.elapsedRealtime() - start));
        } catch (Exception e) {
            Log.e(TAG, "--> realLoad()  Failed!!! e=" + e);
            e.printStackTrace();
        }
    }

    private List<ImageEntityNew> parseListBean(String category, String folder, List<ImageBean> imageBeans) {
        List<ImageEntityNew> entities = new ArrayList<>();
        if (imageBeans.isEmpty()) return entities;
        for (ImageBean imageBean : imageBeans) {
            ImageEntityNew imageEntityNew = new ImageEntityNew();
            if (imageBean.date != null && !imageBean.date.isEmpty()) {
                imageEntityNew.createTime = TimeUtils.string2Millis(imageBean.date, "yyyy-MM-dd HH:mm:ss");
            }
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
            entities.add(imageEntityNew);
        }
        return entities;
    }

    private List<ImageEntityNew> handUpdateList(Map<String, List<ImageEntityNew>> updateListMap) {
        // update 列表的更新时间填充，一天更新两张不同种类的图片
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, 11 - 1); // 11 月
        calendar.set(Calendar.DATE, 30); // 从 11.30 号开始添加更新图片
        List<ImageEntityNew> updateEntities = new ArrayList<>();

        int index = 0;
        int count = 0;
        for(;;) {
            int lastSize = updateEntities.size();

            for (Map.Entry<String, List<ImageEntityNew>> entry : updateListMap.entrySet()) {
                if (index < entry.getValue().size()) {
                    ImageEntityNew imageEntityNew = entry.getValue().get(index);
                    imageEntityNew.createTime = calendar.getTimeInMillis();
                    updateEntities.add(imageEntityNew);
                    ++count;
                }
                if (count == 2) { // 每天更新2个
                    calendar.add(Calendar.DATE, 1);
                    count = 0;
                }
            }
            int size = updateEntities.size();
            if (lastSize == size) { // 没有了
                break;
            }
            ++index;
        }

//            LogUtils.e(TAG, "--> parseInfoBean()   updateEntities.size=" + updateEntities.size());
//            for (ImageEntityNew imageEntityNew : updateEntities) {
//                LogUtils.e(TAG, "--> parseInfoBean()   updateEntities  imageEntityNew=" + imageEntityNew);
//            }
        return updateEntities;
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
                    pixelList = PixelHelper.getAllPixels(bitmap/*, 5*/);
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

