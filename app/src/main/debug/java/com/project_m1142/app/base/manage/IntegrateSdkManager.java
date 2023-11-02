package com.project_m1142.app.base.manage;

import android.app.Application;
import android.text.TextUtils;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAdRevenue;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.LogLevel;
import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
//import com.applovin.sdk.AppLovinSdk;
//import com.applovin.sdk.AppLovinSdkConfiguration;
import com.blankj.utilcode.util.SPUtils;
import com.project_m1142.app.base.config.AppConfig;
import com.project_m1142.app.base.user.UserService;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
//import com.google.android.gms.ads.initialization.AdapterStatus;
//import com.google.firebase.analytics.FirebaseAnalytics;
//import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.project_m1142.app.base.constants.SPConstants;
import com.project_m1142.app.base.utils.LogUtils;

public enum IntegrateSdkManager {

    INSTANCE;

    private static final String TAG = "IntegrateSdkManager";

    private static String gid; // google 广告 id

    public void init(Application application) {
        initAdjust(application);
        setupFirebase(application);
        initFacebook(application);
        initMobilAds(application);
        initApplovin(application);
        setupReferrer(application);
    }

    private void initMobilAds(Application application) {

//        // if (BuildConfig.GAME_DEBUG) {
//        //     // 添写测试机的 DeviceId
//        //     // 参考：https://developers.google.cn/admob/android/test-ads#add_your_test_device_programmatically
//        //       List<String> testDeviceIds = Arrays.asList("2B290798B3C52E15FF6CACDAAA57C910", "F9180FB4550B86775EB93370CFB3BCA8");
//        //       RequestConfiguration configuration =
//        //               new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
//        //       MobileAds.setRequestConfiguration(configuration);
//        // }
//
//        MobileAds.initialize(application, initializationStatus -> {
//            for (Map.Entry<String, AdapterStatus> entry : initializationStatus.getAdapterStatusMap().entrySet()) {
//                LogUtils.e(TAG, "--> initMobilAds() onInitializationComplete  entry.key=" + entry.getKey()
//                        + "  entry.AdapterStatus.desc=" + entry.getValue().getDescription()
//                        + "  entry.AdapterStatus.state=" + entry.getValue().getInitializationState());
//            }
//        });
    }

    private void initApplovin(Application application) {

//        AppLovinSdk.getInstance(application).getSettings().setVerboseLogging(AppConfig.HEALTH_DEBUG);
//
//        // Make sure to set the mediation provider value to "max" to ensure proper functionality
//        AppLovinSdk.getInstance(application).setMediationProvider("max");
//        AppLovinSdk.initializeSdk(application, new AppLovinSdk.SdkInitializationListener() {
//            @Override
//            public void onSdkInitialized(final AppLovinSdkConfiguration configuration) {
//                // AppLovin SDK is initialized, start loading ads
//                LogUtils.e(TAG, "--> initApplovin()  onSdkInitialized  configuration=" + configuration);
//            }
//        } );
    }

    private void setupFirebase(Application application) { // TODO 去掉 Firebase
//        // Analytics
//        FirebaseAnalytics.getInstance(application).setAnalyticsCollectionEnabled(true);
//        // Crashlytics: release 传 true，debug 传 false
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(AppConfig.ENABLE_CRASH);
    }

    private void initFacebook(Application application) { // TODO 去掉 Facebook
//        String id = AppConfig.FACEBOOK_ID;
//        String token = AppConfig.FACEBOOK_TOKEN;
//        LogUtils.e(TAG, "--> deployFacebook()  id=" + id + "  token=" + token);
//        FacebookSdk.setApplicationId(id); // app id（跟 AndroidManifest.xml 中配置的一样）
//        FacebookSdk.setClientToken(token); // token
//        FacebookSdk.sdkInitialize(application);
//        AppEventsLogger.activateApp(application);
//        FacebookSdk.setAutoLogAppEventsEnabled(true);
    }

    private void initAdjust(Application application) {
        String token = AppConfig.ADJUST_TOKEN;
        LogUtils.e(TAG, "--> deployAdjust()  token=" + token);
        /*
        Debug 时 environment 设置为 AdjustConfig.ENVIRONMENT_SANDBOX 。
        Release 时 environment 设置为 AdjustConfig.ENVIRONMENT_PRODUCTION。
         */
        String env = AppConfig.VPN_DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(application, token, env);
        config.setLogLevel(AppConfig.VPN_DEBUG ? LogLevel.WARN : LogLevel.SUPRESS);
        Adjust.onCreate(config);
    }

    private void setupReferrer(Application application) {
        InstallReferrerClient referrerClient;
        referrerClient = InstallReferrerClient.newBuilder(application).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                LogUtils.e(TAG, "--> onInstallReferrerSetupFinished()  responseCode=" + responseCode);
                String referrerUrl = null;
                try {
                    switch (responseCode) {
                        case InstallReferrerClient.InstallReferrerResponse.OK:
                            // Connection established.
                            referrerUrl = referrerClient.getInstallReferrer().getInstallReferrer();
                            LogUtils.e(TAG, "--> onInstallReferrerSetupFinished()  referrerUrl=" + referrerUrl);
                            boolean traced = SPUtils.getInstance().getBoolean(SPConstants.SP_REFERRER_TRACE);
                            if (!traced) {
                                AdjustEvent event = new AdjustEvent("shvi81");
                                event.addCallbackParameter("One-Clickurl", referrerUrl); // 加密数据参数：One-Clickurl
                                Adjust.trackEvent(event);
                                //注意要做一个上传标记，避免重复上传，文档也说了尽量避免不必要的重复调用
                                SPUtils.getInstance().put(SPConstants.SP_REFERRER_TRACE, true);
                            }
                            //获取归因后要及时断开，避免内存泄露
                            referrerClient.endConnection();
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                            // API not available on the current Play Store app.
                            break;
                        case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                            // Connection couldn't be established.
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                UserService.getService().parseUser(referrerUrl);
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    public static void reportRevenue(long valueMicros, String currencyCode, String network, String adId, String adPlace) {
        double revenue = valueMicros / 1000000.0; //把原来的千分值转换成0.001
        AdjustAdRevenue adRevenue = new AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB);
        adRevenue.setRevenue(revenue, currencyCode);
        adRevenue.setAdRevenueNetwork(network); //广告源渠道
        adRevenue.setAdRevenuePlacement(adPlace); //广告位名称
        adRevenue.setAdRevenueUnit(adId); //广告ID
        Adjust.trackAdRevenue(adRevenue); //调用Adjust上报广告价值的方法
    }

    public static void loadGid(Application application) {
        try {
            AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(application);
            gid = adInfo.getId();
            LogUtils.e(TAG, "loadGid() -->  googleId=" + gid);
        } catch (Exception e) {
            LogUtils.e(TAG, "loadGid() -->  Exception=" + e);
        }
    }

    public static String getGid() {
        return TextUtils.isEmpty(gid) ? "" : gid;
    }
}
