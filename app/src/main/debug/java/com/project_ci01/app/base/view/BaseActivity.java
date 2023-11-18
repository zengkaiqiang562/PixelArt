package com.project_ci01.app.base.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.view.holder.ActivityLifecycleObserver;
import com.project_ci01.app.base.view.holder.LifecycleObservable;
import com.gyf.immersionbar.ImmersionBar;
import com.project_ci01.app.base.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity implements LifecycleObservable<ActivityLifecycleObserver> {
    public boolean isPaused = false;
    public boolean isResumed = false;
    protected boolean turning = false;

    protected boolean skipStartLoading = false; // 是否跳过启动加载流程

    protected UIHandler<?> uiHandler;

    protected String TAG = "BaseActivity";

    private final List<ActivityLifecycleObserver> lifecycleObservers = new ArrayList<>();

    protected abstract String tag();
    protected abstract void setContentView();
    protected abstract View stubBar();

    protected void handleMessage(@NonNull Message msg) {}

    protected void onFirstResume() {
        LogUtils.e(TAG, "--> onFirstResume()");
    }

    protected void back() {
        setResult(RESULT_OK, null);
        finish();
    }

    /**
     * @return 返回 true 表示可以跳转 UI，避免快速重复点击
     */
    public boolean checkTurnFlag() {
        return !turning && (turning = true);
    }

    public void setSkipStartLoading(boolean skipStartLoading) {
        this.skipStartLoading = skipStartLoading;
    }

    public boolean isSkipStartLoading() {
        return skipStartLoading;
    }

    @Override
    public void addLifecycleObserver(ActivityLifecycleObserver callback) {
        if (!lifecycleObservers.contains(callback)) {
            lifecycleObservers.add(callback);
        }
    }

    @Override
    public void removeLifecycleObserver(ActivityLifecycleObserver callback) {
        lifecycleObservers.remove(callback);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = tag();
        LogUtils.e(TAG, "--> onCreate()");
        initBar();
        setContentView();
        fillStatusbar(stubBar());
        ContextManager.INSTANCE.addActivity(this);
        uiHandler = new UIHandler<>(this);

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onCreate();
            }
        }
    }

    protected void initBar() {
        ImmersionBar.with(this)/*.transparentNavigationBar()*/.transparentStatusBar().statusBarDarkFont(true).init();
    }

    private void fillStatusbar(@Nullable View stubBar) {
        if (stubBar != null) {
            int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
            int dp44 = ConvertUtils.dp2px(44);
            if (statusBarHeight < dp44) {
                statusBarHeight = dp44;
            }
            ViewGroup.LayoutParams layoutParams = stubBar.getLayoutParams();
            layoutParams.height = statusBarHeight;
            stubBar.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.e(TAG, "--> onNewIntent()");
        ContextManager.INSTANCE.addActivity(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.e(TAG, "--> onConfigurationChanged()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e(TAG, "--> onRestart()");

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onRestart();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e(TAG, "--> onStart()");

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e(TAG, "--> onResume()");
        isResumed = true;
        turning = false;
        if (!isPaused) {
            onFirstResume();
        }
        isPaused = false;
        skipStartLoading = false;

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onResume();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e(TAG, "--> onPause()");
        isResumed = false;
        isPaused = true;

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onPause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e(TAG, "--> onStop()");
        isResumed = false;
        isPaused = true;

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onStop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "--> onDestroy()");
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
        }


        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                callback.onDestroy();
            }
        }

        ContextManager.INSTANCE.removeActivity(this);
    }

    @Override
    public void onBackPressed() {

        boolean handle = false;

        if (lifecycleObservers != null) {
            for (ActivityLifecycleObserver callback : lifecycleObservers) {
                if (callback.onBackPressed()) {
                    handle = true;
                }
            }
        }

        if (!handle) { // 没有 BaseViewHolder 处理 返回键，才交给 Activity 处理
            back();
        }
    }

    protected static class UIHandler<T extends BaseActivity> extends Handler {

        protected WeakReference<T> wRefActivity;

        public UIHandler(T activity) {
            super(Looper.getMainLooper());
            wRefActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            T activity = wRefActivity.get();
            if (!ContextManager.isSurvival(activity)) {
                return;
            }
            activity.handleMessage(msg);
        }
    }
}
