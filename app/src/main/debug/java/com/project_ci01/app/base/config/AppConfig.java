package com.project_ci01.app.base.config;

import com.project_ci01.app.BuildConfig;

public final class AppConfig {

    public static final int MAX_LAUNCH_TIME = 10; // 启动页最大时长（s）

    public static final int MIN_LAUNCH_TIME = 3; // 启动页最小时长（s）

    public static final boolean VPN_DEBUG = BuildConfig.VPN_DEBUG;
    public static final boolean VPN_LIMIT = BuildConfig.VPN_LIMIT;
    public static final boolean ENABLE_LOG = BuildConfig.ENABLE_LOG;
    public static final boolean ENABLE_CRASH = BuildConfig.ENABLE_CRASH;
    public static final String URL_PRIVACY = BuildConfig.URL_PRIVACY;
    public static final String URL_TERMS = BuildConfig.URL_TERMS;
    public static final String FEEDBACK_EMAIL = BuildConfig.FEEDBACK_EMAIL;
    public static final String URL_OFFICIAL = BuildConfig.URL_OFFICIAL;
//    public static final String BASE_URL = BuildConfig.BASE_URL;
//    public static final String PATH_CONFIG = BuildConfig.PATH_CONFIG;
    public static final String PATH_LOCATION_1 = BuildConfig.PATH_LOCATION_1;
    public static final String PATH_LOCATION_2 = BuildConfig.PATH_LOCATION_2;
    public static final String PATH_LOCATION_3 = BuildConfig.PATH_LOCATION_3;
    public static final String PATH_LOCATION_4 = BuildConfig.PATH_LOCATION_4;

    // TODO 去掉 Facebook
//    public static final String FACEBOOK_ID = BuildConfig.FACEBOOK_ID;
//    public static final String FACEBOOK_TOKEN = BuildConfig.FACEBOOK_TOKEN;
    public static final String ADJUST_TOKEN = BuildConfig.ADJUST_TOKEN;

//    public static final String LOCAL_CONFIG = BuildConfig.LOCAL_CONFIG; // TODO 去掉本地全局配置
//    public static final String VPN_LIST = BuildConfig.VPN_LIST;
}
