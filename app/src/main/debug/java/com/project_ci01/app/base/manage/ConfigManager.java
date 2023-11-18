package com.project_ci01.app.base.manage;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;

// TODO 去掉 Firebase
//import com.google.firebase.remoteconfig.ConfigUpdate;
//import com.google.firebase.remoteconfig.ConfigUpdateListener;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
//import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import com.project_ci01.app.base.bean.gson.ConfigBean;
import com.project_ci01.app.base.config.AppConfig;
import com.project_ci01.app.base.constants.SPConstants;
import com.project_ci01.app.base.user.User;
import com.project_ci01.app.base.user.UserService;
import com.project_ci01.app.base.utils.LogUtils;

import java.util.Calendar;

public enum ConfigManager {

    INSTANCE;

    private static final String TAG = "ConfigManager";

    private static final String CONFIG_KEY = "config";

//    private static final int WHAT_PULL_CONFIG = 1001; // 更新全局配置

//    private final ConfigCache configCache = new ConfigCache();

//    private final ConfigHandler configHandler = new ConfigHandler();

    public @Nullable ConfigBean getConfigBean() { // TODO 去掉全局配置
//        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
//        String configJson = remoteConfig.getString(CONFIG_KEY);
//        ConfigBean configBean = JsonUtils.fromJson(configJson, ConfigBean.class);
//        LogUtils.e(TAG, "--> getConfigBean()  configBean=" + configBean);
//        return configBean;
        return null;
    }

