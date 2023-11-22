package com.project_ci01.app.dao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.common.CompleteCallback;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.utils.MyTimeUtils;

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

    public void addImage(ImageEntityNew entity) {
        dbHandler.post(() -> {
            long result = imageDao.addImage(entity);
            LogUtils.e(TAG, "-->  addImage()  result=" + result);

            mainHandler.post(this::notifyOnDbChanged);
        });
    }


    public void updateImage(ImageEntityNew entity) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = queryByStoreDir(entity.storeDir);
            if (entities != null && !entities.isEmpty()) {
                for (ImageEntityNew tmp : entities) {
                    if (entity.storeDir.equals(tmp.storeDir)) {
                        tmp.createTime = entity.createTime;
                        tmp.imageId = entity.imageId;
                        tmp.fileName = entity.fileName;
                        tmp.description = entity.description;
                        tmp.permission = entity.permission;
                        tmp.display = entity.display;
                        tmp.category = entity.category;
                        tmp.fromType = entity.fromType;
                        tmp.filePath = entity.filePath;
                        tmp.storeDir = entity.storeDir;
                        tmp.colorImagePath = entity.colorImagePath;
                        tmp.pixelsObjPath = entity.pixelsObjPath;
                        tmp.colorTime = entity.colorTime;
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

    public void updateImageSync(ImageEntityNew entity) {
        List<ImageEntityNew> entities = queryByStoreDir(entity.storeDir);
        if (entities != null && !entities.isEmpty()) {
            for (ImageEntityNew tmp : entities) {
                if (entity.storeDir.equals(tmp.storeDir)) {
                    tmp.createTime = entity.createTime;
                    tmp.imageId = entity.imageId;
                    tmp.fileName = entity.fileName;
                    tmp.description = entity.description;
                    tmp.permission = entity.permission;
                    tmp.display = entity.display;
                    tmp.category = entity.category;
                    tmp.fromType = entity.fromType;
                    tmp.filePath = entity.filePath;
                    tmp.storeDir = entity.storeDir;
                    tmp.colorImagePath = entity.colorImagePath;
                    tmp.pixelsObjPath = entity.pixelsObjPath;
                    tmp.colorTime = entity.colorTime;
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

    /**
     * storeDir 相同的 ImageEntityNew 视为同一个
     */
    private List<ImageEntityNew> queryByStoreDir(String storeDir) {
        List<ImageEntityNew> entities = imageDao.queryByStoreDir(storeDir);
        LogUtils.e(TAG, "-->  queryByStoreDir()  entities=" + entities);
        return entities;
    }

    public void queryAll(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryAll();
            LogUtils.e(TAG, "-->  queryAll()  entities=" + entities);
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

    public void queryAllInHome(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryAllInHome(MyTimeUtils.getEndOfToday());
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
            List<ImageEntityNew> entities = imageDao.queryByFromType(fromType);
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
            List<ImageEntityNew> entities = imageDao.queryByCategory(category, MyTimeUtils.getEndOfToday());
            LogUtils.e(TAG, "-->  queryByCategory()  category=" + category + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryCompleted(QueryImageCallback callback) {
        dbHandler.post(() -> {
            List<ImageEntityNew> entities = imageDao.queryCompleted();
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
            List<ImageEntityNew> entities = imageDao.queryInProgress();
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




    private static class ImageDbHandler extends Handler {

        public ImageDbHandler(Looper looper) {
            super(looper);
        }
    }

    public interface QueryImageCallback {
        void onSuccess(List<ImageEntityNew> entities);
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
