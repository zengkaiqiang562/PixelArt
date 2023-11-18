package com.project_ci01.app.base;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.ProcessUtils;
import com.project_ci01.app.base.manage.LifecyclerManager;
import com.project_ci01.app.base.utils.LogUtils;

public abstract class BaseApplication extends Application {

    protected static final String TAG = "BaseApplication";

    protected Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 主进程创建
     */
    protected abstract void onMainCreate();

    protected void createNotificationChannels() {}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        boolean isMainProcess = isMainProcess();
        LogUtils.e(TAG, "================ BaseApplication onCreate()  isMainProcess=" + isMainProcess + " ================");

        if (isMainProcess) {
            LifecyclerManager.INSTANCE.main(this);

            createNotificationChannels();

            onMainCreate();
        }
    }

    protected boolean isMainProcess() {
        return ProcessUtils.isMainProcess();
    }
}
