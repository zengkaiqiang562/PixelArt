package com.project_m1142.app.base.view.holder;

public interface ActivityLifecycleObserver extends LifecycleObserver {

    void onCreate();

    void onRestart();

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    boolean onBackPressed();
}
