package com.project_ci01.app.dao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.common.CompleteCallback;
import com.project_ci01.app.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

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

    public void addImage(ImageEntity entity) {
        dbHandler.post(() -> {
            long result = imageDao.addImage(entity);
            LogUtils.e(TAG, "-->  addImage()  result=" + result);

            mainHandler.post(this::notifyOnDbChanged);
        });
    }

    public void deleteImage(ImageEntity entity) {
        dbHandler.post(() -> {
            int result = imageDao.deleteImage(entity);
            LogUtils.e(TAG, "-->  deleteImage()  result=" + result);
            mainHandler.post(this::notifyOnDbChanged);
        });
    }

    public void deleteImages(List<ImageEntity> entities) {
        dbHandler.post(() -> {
            int result = imageDao.deleteImage(entities);
            LogUtils.e(TAG, "-->  deleteImages()  result=" + result);
            mainHandler.post(this::notifyOnDbChanged);
        });
    }

    public void deleteAll() {
        dbHandler.post(() -> {
            int result = imageDao.deleteAll();
            LogUtils.e(TAG, "-->  deleteAll()  result=" + result);
            mainHandler.post(this::notifyOnDbChanged);
        });
    }


    public void updateImage(ImageEntity entity) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = queryByStoreDir(entity.storeDir);
            if (entities != null && !entities.isEmpty()) {
                for (ImageEntity tmp : entities) {
                    if (entity.storeDir.equals(tmp.storeDir)) {
                        tmp.colorTime = entity.colorTime;
                        tmp.fileName = entity.fileName;
                        tmp.netUrl = entity.netUrl;
                        tmp.fromType = entity.fromType;
                        tmp.category = entity.category;
                        tmp.storeDir = entity.storeDir;
                        tmp.originImagePath = entity.originImagePath;
                        tmp.colorImagePath = entity.colorImagePath;
                        tmp.pixelsObjPath = entity.pixelsObjPath;
                        tmp.completed = entity.completed;
                        int result = imageDao.updateImage(tmp);
                        LogUtils.e(TAG, "-->  updateImage()  result=" + result);
                        mainHandler.post(this::notifyOnDbChanged);
                    }
                }
            } else {
                addImage(entity);
            }
        });
    }

    public void updateImageSync(ImageEntity entity) {
        List<ImageEntity> entities = queryByStoreDir(entity.storeDir);
        if (entities != null && !entities.isEmpty()) {
            for (ImageEntity tmp : entities) {
                if (entity.storeDir.equals(tmp.storeDir)) {
                    tmp.colorTime = entity.colorTime;
                    tmp.fileName = entity.fileName;
                    tmp.netUrl = entity.netUrl;
                    tmp.fromType = entity.fromType;
                    tmp.category = entity.category;
                    tmp.storeDir = entity.storeDir;
                    tmp.originImagePath = entity.originImagePath;
                    tmp.colorImagePath = entity.colorImagePath;
                    tmp.pixelsObjPath = entity.pixelsObjPath;
                    tmp.completed = entity.completed;
                    int result = imageDao.updateImage(tmp);
                    LogUtils.e(TAG, "-->  updateImage()  result=" + result);
                    mainHandler.post(this::notifyOnDbChanged);
                }
            }
        } else {
            addImage(entity);
        }
    }

    private List<ImageEntity> queryByCreateTime(long createTime) {
        List<ImageEntity> entities = imageDao.queryByCreateTime(createTime);
        LogUtils.e(TAG, "-->  queryByCreateTime()  entities=" + entities);
        return entities;
    }

    /**
     * storeDir 相同的 ImageEntity 视为同一个
     */
    private List<ImageEntity> queryByStoreDir(String storeDir) {
        List<ImageEntity> entities = imageDao.queryByStoreDir(storeDir);
        LogUtils.e(TAG, "-->  queryByStoreDir()  entities=" + entities);
        return entities;
    }

    public void queryAll(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryAll();
            LogUtils.e(TAG, "-->  queryAll()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryAllInHome(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryAllInHome();
            LogUtils.e(TAG, "-->  queryAllInHome()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryByFromType(String fromType, QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryByFromType(fromType);
            LogUtils.e(TAG, "-->  queryByFromType()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryByCategory(String category, QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryByCategory(category);
            LogUtils.e(TAG, "-->  queryByCategory()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryCompleted(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryCompleted();
            LogUtils.e(TAG, "-->  queryCompleted()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryInProgress(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryInProgress();
            LogUtils.e(TAG, "-->  queryInProgress()  entities=" + entities);
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


    // 判断某段时间是否有记录
    public void countByTimeRange(long startTime, long endCTime, QueryCountCallback callback) {
        dbHandler.post(() -> {
            int count = imageDao.countByCreateTimeRange(startTime, endCTime);
            LogUtils.e(TAG, "-->  countByTimeRange()  count=" + count);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }


    // 获取某段时间的所有记录
    public void queryByTimeRange(long startTime, long endCTime, QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntity> entities = imageDao.queryByCreateTimeRange(startTime, endCTime);
            LogUtils.e(TAG, "-->  queryByTimeRange()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
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
        void onSuccess(List<ImageEntity> entities);
    }

    public interface QueryCountCallback {
        void onSuccess(int count);
    }

    public interface OnImageDbChangedListener {
        void onImageDbChanged(); // 数据库发生了变化
    }

    public void addOnDbChangedListener(@NonNull OnImageDbChangedListener listener) {
        if (!onImageDbChangedListeners.contains(listener)) {
            onImageDbChangedListeners.add(listener);
        }
    }

    public void removeOnDbChangedListener(@NonNull OnImageDbChangedListener listener) {
        onImageDbChangedListeners.remove(listener);
    }

    private void notifyOnDbChanged() {
        for (OnImageDbChangedListener listener : onImageDbChangedListeners) {
            listener.onImageDbChanged();
        }
    }
}
