package com.project_m1142.app.wifi.ext.dao;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.utils.LogUtils;

import java.util.List;

public class WifiDbManager {
    private static final String TAG = "WifiDbManager";
    private volatile static WifiDbManager instance;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private final WifiHandler wifiHandler;

    private final WifiDao wifiDao;

    public static WifiDbManager getInstance() {
        if(instance == null) {
            synchronized(WifiDbManager.class) {
                if(instance == null) {
                    instance = new WifiDbManager();
                }
            }
        }
        return instance;
    }

    private WifiDbManager() {
        /*
         * 历史记录 db 操作的 Handler 线程
         */
        HandlerThread wifiThread = new HandlerThread("thread_wifi");
        wifiThread.start();
        wifiHandler = new WifiHandler(wifiThread.getLooper());
        WifiDatabase database = WifiDatabase.getInstance();
        wifiDao = database.wifiDao();
    }

    public void addWifi(WifiEntity entity) {
        wifiHandler.post(() -> {
            long result = wifiDao.addWifi(entity);
            LogUtils.e(TAG, "-->  addWifi()  result=" + result);
        });
    }

    public void deleteWifi(WifiEntity entity) {
        wifiHandler.post(() -> {
            int result = wifiDao.deleteWifi(entity);
            LogUtils.e(TAG, "-->  deleteWifi()  result=" + result);
        });
    }

    public void deleteBySsid(String ssid) {
        wifiHandler.post(() -> {
            int result = wifiDao.deleteBySsid(ssid);
            LogUtils.e(TAG, "-->  deleteBySsid()  result=" + result);
        });
    }

    public void deleteWifi(List<WifiEntity> entities) {
        wifiHandler.post(() -> {
            int result = wifiDao.deleteWifi(entities);
            LogUtils.e(TAG, "-->  deleteWifiList()  result=" + result);
        });
    }

    public void deleteAll() {
        wifiHandler.post(() -> {
            int result = wifiDao.deleteAll();
            LogUtils.e(TAG, "-->  deleteAll()  result=" + result);
        });
    }


    public void updateWifi(WifiEntity wifi) {
        wifiHandler.post(() -> {
            List<WifiEntity> entities = queryBySsid(wifi.ssid);
            if (entities != null && !entities.isEmpty()) {
                LogUtils.e(TAG, "-->  updateWifi()  entities.size=" + entities.size());
                for (WifiEntity tmp : entities) {
                    tmp.updateTime = System.currentTimeMillis();
                    tmp.ssid = wifi.ssid;
                    tmp.bssid = wifi.bssid;
                    tmp.level = wifi.level;
                    tmp.wifiStandard = wifi.wifiStandard;
                    tmp.password = wifi.password;
                    tmp.isEncrypt = wifi.isEncrypt;
                    tmp.encryption = wifi.encryption;
                    tmp.capabilities = wifi.capabilities;
                    tmp.centerFreq0 = wifi.centerFreq0;
                    tmp.centerFreq1 = wifi.centerFreq1;
                    tmp.frequency = wifi.frequency;
                    int result = wifiDao.updateWifi(tmp);
                    LogUtils.e(TAG, "-->  updateWifi()  result=" + result);
                }
            } else { // health 可能是记录清空后，在首页显示的默认数据，此时直接添加
                addWifi(wifi);
            }
        });
    }

    private List<WifiEntity> queryBySsid(@NonNull String ssid) {
        List<WifiEntity> entities = wifiDao.queryBySsid(ssid);
        LogUtils.e(TAG, "-->  queryBySsid()  entities=" + entities);
        return entities;
    }

    public void queryBySsid(QueryWifiCallback callback, @NonNull String ssid) {
        wifiHandler.post(() -> {
            List<WifiEntity> entities = wifiDao.queryBySsid(ssid);
            LogUtils.e(TAG, "-->  queryBySsid()  ssid=" + ssid + "  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }

    public void queryAll(QueryWifiCallback callback) {
        wifiHandler.post(() -> {
            List<WifiEntity> entities = wifiDao.queryAll();
            LogUtils.e(TAG, "-->  queryAll()  entities=" + entities);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(entities);
                }
            });
        });
    }


    // 用于判断是否存在历史记录
    public void countAll(QueryCountCallback callback) {
        wifiHandler.post(() -> {
            int count = wifiDao.countWifi();
            LogUtils.e(TAG, "-->  countAll()  count=" + count);
            mainHandler.post(() -> {
                if (callback != null) {
                    callback.onSuccess(count);
                }
            });
        });
    }

    private static class WifiHandler extends Handler {

        public WifiHandler(Looper looper) {
            super(looper);
        }
    }

    public interface QueryWifiCallback {
        void onSuccess(List<WifiEntity> entities);
    }

    public interface QueryCountCallback {
        void onSuccess(int count);
    }
}
