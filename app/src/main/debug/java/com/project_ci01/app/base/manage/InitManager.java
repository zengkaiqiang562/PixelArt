package com.project_ci01.app.base.manage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum InitManager {
    INSTANCE;

//    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void init() {

//        IntegrateSdkManager.INSTANCE.init(LifecyclerManager.INSTANCE.getApplication());
//
//        ConfigManager.INSTANCE.initRemoteConfig(/*true*/);
//
//        LocationManager.INSTANCE.initLocation();
//
//        executorService.submit(() -> {
//            IntegrateSdkManager.loadGid(LifecyclerManager.INSTANCE.getApplication());
//        });
    }
}
