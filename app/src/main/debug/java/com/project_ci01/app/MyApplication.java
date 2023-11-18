package com.project_ci01.app;

import android.app.Activity;
import android.content.Intent;

import com.project_ci01.app.activity.MainActivity;
import com.project_ci01.app.activity.StartActivity;
import com.project_ci01.app.pixel.PixelManager;
import com.project_ci01.app.base.BaseApplication;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.manage.LifecyclerManager;
import com.project_ci01.app.base.view.BaseActivity;

public class MyApplication extends BaseApplication {

    private static final String TAG = "MyApplication";

    @Override
    protected void onMainCreate() {

        LifecyclerManager.INSTANCE.addLifecycleCallback(new LifecyclerManager.LifecycleCallback() {
            @Override
            public void onAppForeground(Activity activity) {

                if (activity instanceof BaseActivity && ((BaseActivity) activity).isSkipStartLoading()) {
                    return; // 如果热启动之前是在请求权限，则直接返回到之前的界面即可
                }

                // TODO test
//                Activity homeActivity = ContextManager.INSTANCE.peekActivity(MainActivity.class);
//                if (homeActivity != null) {
//                    homeActivity.startActivity(new Intent(homeActivity, StartActivity.class));
//                }
            }

            @Override
            public void onAppBackground(Activity activity) {

            }
        });

        PixelManager.getInstance().loadLocalImages();
    }
}
