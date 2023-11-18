package com.project_ci01.app.base.advert;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.base.bean.gson.PlaceBean;
import com.project_ci01.app.base.bean.gson.UnitBean;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.manage.EventTracker;
//import com.google.android.gms.ads.AdError;
//import com.google.android.gms.ads.AdLoader;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdSize;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.FullScreenContentCallback;
//import com.google.android.gms.ads.LoadAdError;
//import com.google.android.gms.ads.VideoOptions;
//import com.google.android.gms.ads.appopen.AppOpenAd;
//import com.google.android.gms.ads.interstitial.InterstitialAd;
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
//import com.google.android.gms.ads.nativead.NativeAd;
//import com.google.android.gms.ads.nativead.NativeAdOptions;
//import com.google.android.gms.ads.nativead.NativeAdView;
//import com.google.android.gms.ads.rewarded.RewardItem;
import com.project_ci01.app.base.utils.LogUtils;

public class Resource<T> {

    protected static String TAG = "AdResource";

    protected final Handler handler = new Handler(Looper.getMainLooper());

    protected final ResourceListener listener;
    protected final PlaceBean placeBean;
    protected final UnitBean unitBean;
    protected Status status;
    protected T ad;
    protected long timeLoaded; // timestamp when fetch successful

//    protected RewardItem rewardItem;

    public Resource(@NonNull PlaceBean placeBean, @NonNull UnitBean unitBean, ResourceListener listener) {
        status =  Status.PREPARE;
        this.placeBean = placeBean;
        this.unitBean = unitBean;
        this.listener = listener;
    }

    public Status getStatus() {
        return status;
    }

    public PlaceBean getPlaceBean() {
        return placeBean;
    }

    public UnitBean getUnitBean() {
        return unitBean;
    }

//    public @Nullable RewardItem getRewardItem() {
//        return rewardItem;
//    }

    public void pull() {
        if (isNotReady()) {
            return;
        }

        final UnitType unitType = UnitType.convert(unitBean.getType());
        if (unitType == null) {
            Log.e(TAG, "-->  pull()  faild !!! because of a null UnitType");
            return;
        }

        if (listener != null && unitType != UnitType.BAN) {
            listener.onPrePull(this);
        }

        EventTracker.traceAdPreload(unitBean.getId(), placeBean.getPlace());

        handler.post(() -> {
            switch (unitType) {
                case START:
                    status =  Status.PULLING;
                    pullOpenAd();
                    break;
                case INT:
                    status =  Status.PULLING;
                    pullIntAd();
                    break;
                case NAV:
                    status =  Status.PULLING;
                    pullNavAd();
                    break;
//                case BAN:
//                    /* ban don't need load */
//                    break;
            }
        });
    }

    public boolean show(Activity activity, @Nullable ViewGroup container) {
        return show(activity, container, null);
    }

    public boolean show(Activity activity, @Nullable ViewGroup container, NativeAdViewCreator creator) {
        if (!ContextManager.isSurvival(activity)) {
            return false;
        }

        final UnitType unitType = UnitType.convert(unitBean.getType());
        if (unitType == null) {
            Log.e(TAG, "-->  show()  faild !!! because of a null UnitType");
            return false;
        }

        /* 注意：Banner 广告不走缓存，不用判断 isDirty 和 isExpired */

        if (unitType != UnitType.BAN && isDirty()) {
            return false;
        }

        if (unitType != UnitType.BAN && isExpired()) {
            status =  Status.EXPIRED;
            if (listener != null) {
                listener.onExpired(this);
            }
            return false;
        }

        boolean result = false;
        switch (unitType) {
            case START:
                result = showOpenAd(activity);
                break;
            case INT:
                result = showIntAd(activity);
                break;
            case NAV:
                result = creator != null && showNavAd(activity, container, creator);
                break;
            case BAN:
                result = showBanAd(activity, container);
                break;
        }
        return result;
    }

    public void resume() {
//        final UnitType unitType = UnitType.convert(unitBean.getType());
//        if (unitType == null) {
//            Log.e(TAG, "-->  resume()  faild !!! because of a null UnitType");
//            return;
//        }
//
//        if (ad != null && unitType == UnitType.BAN) {
//            ((AdView) ad).resume();
//        }
    }

