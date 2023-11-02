package com.project_m1142.app.dao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.project_m1142.app.base.utils.LogUtils;

import java.util.List;

public class TestHistoryDbManager {
    private static final String TAG = "TestHistoryDbManager";
    private volatile static TestHistoryDbManager instance;

    private static final int MAX_NUM = 500; // 最多保存 500 条记录

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final HistoryHandler historyHandler;

    private final TestHistoryDao historyDao;

    public static TestHistoryDbManager getInstance() {
        if(instance == null) {
            synchronized(TestHistoryDbManager.class) {
                if(instance == null) {
                    instance = new TestHistoryDbManager();
                }
            }
        }
        return instance;
    }

    private TestHistoryDbManager() {
        /*
         * 历史记录 db 操作的 Handler 线程
         */
        HandlerThread historyThread = new HandlerThread("thread_test_history");
        historyThread.start();
        historyHandler = new HistoryHandler(historyThread.getLooper());
        MyDatabase database = MyDatabase.getInstance();
        historyDao = database.testHistoryDao();
        trimHistory();
    }

    public void addHistory(TestHistoryEntity entity) {
        historyHandler.post(() -> {
            long result = historyDao.addHistory(entity);
            LogUtils.e(TAG, "-->  addHistory()  result=" + result);
        });
    }

    public void deleteHistory(TestHistoryEntity entity) {
        historyHandler.post(() -> {
            int result = historyDao.deleteHistory(entity);
            LogUtils.e(TAG, "-->  deleteHistory()  result=" + result);
        });
    }

    public void deleteHistory(List<TestHistoryEntity> entities) {
        historyHandler.post(() -> {
            int result = historyDao.deleteHistory(entities);
            LogUtils.e(TAG, "-->  deleteHistoryList()  result=" + result);
        });
    }

    public void deleteAll() {
        historyHandler.post(() -> {
            int result = historyDao.deleteAll();
            LogUtils.e(TAG, "-->  deleteAll()  result=" + result);
        });
    }


    public void updateHistory(TestHistoryEntity history) {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = queryByCreateTime(history.createTime);
            if (entities != null && !entities.isEmpty()) {
                for (TestHistoryEntity tmp : entities) {
                    if (history.createTime == tmp.createTime) {
                        tmp.device = history.device;
                        tmp.delay = history.delay;
                        tmp.rxRate = history.rxRate;
                        tmp.txRate = history.txRate;
                        tmp.netName = history.netName;
                        tmp.netMode = history.netMode;
                        tmp.signal = history.signal;
                        tmp.dns = history.dns;
                        int result = historyDao.updateHistory(tmp);
                        LogUtils.e(TAG, "-->  updateHistory()  result=" + result);
                    }
                }
            } else { // health 可能是记录清空后，在首页显示的默认数据，此时直接添加
                addHistory(history);
            }
        });
    }

    private List<TestHistoryEntity> queryByCreateTime(long createTime) {
        List<TestHistoryEntity> entities = historyDao.queryByCreateTime(createTime);
        LogUtils.e(TAG, "-->  queryByCreateTime()  entities=" + entities);
        return entities;
    }

    public void queryAll(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = historyDao.queryAll();
            LogUtils.e(TAG, "-->  queryAll()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryLast7(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = historyDao.queryLast7();
            LogUtils.e(TAG, "-->  queryLast7()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryLast2(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = historyDao.queryLast2();
            LogUtils.e(TAG, "-->  queryLast2()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryLast1(QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = historyDao.queryLast1();
            LogUtils.e(TAG, "-->  queryLast1()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }


    // 用于判断是否存在历史记录
    public void countAll(QueryCountCallback callback) {
        historyHandler.post(() -> {
            int count = historyDao.countHistory();
            LogUtils.e(TAG, "-->  countAll()  count=" + count);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }

    // 判断某段时间是否有记录
    public void countDataOfTimeRange(long startTime, long endCTime, QueryCountCallback callback) {
        historyHandler.post(() -> {
            int count = historyDao.countByCreateTimeRange(startTime, endCTime);
            LogUtils.e(TAG, "-->  countDataOfToday()  count=" + count);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }


    // 获取某段时间的所有记录
    public void queryDataOfTimeRange(long startTime, long endCTime, QueryHistoryCallback callback) {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = historyDao.queryByCreateTimeRange(startTime, endCTime);
            LogUtils.e(TAG, "-->  queryDataOfToday()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    // 最多只保存最近的1000个记录
    private void trimHistory() {
        historyHandler.post(() -> {
            List<TestHistoryEntity> entities = historyDao.queryAll();
            if (entities.size() > MAX_NUM) {
                List<TestHistoryEntity> expiredEntities = entities.subList(MAX_NUM, entities.size());
                historyDao.deleteHistory(expiredEntities);
            }
        });
    }

    private static class HistoryHandler extends Handler {

        public HistoryHandler(Looper looper) {
            super(looper);
        }
    }

    public interface QueryHistoryCallback {
        void onSuccess(List<TestHistoryEntity> entities);
    }

    public interface QueryCountCallback {
        void onSuccess(int count);
    }
}
