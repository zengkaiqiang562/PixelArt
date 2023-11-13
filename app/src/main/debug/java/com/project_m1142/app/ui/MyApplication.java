package com.project_m1142.app.ui;

import android.app.Activity;
import android.content.Intent;

// import androidx.appcompat.app.AppCompatDelegate;
// import androidx.work.Configuration;

import com.project_ci01.app.pixel.PixelManager;
import com.project_m1142.app.base.BaseApplication;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.LifecyclerManager;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.network.TrafficChartManager;
//import com.project_m1142.app.network.TrafficManager;
import com.project_m1142.app.ui.activity.HomeActivity;
import com.project_m1142.app.ui.activity.StartActivity;

public class MyApplication extends BaseApplication/*  implements Configuration.Provider */ {

    private static final String TAG = "MyApplication";

    @Override
    protected void onMainCreate() {

//        TrafficManager.getInstance().initTraffic();
        TrafficChartManager.getInstance().startTraffic();

        LifecyclerManager.INSTANCE.addLifecycleCallback(new LifecyclerManager.LifecycleCallback() {
            @Override
            public void onAppForeground(Activity activity) {

                if (activity instanceof BaseActivity && ((BaseActivity) activity).isSkipStartLoading()) {
                    return; // 如果热启动之前是在请求权限，则直接返回到之前的界面即可
                }

                Activity homeActivity = ContextManager.INSTANCE.peekActivity(HomeActivity.class);
                if (homeActivity != null) {
                    homeActivity.startActivity(new Intent(homeActivity, StartActivity.class));
                }
            }

            @Override
            public void onAppBackground(Activity activity) {

            }
        });

        PixelManager.getInstance().loadLocalImages();
    }

    // @NonNull
    // @Override
    // public Configuration getWorkManagerConfiguration() {
    //     return Core.INSTANCE.getWorkManagerConfiguration();
    // }

    // @Override
    // public void onConfigurationChanged(@NonNull android.content.res.Configuration newConfig) {
    //     super.onConfigurationChanged(newConfig);
    //     Core.INSTANCE.updateNotificationChannels();
    // }
}
