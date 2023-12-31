package com.project_ci01.app.dao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.common.CompleteCallback;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ImageDbManager {
    private static final String TAG = "ImageDbManager";
    private volatile static ImageDbManager instance;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final ImageDbHandler dbHandler;

    private final ImageDao imageDao;

    private final List<OnImageDbChangedListener> onImageDbChangedListeners = new ArrayList<>();

    public static ImageDbManager getInstance() {
        if(instance == null) {
            synchronized(ImageDbManager.class) {
                if(instance == null) {
                    instance = new ImageDbManager();
                }
            }
        }
        return instance;
    }

    private ImageDbManager() {
        /*
         * 历史记录 db 操作的 Handler 线程
         */
        HandlerThread imageDbThread = new HandlerThread("thread_image_db");
        imageDbThread.start();
        dbHandler = new ImageDbHandler(imageDbThread.getLooper());
        ImageDB imageDB = ImageDB.getInstance();
        imageDao = imageDB.imageDao();
    }

    public void addImage(ImageEntityNew entity) {
        dbHandler.post(() -> {
            long result = imageDao.addImage(entity);
            LogUtils.e(TAG, "-->  addImage()  result=" + result);

            mainHandler.post(() -> {
                notifyOnImageAdded(entity.category, entity.imageId);
            });

            if (Category.DAILY.catName.equals(entity.category) && MyTimeUtils.isInToday(entity.createTime)) {
                mainHandler.post(this::notifyOnDailyChanged);
            }
        });
    }

    public void addImageSync(ImageEntityNew entity) {
        long result = imageDao.addImage(entity);
        LogUtils.e(TAG, "-->  addImage()  result=" + result);

        mainHandler.post(() -> {
            notifyOnImageAdded(entity.category, entity.imageId);
        });

        if (Category.DAILY.catName.equals(entity.category) && MyTimeUtils.isInToday(entity.createTime)) {
            mainHandler.post(this::notifyOnDailyChanged);
        }
    }

    public void updateColorTime(ImageEntityNew entity) { // 更新进入填色页的时间
        dbHandler.post(() -> {
            int updateCount = imageDao.updateColorTime(entity.imageId, entity.colorTime);
            LogUtils.e(TAG, "-->  updateColorTime()  entity.imageId=" + entity.imageId + "  updateCount=" + updateCount);
            mainHandler.post(() -> {
                notifyOnImageUpdated(entity.category, entity.imageId);
            });
        });
    }

    public void updateProgress(ImageEntityNew entity) { // 更新填色进度
        dbHandler.post(() -> {
            int updateCount = imageDao.updateProgress(entity.imageId, entity.colorTime, entity.completed, entity.colorCount, entity.totalCount);
            LogUtils.e(TAG, "-->  updateProgress()  entity.imageId=" + entity.imageId + "  updateCount=" + updateCount);
            mainHandler.post(() -> {
                notifyOnImageUpdated(entity.category, entity.imageId);
            });
            if (Category.DAILY.catName.equals(entity.category) && MyTimeUtils.isInToday(entity.createTime)) {
                mainHandler.post(this::notifyOnDailyChanged);
            }
        });
    }

    public void updateProgressSync(ImageEntityNew entity) { // 更新填色进度
        int updateCount = imageDao.updateProgress(entity.imageId, entity.colorTime, entity.completed, entity.colorCount, entity.totalCount);
        LogUtils.e(TAG, "-->  updateProgressSync()   entity.imageId="  + entity.imageId +  "   updateCount=" + updateCount);
        mainHandler.post(() -> {
            notifyOnImageUpdated(entity.category, entity.imageId);
        });
        if (Category.DAILY.catName.equals(entity.category) && MyTimeUtils.isInToday(entity.createTime)) {
            mainHandler.post(this::notifyOnDailyChanged);
        }
    }

    public void updateImageFromNet(ImageEntityNew entity) { // 更新来自网络的图片
        dbHandler.post(() -> {
            int count = imageDao.countByImageId(entity.imageId);
            LogUtils.e(TAG, "-->  updateImageFromNet()    entity.imageId=" + entity.imageId + "  count=" + count);
            if (imageDao.countByImageId(entity.imageId) <= 0) { // 不存在，则添加
                addImageSync(entity);
            } else { // 存在时更新（主要是可能会更新 display）
                // int imageId, String fileName, String description, List<String> permission, List<String> display
                int updateCount = imageDao.updateImage(entity.imageId, entity.fileName, entity.description, entity.permission, entity.display);
                LogUtils.e(TAG, "-->  updateImageFromNet()  entity.imageId=" + entity.imageId + "  updateCount=" + updateCount);

                mainHandler.post(() -> {
                    notifyOnImageUpdated(entity.category, entity.imageId);
                });
            }
        });
    }

    public void updateSaveImagePath(ImageEntityNew entity) { // 更新本地保存图片路径
        dbHandler.post(() -> {
            int updateCount = imageDao.updateSaveImagePath(entity.imageId, entity.saveImagePath);
            LogUtils.e(TAG, "-->  updateSaveImagePath()  entity.imageId=" + entity.imageId + "  updateCount=" + updateCount);
            mainHandler.post(() -> {
                notifyOnImageUpdated(entity.category, entity.imageId);
            });
        });
    }

    /**
     * storeDir 相同的 ImageEntityNew 视为同一个
     */
    private List<ImageEntityNew> queryByStoreDir(String storeDir) {
        List<ImageEntityNew> entities = imageDao.queryByStoreDir(storeDir);
        LogUtils.e(TAG, "-->  queryByStoreDir()   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public void queryByImageId(int imageId, QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryByImageId(imageId);
            LogUtils.e(TAG, "-->  queryByImageId()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryAll(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryAll();
            LogUtils.e(TAG, "-->  queryAll()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryAllCategories(CompleteCallback<List<String>> callback) {
        dbHandler.post(() -> {
            List<String> categories = imageDao.queryAllCategories();
            LogUtils.e(TAG, "-->  queryAllCategories()  categories=" + categories);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onCompleted(categories);
                }
            });
        });
    }

    public void queryHomeCategories(CompleteCallback<List<String>> callback) {
        dbHandler.post(() -> {
            List<String> categories = imageDao.queryAllCategories();
            LogUtils.e(TAG, "-->  queryHomeCategories()  categories=" + categories);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onCompleted(categories);
                }
            });
        });
    }

    public void queryAllInHome(int pageNum, int itemCount, QueryPageCallback<List<ImageEntityNew>> callback) {
        dbHandler.post(() -> {
            int count = imageDao.countAllInHome(MyTimeUtils.getEndOfToday());
            List<ImageEntityNew> todayEntities = queryAllTodayInHome();
            List<ImageEntityNew> recommendEntities = queryAllRecommendInHome(MyTimeUtils.getEndOfYesterday());
            List<ImageEntityNew> unrecommendEntities = queryAllUnrecommendInHome(MyTimeUtils.getEndOfYesterday());

            Collections.sort(recommendEntities, (e1, e2) -> { // 曝光位置排序
                String allPrefix = "All_";
                int index1 = 0;
                int index2 = 0;
                List<String> d1 = e1.display;
                List<String> d2 = e2.display;
                for (String str : d1) {
                    if (str.startsWith(allPrefix)) {
                        index1 = Integer.parseInt(str.substring(str.indexOf(allPrefix) + allPrefix.length()));
                        break;
                    }
                }
                for (String str : d2) {
                    if (str.startsWith(allPrefix)) {
                        index2 = Integer.parseInt(str.substring(str.indexOf(allPrefix) + allPrefix.length()));
                        break;
                    }
                }
                return index1 - index2;
            });

            Collections.shuffle(unrecommendEntities, new Random()); // 随机

            int todayCount = todayEntities.size();
            int recommendCount = recommendEntities.size();
            int unrecommendCount = unrecommendEntities.size();
            int dstCount = Math.min(pageNum * itemCount, todayCount + recommendCount + unrecommendCount);

            List<ImageEntityNew> all = new ArrayList<>();
            all.addAll(todayEntities);
            all.addAll(recommendEntities);
            all.addAll(unrecommendEntities);
            List<ImageEntityNew> result = new ArrayList<>(all.subList(0, dstCount));

            LogUtils.e(TAG, "-->  queryAllInHome()   count=" + count + "  todayCount=" + todayCount + "  recommendCount=" + recommendCount + "  unrecommendCount=" + unrecommendCount);
            int totalSize = todayCount + recommendCount + unrecommendCount;
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onCompleted(totalSize, result);
                }
            });
        });
    }

    public List<ImageEntityNew> queryAllTodayInHome() {
        List<ImageEntityNew> entities = imageDao.queryAllInHomeByRange(MyTimeUtils.getStartOfToday(), MyTimeUtils.getEndOfToday());
        LogUtils.e(TAG, "-->  queryAllTodayInHome()   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public List<ImageEntityNew> queryAllRecommendInHome(long endTime) {
        List<ImageEntityNew> entities = imageDao.queryAllRecommendInHome(endTime);
        LogUtils.e(TAG, "-->  queryAllRecommendInHome()   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public List<ImageEntityNew> queryAllUnrecommendInHome(long endTime) {
        List<ImageEntityNew> entities = imageDao.queryAllUnrecommendInHome(endTime);
        LogUtils.e(TAG, "-->  queryAllUnrecommendInHome()   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public void queryByFromType(String fromType, QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryByFromType(fromType);
            LogUtils.e(TAG, "-->  queryByFromType()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryByCategory(String category, QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryByCategory(category, MyTimeUtils.getEndOfToday());
            List<ImageEntityNew> recommendEntities = queryRecommendCategory(category, MyTimeUtils.getEndOfToday());
            List<ImageEntityNew> unrecommendEntities = queryUnrecommendCategory(category, MyTimeUtils.getEndOfToday());

            LogUtils.e(TAG, "-->  queryByCategory()  category=" + category + "   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryByCategory(String category, int pageNum, int itemCount, QueryPageCallback<List<ImageEntityNew>> callback) {
        dbHandler.post(() -> {
            int count = imageDao.countByCategory(category, MyTimeUtils.getEndOfToday());
            List<ImageEntityNew> todayEntities = queryTodayCategory(category);
            List<ImageEntityNew> recommendEntities = queryRecommendCategory(category, MyTimeUtils.getEndOfYesterday());
            List<ImageEntityNew> unrecommendEntities = queryUnrecommendCategory(category, MyTimeUtils.getEndOfYesterday());

            Collections.sort(recommendEntities, (e1, e2) -> { // 曝光位置排序
                String catPrefix = category + "_";
                int index1 = 0;
                int index2 = 0;
                List<String> d1 = e1.display;
                List<String> d2 = e2.display;
                for (String str : d1) {
                    if (str.startsWith(catPrefix)) {
                        index1 = Integer.parseInt(str.substring(str.indexOf(catPrefix) + catPrefix.length()));
                        break;
                    }
                }
                for (String str : d2) {
                    if (str.startsWith(catPrefix)) {
                        index2 = Integer.parseInt(str.substring(str.indexOf(catPrefix) + catPrefix.length()));
                        break;
                    }
                }
                return index1 - index2;
            });

            Collections.shuffle(unrecommendEntities, new Random()); // 随机

            int todayCount = todayEntities.size();
            int recommendCount = recommendEntities.size();
            int unrecommendCount = unrecommendEntities.size();
            int dstCount = Math.min(pageNum * itemCount, todayCount + recommendCount + unrecommendCount);

            List<ImageEntityNew> all = new ArrayList<>();
            all.addAll(todayEntities);
            all.addAll(recommendEntities);
            all.addAll(unrecommendEntities);
            List<ImageEntityNew> result = new ArrayList<>(all.subList(0, dstCount));

            LogUtils.e(TAG, "-->  queryByCategory()   category=" + category + "  count=" + count + "  todayCount=" + todayCount + "  recommendCount=" + recommendCount + "  unrecommendCount=" + unrecommendCount);
            int totalSize = todayCount + recommendCount + unrecommendCount;
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onCompleted(totalSize, result);
                }
            });
        });
    }

    public List<ImageEntityNew> queryTodayCategory(String category) {
        List<ImageEntityNew> entities = imageDao.queryCategoryByRange(category, MyTimeUtils.getStartOfToday(), MyTimeUtils.getEndOfToday());
        LogUtils.e(TAG, "-->  queryTodayCategory()   category=" + category + "   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public List<ImageEntityNew> queryRecommendCategory(String category, long endTime) {
        List<ImageEntityNew> entities = imageDao.queryRecommendCategory(category, endTime);
        LogUtils.e(TAG, "-->  queryRecommendCategory()    category=" + category + "   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public List<ImageEntityNew> queryUnrecommendCategory(String category, long endTime) {
        List<ImageEntityNew> entities = imageDao.queryUnrecommendCategory(category, endTime);
        LogUtils.e(TAG, "-->  queryUnrecommendCategory()    category=" + category + "   entities.size=" + entities.size() + "  entities=" + entities);
        return entities;
    }

    public void queryCompleted(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryCompleted();
            LogUtils.e(TAG, "-->  queryCompleted()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryInProgress(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryInProgress();
            LogUtils.e(TAG, "-->  queryInProgress()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryDailyInToday(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryDailyInRange(MyTimeUtils.getStartOfToday(), MyTimeUtils.getEndOfToday());
            LogUtils.e(TAG, "-->  queryDailyInToday()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryWithoutColor(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryWithoutColor(MyTimeUtils.getEndOfToday());
            LogUtils.e(TAG, "-->  queryWithoutColor()   entities.size=" + entities.size() + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }


    // 用于判断是否存在历史记录
    public void countAll(QueryCountCallback callback) {
        dbHandler.post(() -> {
            int count = imageDao.countImage();
            LogUtils.e(TAG, "-->  countAll()  count=" + count);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }

    // 判断是否存在匹配 storeDir 的记录
    public int countByStoreDirSync(String storeDir) {
        int count = imageDao.countByStoreDir(storeDir);
        LogUtils.e(TAG, "-->  countByStoreDir()  count=" + count);
        return count;
    }

    /**
     * int[0] -> countCompleted
     * int[1] -> countInProgress
     */
    public void countInProgressAndCompleted(CompleteCallback<int[]> callback) {
        dbHandler.post(() -> {
            int countCompleted = imageDao.countCompleted();
            int countInProgress = imageDao.countInProgress();
            LogUtils.e(TAG, "-->  countInProgressAndCompleted()  countCompleted=" + countCompleted + "  countInProgress=" + countInProgress);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onCompleted(new int[]{countCompleted, countInProgress});
                }
            });
        });
    }


    private static class ImageDbHandler extends Handler {

        public ImageDbHandler(Looper looper) {
            super(looper);
        }
    }

    public interface QueryImageCallback {
        void onSuccess(List<ImageEntityNew> entities);
    }

    public interface QueryPageCallback<T> {
        void onCompleted(int totalSize, T result);
    }

    public interface QueryCountCallback {
        void onSuccess(int count);
    }

    public interface OnImageDbChangedListener {
        void onImageAdded(String category, int imageId);
        void onImageUpdated(String category, int imageId);
        void onDailyChanged();
    }

    public void addOnDbChangedListener(@NonNull OnImageDbChangedListener listener) {
        if (!onImageDbChangedListeners.contains(listener)) {
            onImageDbChangedListeners.add(listener);
        }
    }

    public void removeOnDbChangedListener(@NonNull OnImageDbChangedListener listener) {
        onImageDbChangedListeners.remove(listener);
    }

    private void notifyOnImageAdded(String category, int imageId) {
        for (OnImageDbChangedListener listener : onImageDbChangedListeners) {
            listener.onImageAdded(category, imageId);
        }
    }

    private void notifyOnImageUpdated(String category, int imageId) {
        for (OnImageDbChangedListener listener : onImageDbChangedListeners) {
            listener.onImageUpdated(category, imageId);
        }
    }

    private void notifyOnDailyChanged() {
        for (OnImageDbChangedListener listener : onImageDbChangedListeners) {
            listener.onDailyChanged();
        }
    }
}