    public void pause() {
//        final UnitType unitType = UnitType.convert(unitBean.getType());
//        if (unitType == null) {
//            Log.e(TAG, "-->  pause()  faild !!! because of a null UnitType");
//            return;
//        }
//
//        if (ad != null && unitType == UnitType.BAN) {
//            ((AdView) ad).pause();
//        }
    }

    public void destroy() {
//        final UnitType unitType = UnitType.convert(unitBean.getType());
//        if (unitType == null) {
//            Log.e(TAG, "-->  destroy()  faild !!! because of a null UnitType");
//            return;
//        }
//
//        if (ad != null) {
//            try {
//                switch (unitType) {
//                    case NAV:
//                        ((NativeAd) ad).destroy();
//                        break;
//                    case BAN:
//                        /* ban don't need load */
//                        ((AdView) ad).destroy();
//                        break;
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "-->  destroy()  faild !!! Exception=" + e);
//            }
//        }
//
//        ad = null;
//        status =  Status.RELEASE;
    }

    @Override
    public String toString() {
        return "AdResource{" +
                "placeBean=" + placeBean +
                ", unitBean=" + unitBean +
                ", status=" + status +
                '}';
    }

    public boolean isExpired() {
        long duration = SystemClock.elapsedRealtime() - timeLoaded;
        LogUtils.e(TAG, "--> isExpired()  duration=" + duration);
        return duration > ConvertUtils.timeSpan2Millis(50, TimeConstants.MIN); // 50min 内的广告才有效
    }

    protected boolean isNotReady() {
        LogUtils.e(TAG, "--> isNotReady()  Place=" + placeBean.getPlace() + "  unitBean=" + unitBean + "  status=" + status);
        return TextUtils.isEmpty(unitBean.getId()) || status != Status.PREPARE;
    }

    protected boolean isDirty() {
        LogUtils.e(TAG, "--> isDirty()  ad=" + ad + "  status=" + status);
        return ad == null || status != Status.PULL_SUCCESS;
    }

    protected void pullOpenAd() {
//        AdRequest request = new AdRequest.Builder().build();
//        AppOpenAd.load(LifecyclerManager.INSTANCE.getApplication(), unitBean.getId(), request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
//                new AppOpenAd.AppOpenAdLoadCallback() {
//
//                    @Override
//                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
//                        LogUtils.e(TAG, "--> onAdLoaded() appOpenAd=" + appOpenAd);
//                        status = Status.PULL_SUCCESS;
//                        timeLoaded = SystemClock.elapsedRealtime();
//                        String adapterName = appOpenAd.getResponseInfo().getMediationAdapterClassName();
//                        appOpenAd.setOnPaidEventListener(new MyOnPaidEventListener(placeBean, unitBean, adapterName));
//                        EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//                        ad = (T) appOpenAd;
//                        if (listener != null) {
//                            listener.onPullSuccess(Resource.this);
//                        }
//                    }
//
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        LogUtils.e(TAG, "--> onAdFailedToLoad() loadAdError=" + loadAdError);
//
//                        status = Status.PULL_FAILED;
//
//                        String domain = loadAdError.getDomain();
//                        int code = loadAdError.getCode();
//                        String message = loadAdError.getMessage();
//                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);
//
//                        if (listener != null) {
//                            listener.onPullFailed(Resource.this, code, errorMsg);
//                        }
//                    }
//                });
    }

    protected void pullIntAd() {
//        AdRequest request = new AdRequest.Builder().build();
//        InterstitialAd.load(LifecyclerManager.INSTANCE.getApplication(), unitBean.getId(), request,
//                new InterstitialAdLoadCallback() {
//                    @Override
//                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        LogUtils.e(TAG, "--> onAdLoaded()  interstitialAd=" + interstitialAd);
//                        status = Status.PULL_SUCCESS;
//                        timeLoaded = SystemClock.elapsedRealtime();
//                        String adapterName = interstitialAd.getResponseInfo().getMediationAdapterClassName();
//                        interstitialAd.setOnPaidEventListener(new MyOnPaidEventListener(placeBean, unitBean, adapterName));
//                        EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//                        ad = (T) interstitialAd;
//                        if (listener != null) {
//                            listener.onPullSuccess(Resource.this);
//                        }
//                    }
//
//                    @Override
//                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        LogUtils.e(TAG, "--> onAdFailedToLoad() loadAdError : " + loadAdError);
//
//                        status = Status.PULL_FAILED;
//
//                        String domain = loadAdError.getDomain();
//                        int code = loadAdError.getCode();
//                        String message = loadAdError.getMessage();
//                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);
//
//                        if (listener != null) {
//                            listener.onPullFailed(Resource.this, code, errorMsg);
//                        }
//                    }
//                });
    }


    protected void pullNavAd() {
//        AdLoader.Builder adLoadBuilder = new AdLoader.Builder(LifecyclerManager.INSTANCE.getApplication(), unitBean.getId());
//
//        adLoadBuilder.forNativeAd(nativeAd -> { // onNativeAdLoaded
//            LogUtils.e(TAG, "--> onNativeAdLoaded()  nativeAd=" + nativeAd);
//            status = Status.PULL_SUCCESS;
//            timeLoaded = SystemClock.elapsedRealtime();
//            String adapterName = nativeAd.getResponseInfo() == null ? "" : nativeAd.getResponseInfo().getMediationAdapterClassName();
//            nativeAd.setOnPaidEventListener(new MyOnPaidEventListener(placeBean, unitBean, adapterName));
//            EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//            ad = (T) nativeAd;
//            if (listener != null) {
//                listener.onPullSuccess(Resource.this);
//            }
//        });
//
//        VideoOptions videoOptions =
//                new VideoOptions.Builder().setStartMuted(true).build(); // 默认静音
//
//        NativeAdOptions adOptions =
//                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();
//
//        adLoadBuilder.withNativeAdOptions(adOptions);
//        AdLoader adLoader = adLoadBuilder.withAdListener(new com.google.android.gms.ads.AdListener() {
//
//            @Override
//            public void onAdClicked() { // 记录了广告获得的点击后，系统会调用 onAdClicked() 方法。
//                LogUtils.e(TAG, "--> onAdClicked()");
////                BillService.getInstance().calcNavClickTimes();
//                String adapterName = ((NativeAd) ad).getResponseInfo() == null ? "" : ((NativeAd) ad).getResponseInfo().getMediationAdapterClassName();
//                EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//                if (listener != null) {
//                    listener.onClick(Resource.this);
//                }
//            }
//
//            @Override
//            public void onAdClosed() { // 用户在查看广告的目标网址后返回应用时，系统会调用 onAdClosed() 方法。应用可以使用此方法恢复暂停的活动，或执行任何其他必要的操作，以做好互动准备。
//                LogUtils.e(TAG, "--> onAdClosed()");
//                status = Status.DISMISS;
//                if (listener != null) {
//                    listener.onDismiss(Resource.this);
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // onAdFailedToLoad() 是唯一包含参数的方法。LoadAdError 类型的错误参数描述了发生的错误。
//                LogUtils.e(TAG, "--> onAdFailedToLoad() loadAdError=" + loadAdError);
//
//                status = Status.PULL_FAILED;
//
//                String domain = loadAdError.getDomain();
//                int code = loadAdError.getCode();
//                String message = loadAdError.getMessage();
//                String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);
//
//                if (listener != null) {
//                    listener.onPullFailed(Resource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdImpression() { // 记录了广告获得的展示后，系统会调用 onAdImpression() 方法。
//                LogUtils.e(TAG, "--> onAdImpression()");
//                status = Status.SHOW;
//                if (listener != null) {
//                    listener.onShow(Resource.this);
//                }
//            }
//
//            public void onAdLoaded() { // 广告加载完成后，系统会执行 onAdLoaded() 方法。例如，如果您想将为 Activity 或 Fragment 添加 AdView 的操作推迟到您确定广告会加载时再执行，就可以通过此方法做到。
//                LogUtils.e(TAG, "--> onAdLoaded()");
//                // 对原生广告，触发 OnNativeAdLoadedListener.onNativeAdLoaded(NativeAd nativeAd)，不会回调 onAdLoaded()
//            }
//
//            public void onAdOpened() { // 广告打开覆盖屏幕的叠加层时，系统会调用 onAdOpened() 方法。
//                LogUtils.e(TAG, "--> onAdOpened()");
//            }
//
//        }).build();
//
//        adLoader.loadAd(new AdRequest.Builder().build());
    }

    protected boolean showOpenAd(Activity activity) {
//        ((AppOpenAd) ad).setFullScreenContentCallback(
//                new FullScreenContentCallback() {
//                    /** Called when full screen content is dismissed. */
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
//
//                        LogUtils.e(TAG, "--> onAdDismissedFullScreenContent()");
//
//                        status = Status.DISMISS;
//                        if (listener != null) {
//                            listener.onDismiss(Resource.this);
//                        }
//                    }
//
//                    /** Called when fullscreen content failed to show. */
//                    @Override
//                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//
//                        LogUtils.e(TAG, "--> onAdFailedToShowFullScreenContent() adError : " + adError);
//
//                        status = Status.UNSHOW;
//
//                        String domain = adError.getDomain();
//                        int code = adError.getCode();
//                        String message = adError.getMessage();
//                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);
//
//                        if (listener != null) {
//                            listener.onUnshow(Resource.this, code, errorMsg);
//                        }
//                    }
//
//                    /** Called when fullscreen content is shown. */
//                    @Override
//                    public void onAdShowedFullScreenContent() {
//                        LogUtils.e(TAG, "--> onAdShowedFullScreenContent()");
//
//                        status = Status.SHOW;
//                        if (listener != null) {
//                            listener.onShow(Resource.this);
//                        }
//                    }
//
//                    @Override
//                    public void onAdClicked() {
//                        LogUtils.e(TAG, "--> onAdClicked()");
//
//                        String adapterName = ((AppOpenAd) ad).getResponseInfo().getMediationAdapterClassName();
//                        EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//
//                        if (listener != null) {
//                            listener.onClick(Resource.this);
//                        }
//                    }
//                });
//
//        ((AppOpenAd) ad).show(activity);
//        status = Status.SHOW;
//        return true;
        return false;
    }

    protected boolean showIntAd(Activity activity) {
//        ((InterstitialAd) ad).setFullScreenContentCallback(
//                new FullScreenContentCallback() {
//                    /** Called when full screen content is dismissed. */
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
//
//                        LogUtils.e(TAG, "--> onAdDismissedFullScreenContent()");
//
//                        status = Status.DISMISS;
//                        if (listener != null) {
//                            listener.onDismiss(Resource.this);
//                        }
//                    }
//
//                    /** Called when fullscreen content failed to show. */
//                    @Override
//                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
//
//                        LogUtils.e(TAG, "--> onAdFailedToShowFullScreenContent() adError=" + adError);
//
//                        status = Status.UNSHOW;
//
//                        String domain = adError.getDomain();
//                        int code = adError.getCode();
//                        String message = adError.getMessage();
//                        String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);
//
//                        if (listener != null) {
//                            listener.onUnshow(Resource.this, code, errorMsg);
//                        }
//                    }
//
//                    /** Called when fullscreen content is shown. */
//                    @Override
//                    public void onAdShowedFullScreenContent() {
//                        LogUtils.e(TAG, "--> onAdShowedFullScreenContent()");
//
//                        status = Status.SHOW;
//                        if (listener != null) {
//                            listener.onShow(Resource.this);
//                        }
//                    }
//
//                    @Override
//                    public void onAdClicked() {
//                        LogUtils.e(TAG, "--> onAdClicked()");
//
//                        String adapterName = ((InterstitialAd) ad).getResponseInfo().getMediationAdapterClassName();
//                        EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//
//                        if (listener != null) {
//                            listener.onClick(Resource.this);
//                        }
//                    }
//                });
//
//        ((InterstitialAd) ad).show(activity);
//        status = Status.SHOW;
//        return true;
        return false;
    }

    protected boolean showNavAd(Activity activity, @Nullable ViewGroup container, @NonNull NativeAdViewCreator creator) {
//        if (container == null) {
//            return false;
//        }
//
//        NativeAdView adView = creator.create(Provider.ADMOB, (NativeAd) ad, activity);
//        container.removeAllViews();
//        container.addView(adView);
//        status = Status.SHOW;
//        return true;
        return false;
    }

    protected boolean showBanAd(Activity activity, @Nullable ViewGroup container) {
//        if (container == null) {
//            return false;
//        }
//
//        if (isNotReady()) {
//            return false;
//        }
//
//        if (listener != null) {
//            listener.onPrePull(this);
//        }
//
//        EventTracker.traceAdPreload(unitBean.getId(), placeBean.getPlace());
//
//        final AdView adView = new AdView(activity);
//
//        adView.setAdUnitId(unitBean.getId());
//
//        adView.setAdListener(new com.google.android.gms.ads.AdListener() {
//
//            @Override
//            public void onAdClicked() { // 记录了广告获得的点击后，系统会调用 onAdClicked() 方法。
//                LogUtils.e(TAG, "--> onAdClicked()");
//
//                String adapterName = adView.getResponseInfo() == null ? "" : adView.getResponseInfo().getMediationAdapterClassName();
//                EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//
//                if (listener != null) {
//                    listener.onClick(Resource.this);
//                }
//            }
//
//            @Override
//            public void onAdClosed() { // 用户在查看广告的目标网址后返回应用时，系统会调用 onAdClosed() 方法。应用可以使用此方法恢复暂停的活动，或执行任何其他必要的操作，以做好互动准备。
//                LogUtils.e(TAG, "--> onAdClosed()");
//                status = Status.DISMISS;
//                if (listener != null) {
//                    listener.onDismiss(Resource.this);
//                }
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) { // onAdFailedToLoad() 是唯一包含参数的方法。LoadAdError 类型的错误参数描述了发生的错误。
//                LogUtils.e(TAG, "--> onAdFailedToLoad() loadAdError=" + loadAdError);
//
//                status = Status.PULL_FAILED;
//
//                String domain = loadAdError.getDomain();
//                int code = loadAdError.getCode();
//                String message = loadAdError.getMessage();
//                String errorMsg = String.format(Locale.getDefault(),"domain: %s, code: %d, message: %s", domain, code, message);
//
//                if (listener != null) {
//                    listener.onPullFailed(Resource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdImpression() { // 记录了广告获得的展示后，系统会调用 onAdImpression() 方法。
//                LogUtils.e(TAG, "--> onAdImpression()");
//                status = Status.SHOW;
//                if (listener != null) {
//                    listener.onShow(Resource.this);
//                }
//            }
//
//            public void onAdLoaded() { // 广告加载完成后，系统会执行 onAdLoaded() 方法。例如，如果您想将为 Activity 或 Fragment 添加 AdView 的操作推迟到您确定广告会加载时再执行，就可以通过此方法做到。
//                LogUtils.e(TAG, "--> onAdLoaded()");
//
//                status = Status.PULL_SUCCESS;
//                timeLoaded = SystemClock.elapsedRealtime();
//
//                String adapterName = adView.getResponseInfo() == null ? "" : adView.getResponseInfo().getMediationAdapterClassName();
//                adView.setOnPaidEventListener(new MyOnPaidEventListener(placeBean, unitBean, adapterName));
//                EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), AdConfig.getAdChannel(adapterName));
//
//                if (listener != null) {
//                    listener.onPullSuccess(Resource.this);
//                }
//            }
//
//            public void onAdOpened() { // 广告打开覆盖屏幕的叠加层时，系统会调用 onAdOpened() 方法。
//                LogUtils.e(TAG, "--> onAdOpened()");
//            }
//
//        });
//
//        container.removeAllViews();
//        container.addView(adView);
//
//        int adWidth = ConvertUtils.px2dp(container.getLayoutParams().width);
//        if (adWidth <= 0) {
//            adWidth = 339; // dp
//        }
//        AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
//        LogUtils.e(TAG, "--> showBan() adWidth=" + adWidth + " adSize=" + adSize);
//        adView.setAdSize(adSize); // 自适应 banner
//
//        status = Status.PULLING;
//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);
//
//        ad = (T) adView;
//        return true;
        return false;
    }
}
