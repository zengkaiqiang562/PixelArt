package com.project_ci01.app.base.manage;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.blankj.utilcode.util.SPUtils;
import com.project_ci01.app.base.constants.SPConstants;
import com.project_ci01.app.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public enum LifecyclerManager implements Application.ActivityLifecycleCallbacks {

    INSTANCE;

    private static final String TAG = "LifecyclerManager";

    private Application application;

    private static boolean inBackground = false;
    private static boolean inForeground = false;
    private static boolean hotLaunch = false;
    private int count = 0;

    private final List<LifecycleCallback> lifecycleCallbacks = new ArrayList<>();

    public void main(@NonNull Application application) {
        this.application = application;

        application.registerActivityLifecycleCallbacks(this);

        boolean neverStart = SPUtils.getInstance().getBoolean(SPConstants.SP_NEVER_START, true);
        if (neverStart) {
            SPUtils.getInstance().put(SPConstants.SP_NEVER_START, false);
            // record 1st launch time as the time of install app
            SPUtils.getInstance().put(SPConstants.SP_INSTALL_TIME, System.currentTimeMillis());
        }

        LogUtils.e(TAG, "--> neverStart=" + neverStart);

        InitManager.INSTANCE.init();
    }

    public Application getApplication() {
        return application;
    }

    public boolean isHotlaunch() {
        return hotLaunch;
    }

    public boolean isInBackground() {
        return inBackground;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        LogUtils.e(TAG, "--> onActivityCreated()  activity=" + activity);
//        AdmobService.addAdmob(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        LogUtils.e(TAG, "--> onActivityStarted()  activity=" + activity);
        if (++count == 1) { // 热启动
            LogUtils.e(TAG, "--> App Foreground");

            if (inBackground) {
                hotLaunch = true;
            }
            inBackground = false;
            inForeground = true;

            notifyAppForeground(activity);
        }

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        LogUtils.e(TAG, "--> onActivityResumed()  activity=" + activity);
        Adjust.onResume();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        LogUtils.e(TAG, "--> onActivityPaused()  activity=" + activity);
        Adjust.onPause();
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        LogUtils.e(TAG, "--> onActivityStopped()  activity=" + activity);
        if (--count <= 0) {
            LogUtils.e(TAG, "--> App Background");
            count = 0;

            inBackground = true;
            inForeground = false;

            notifyAppBackground(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        LogUtils.e(TAG, "--> onActivityDestroyed()  activity=" + activity);
//        AdmobService.removeAdmob(activity);
    }

    public interface LifecycleCallback {
        void onAppForeground(Activity activity);
        void onAppBackground(Activity activity);
    }

    public void addLifecycleCallback(LifecycleCallback callback) {
        if (callback != null && !lifecycleCallbacks.contains(callback)) {
            lifecycleCallbacks.add(callback);
        }
    }

    public void removeLifecycleCallback(LifecycleCallback callback) {
        if (callback != null) {
            lifecycleCallbacks.remove(callback);
        }
    }

    private void notifyAppForeground(Activity activity) {
        for (LifecycleCallback callback : lifecycleCallbacks) {
            callback.onAppForeground(activity);
        }
    }

    private void notifyAppBackground(Activity activity) {
        for (LifecycleCallback callback : lifecycleCallbacks) {
            callback.onAppBackground(activity);
        }
    }
}