    public void initRemoteConfig(/*boolean fromUser*/) { // TODO 去掉全局配置
////        ConfigBean configBean = configCache.getConfigBean();
////        long timeout = configBean == null ? 0 : configBean.getConfigTime(); // unit: min
////
////        if (timeout <= 0) timeout = BaseConstants.TIMEOUT_CONFIG;
////
////        if (configHandler.hasMessages(WHAT_PULL_CONFIG)) {
////            configHandler.removeMessages(WHAT_PULL_CONFIG);
////        }
////        configHandler.sendEmptyMessageDelayed(WHAT_PULL_CONFIG, fromUser ? 0 : (timeout * 60L * 1000L));
//
//        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                .setMinimumFetchIntervalInSeconds(3600) // 1h
//                .build();
//        remoteConfig.setConfigSettingsAsync(configSettings);
//        Map<String, Object> defaultConfig = new HashMap<>();
//        defaultConfig.put(CONFIG_KEY, AppConfig.LOCAL_CONFIG);
//        remoteConfig.setDefaultsAsync(defaultConfig);
//
//        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
//            LogUtils.e(TAG, "initRemoteConfig fetchAndActivate isSuccessful=" + task.isSuccessful());
//            if (!task.isSuccessful()) {
//                return;
//            }
//            Boolean result = task.getResult();
//            LogUtils.e(TAG, "initRemoteConfig fetchAndActivate  result=" + result);
//        });
//
//        remoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
//            @Override
//            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
//                LogUtils.e(TAG, "initRemoteConfig onUpdate() keys=" + configUpdate.getUpdatedKeys());
//                remoteConfig.activate().addOnCompleteListener(task -> {
//                    LogUtils.e(TAG, "initRemoteConfig onUpdate() activate isSuccessful=" + task.isSuccessful());
//                    if (!task.isSuccessful()) {
//                        return;
//                    }
//                    Boolean result = task.getResult();
//                    LogUtils.e(TAG, "initRemoteConfig onUpdate() activate result=" + result);
//                });
//            }
//
//            @Override
//            public void onError(@NonNull FirebaseRemoteConfigException error) {
//                LogUtils.e(TAG, "initRemoteConfig onError() error=" + error);
//            }
//        });
    }


//    private void handlePullConfig() {
//        NetExecutor.INSTANCE.pullConfig(new Callback<BaseResponseBean<ConfigBean>>() {
//            @Override
//            public void onResponse(@NonNull Call<BaseResponseBean<ConfigBean>> call, @NonNull Response<BaseResponseBean<ConfigBean>> response) {
//                ConfigBean netConfig = response.body() == null ? null : response.body().getData();
//                LogUtils.e("请求<全局配置>成功 ## " + JsonUtils.toJson(netConfig));
//                handleNetConfig(netConfig);
////                dispatchConfig(null); // TODO test 不从网络获取全局配置
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<BaseResponseBean<ConfigBean>> call, @NonNull Throwable t) {
//                LogUtils.e("请求<全局配置>失败 ## " + t);
//                handleNetConfig(null);
//            }
//        });
//    }
//
//    private void handleNetConfig(ConfigBean netConfig) {
//        if (netConfig == null) { // 网络请求全局配置失败，从本地缓存中获取
//            configCache.loadLocalConfig();
//        } else {
//            // 网络请求全局配置成功，更新到 sprefs 中的网络缓存
//            configCache.storeConfig(netConfig);
//            EventBusHelper.post(new ConfigEvent(netConfig), false);
//        }
//        pullConfig(false);
//    }

    /**
     * 冷热启动时，检查是否需要升级 app
     */
    public void upgrade() { // TODO 去掉更新流程
//        ConfigBean configBean = getConfigBean();
//
//        if (configBean == null || configBean.getUpdateBean() == null) {
//            return;
//        }
//
//        UpdateBean updateBean = configBean.getUpdateBean();
//
//        LogUtils.e(TAG, "--> upgrade()  updateBean=" + updateBean);
//
//        String pkgName = updateBean.getPackage();
//        String title = updateBean.getTitle();
//        String message = updateBean.getMessage();
//        int version = updateBean.getVersion();
//
//        LogUtils.e(TAG, "--> upgrade()  version=" + version + " pkgName=" + pkgName);
//
//        // 文本内容为空不更新
//        if (TextUtils.isEmpty(message)) {
//            return;
//        }
//
//        // 包名为空，认为包名未改变，此时看版本号，大于当前版本号时才更新，小于等于不更新
//        if (TextUtils.isEmpty(pkgName) && version <= AppUtils.getAppVersionCode()) {
//            return;
//        }
//
//        // 包名不为空，且包名未变，此时也看版本号，大于当前版本号时才更新，小于等于不更新
//        if (!TextUtils.isEmpty(pkgName) && pkgName.equals(AppUtils.getAppPackageName()) && version <= AppUtils.getAppVersionCode()) {
//            return;
//        }
//
//        /*
//        执行到此处，说明：
//        1. 文本内容不为空；
//        2. 包名为空时，最小版本号大于当前版本号
//        3. 包名不为空，且包名相同时，最小版本号大于当前版本号
//        4. 包名不为空，且包名不同时，不考虑版本，更新不同包名的app
//         */
//
////        if (TextUtils.isEmpty(pkgName) || !pkgName.equals(AppUtils.getAppPackageName()) // 与当前 app 包名不一样时 不更新
////                || AppUtils.getAppVersionCode() >= version // 当前 app 版本号 >= 更新版本号时 不更新
////                || TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
////            return;
////        }
//
//        boolean need = updateBean.isForce();
//        boolean hotlaunch = LifecyclerManager.INSTANCE.isHotlaunch();
//        LogUtils.e(TAG, "--> upgrade()  need=" + need + " hotlaunch=" + hotlaunch);
//
//
//        if (!need && hotlaunch) {
//            // 非强制更新弹窗每天只弹出一次，冷启动完之后就不弹。
//            EventBusHelper.removeStickyEvent(UpgradeEvent.class);
//            return;
//        }
//
//        if (hasPromptUpgradeToday()) {
//            EventBusHelper.removeStickyEvent(UpgradeEvent.class);
//            return;
//        }
//
//        EventBusHelper.post(new UpgradeEvent(updateBean), true);
    }

    public boolean hasPromptUpgradeToday() {
        long lastTime = SPUtils.getInstance().getLong(SPConstants.SP_UPGRADE_TIME, 0);
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(lastTime);
        Calendar curCalendar = Calendar.getInstance();
        LogUtils.e(TAG, "--> hasPromptUpgradeToday()  lastCalendar=" + TimeUtils.date2String(lastCalendar.getTime(), "yyyy/MM/dd HH:mm")
                + "  curCalendar=" + TimeUtils.date2String(curCalendar.getTime(), "yyyy/MM/dd HH:mm") );
        if (lastCalendar.get(Calendar.DAY_OF_MONTH) == curCalendar.get(Calendar.DAY_OF_MONTH)
                && lastCalendar.get(Calendar.MONTH) == curCalendar.get(Calendar.MONTH)
                && lastCalendar.get(Calendar.YEAR) == curCalendar.get(Calendar.YEAR)) {
            LogUtils.e(TAG, "--> hasPromptUpgradeToday()  has been prompted upgrade today");
            return true;
        }
        return false;
    }

    /**
     * 获取启动页时长上限（S）
     */
    public int getMaxLaunchTime() {

        if (UserService.getService().getUser() == User.ORGANIC) {
            return 3; // 迭代 1.0.5：自然用户：只有connect，原生广告，banner, loading写死3s；
        }

//        ConfigBean configBean = getConfigBean();
//
//        if (configBean == null || configBean.getLaunchTime() == 0) {
//            return BaseConstants.LOCAL_MAX_LAUNCH_TIME;
//        }
//
//        int launchTime = configBean.getLaunchTime();
//        LogUtils.e(TAG, "--> getMaxLaunchTime() launchTime(s)=" + launchTime);
        return AppConfig.MAX_LAUNCH_TIME;
    }


//    private static class ConfigHandler extends Handler {
//
//        ConfigHandler() {
//            super(Looper.getMainLooper());
//        }
//
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            if (msg.what == WHAT_PULL_CONFIG) {
//                LogUtils.e(TAG, "--> WHAT_PULL_CONFIG");
//                ConfigManager.INSTANCE.handlePullConfig();
//            }
//        }
//    }

    /**
     * 归类失败是否为自然用户（新增）
     * 打开则为自然用户，关闭为买量用户，默认打开
     */
    public static boolean isOrganicUser() {
//        ConfigBean configBean = ConfigManager.INSTANCE.getConfigBean();
//        if (configBean == null) {
//            return true;
//        }
//        return configBean.isOrganicUser();
        return true; // TODO 默认为自然用户
    }

//    private static class ConfigCache {
//
//        private ConfigBean configBean;
//
//        public @Nullable ConfigBean getConfigBean() {
//            if (configBean == null) {
//                loadLocalConfig();
//            }
//            return configBean;
//        }
//
//        // cache profile to SP
//        private void storeConfig(ConfigBean netConfig) {
//            configBean = netConfig;
//            String configJson = JsonUtils.toJson(netConfig);
//            if (TextUtils.isEmpty(configJson)) {
//                return;
//            }
//            configJson = SafeNative.encrypt(configJson);
//            SPUtils.getInstance().put(BaseConstants.SP_CONFIG_CACHE, configJson);
//        }
//
//        // get cache from sprefs or native
//        private void loadLocalConfig() {
//
//            ConfigBean localConfig = null;
//
//            String strLocalConfig = SPUtils.getInstance().getString(BaseConstants.SP_CONFIG_CACHE, "");
//
//            if (!TextUtils.isEmpty(strLocalConfig)) { // firstly, get from sprefs
//                strLocalConfig = SafeNative.decrypt(strLocalConfig);
//                LogUtils.e("从网络缓存中获取<全局配置> ## " + strLocalConfig);
//                localConfig = JsonUtils.fromJson(strLocalConfig, ConfigBean.class);
//            }
//
//            if (localConfig == null) {  // if no cache in sprefs, then get from native
//                strLocalConfig = SafeNative.nGetConfig();
//                LogUtils.e("从本地缓存中获取<全局配置> ## " + strLocalConfig);
//                localConfig = JsonUtils.fromJson(strLocalConfig, ConfigBean.class);
//            }
//
//            configBean = localConfig;
//        }
//    }
}
