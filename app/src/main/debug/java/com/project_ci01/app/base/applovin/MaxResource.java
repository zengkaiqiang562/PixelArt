package com.project_ci01.app.base.applovin;

import android.app.Activity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.applovin.mediation.MaxAd;
//import com.applovin.mediation.MaxAdListener;
//import com.applovin.mediation.MaxAdRequestListener;
//import com.applovin.mediation.MaxAdRevenueListener;
//import com.applovin.mediation.MaxAdViewAdListener;
//import com.applovin.mediation.MaxError;
//import com.applovin.mediation.ads.MaxAdView;
//import com.applovin.mediation.ads.MaxAppOpenAd;
//import com.applovin.mediation.ads.MaxInterstitialAd;
//import com.applovin.mediation.nativeAds.MaxNativeAdListener;
//import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
//import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.project_ci01.app.base.advert.NativeAdViewCreator;
import com.project_ci01.app.base.advert.Resource;
import com.project_ci01.app.base.advert.ResourceListener;
import com.project_ci01.app.base.bean.gson.PlaceBean;
import com.project_ci01.app.base.bean.gson.UnitBean;

public class MaxResource<T> extends Resource<T> {

//    private MaxNativeAdLoader maxNativeAdLoader; // maybe null, use for unitType = NAV

    public MaxResource(@NonNull PlaceBean placeBean, @NonNull UnitBean unitBean, ResourceListener listener) {
        super(placeBean, unitBean, listener);
        TAG = "MaxResource";
    }

    @Override
    public void resume() {
        /* Max don't need to do anything */
    }

    @Override
    public void pause() {
        /* Max don't need to do anything */
    }

    @Override
    public void destroy() {
//        final UnitType unitType = UnitType.convert(unitBean.getType());
//        if (unitType == null) {
//            Log.e(TAG, "-->  destroy()  faild !!! because of a null UnitType");
//            return;
//        }
//
//        try {
//            if (ad != null) {
//                switch (unitType) {
//                    case NAV:
//                        maxNativeAdLoader.destroy((MaxAd) ad);
//                        maxNativeAdLoader.destroy();
//                        break;
//                    case BAN:
//                        ((MaxAdView) ad).stopAutoRefresh();
//                        ((MaxAdView) ad).destroy();
//                        break;
//                }
//            } else {
//                switch (unitType) {
//                    case NAV:
//                        maxNativeAdLoader.destroy();
//                        break;
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "-->  destroy()  faild !!! Exception=" + e);
//        }
//
//        ad = null;
//        status =  Status.RELEASE;
    }

    @Override
    protected void pullOpenAd() {
//        /*
//        appOpenAd = new MaxAppOpenAd( "YOUR_AD_UNIT_ID", context);
//       appOpenAd.setListener( this );
//       appOpenAd.loadAd();
//         */
//        MaxAppOpenAd maxAppOpenAd = new MaxAppOpenAd(unitBean.getId(), LifecyclerManager.INSTANCE.getApplication());
//
//        maxAppOpenAd.setRequestListener(new MaxAdRequestListener() {
//            @Override
//            public void onAdRequestStarted(String adUnitId) {
//                LogUtils.e(TAG, "--> onAdRequestStarted()  MaxAppOpenAd adUnitId=" + adUnitId);
//            }
//        });
//
//        maxAppOpenAd.setListener(new MaxAdListener() {
//            @Override
//            public void onAdLoaded(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdLoaded() MaxAppOpenAd maxAd=" + maxAd);
//                status = Status.PULL_SUCCESS;
//                timeLoaded = SystemClock.elapsedRealtime();
//                EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//                ad = (T) maxAppOpenAd;
//                if (listener != null) {
//                    listener.onPullSuccess(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdLoadFailed(String s, MaxError maxError) {
//                LogUtils.e(TAG, "--> onAdLoadFailed() MaxAppOpenAd maxError=" + maxError);
//
//                status = Status.PULL_FAILED;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onPullFailed(MaxResource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdHidden(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdHidden() MaxAppOpenAd  maxAd=" + maxAd);
//
//                status = Status.DISMISS;
//                if (listener != null) {
//                    listener.onDismiss(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//                LogUtils.e(TAG, "--> onAdDisplayFailed() MaxAppOpenAd maxAd=" + maxAd + "  maxError=" + maxError);
//
//                status = Status.UNSHOW;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onUnshow(MaxResource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdDisplayed() MaxAppOpenAd  maxAd=" + maxAd);
//
//                status = Status.SHOW;
//                if (listener != null) {
//                    listener.onShow(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdClicked(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdClicked() MaxAppOpenAd  maxAd=" + maxAd);
//
//                EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//
//                if (listener != null) {
//                    listener.onClick(MaxResource.this);
//                }
//            }
//        });
//
//        maxAppOpenAd.setRevenueListener(new MaxAdRevenueListener() {
//            @Override
//            public void onAdRevenuePaid(MaxAd maxAd) {
//                LogUtils.e(TAG, "-->  onAdRevenuePaid() MaxAppOpenAd  maxAd=" + maxAd);
//                // 上报广告价值
//                AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue( AdjustConfig.AD_REVENUE_APPLOVIN_MAX );
//                adjustAdRevenue.setRevenue(maxAd.getRevenue(), "USD");
//                adjustAdRevenue.setAdRevenueNetwork(maxAd.getNetworkName());
//                adjustAdRevenue.setAdRevenueUnit(maxAd.getAdUnitId() );
//                adjustAdRevenue.setAdRevenuePlacement(maxAd.getPlacement());
//                Adjust.trackAdRevenue(adjustAdRevenue);
//            }
//        });
//
//        maxAppOpenAd.loadAd();
    }

    @Override
    protected void pullIntAd() {
//        /*
//        interstitialAd = new MaxInterstitialAd( "YOUR_AD_UNIT_ID", this );
//        interstitialAd.setListener( this );
//
//        // Load the first ad
//        interstitialAd.loadAd();
//         */
//        Activity topActivity = ContextManager.INSTANCE.topActivity();
//        if (!ContextManager.isSurvival(topActivity)) {
//            status = Status.PULL_FAILED;
//            if (listener != null) {
//                listener.onPullFailed(MaxResource.this, -1, "There is no Activity in Stack");
//            }
//            return;
//        }
//        MaxInterstitialAd maxInterstitialAd = new MaxInterstitialAd(unitBean.getId(), topActivity);
//
//        maxInterstitialAd.setRequestListener(new MaxAdRequestListener() {
//            @Override
//            public void onAdRequestStarted(String adUnitId) {
//                LogUtils.e(TAG, "-->  onAdRequestStarted() MaxInterstitialAd adUnitId=" + adUnitId);
//            }
//        });
//
//        maxInterstitialAd.setListener(new MaxAdListener() {
//            @Override
//            public void onAdLoaded(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdLoaded() MaxAppOpenAd maxAd=" + maxAd);
//                status = Status.PULL_SUCCESS;
//                timeLoaded = SystemClock.elapsedRealtime();
//                EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//                ad = (T) maxInterstitialAd;
//                if (listener != null) {
//                    listener.onPullSuccess(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdLoadFailed(String s, MaxError maxError) {
//                LogUtils.e(TAG, "--> onAdLoadFailed() MaxAppOpenAd maxError=" + maxError);
//
//                status = Status.PULL_FAILED;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onPullFailed(MaxResource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdHidden(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdHidden() MaxAppOpenAd  maxAd=" + maxAd);
//
//                status = Status.DISMISS;
//                if (listener != null) {
//                    listener.onDismiss(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//                LogUtils.e(TAG, "--> onAdDisplayFailed() MaxAppOpenAd maxAd=" + maxAd + "  maxError=" + maxError);
//
//                status = Status.UNSHOW;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onUnshow(MaxResource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdDisplayed() MaxAppOpenAd  maxAd=" + maxAd);
//
//                status = Status.SHOW;
//                if (listener != null) {
//                    listener.onShow(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdClicked(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdClicked() MaxAppOpenAd  maxAd=" + maxAd);
//
//                EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//
//                if (listener != null) {
//                    listener.onClick(MaxResource.this);
//                }
//            }
//        });
//
//        maxInterstitialAd.setRevenueListener(new MaxAdRevenueListener() {
//            @Override
//            public void onAdRevenuePaid(MaxAd maxAd) {
//                LogUtils.e(TAG, "-->  onAdRevenuePaid() MaxAppOpenAd  maxAd=" + maxAd);
//                // 上报广告价值
//                AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue( AdjustConfig.AD_REVENUE_APPLOVIN_MAX );
//                adjustAdRevenue.setRevenue(maxAd.getRevenue(), "USD");
//                adjustAdRevenue.setAdRevenueNetwork(maxAd.getNetworkName());
//                adjustAdRevenue.setAdRevenueUnit(maxAd.getAdUnitId() );
//                adjustAdRevenue.setAdRevenuePlacement(maxAd.getPlacement());
//                Adjust.trackAdRevenue(adjustAdRevenue);
//            }
//        });
//
//        maxInterstitialAd.loadAd();
    }

    @Override
    protected void pullNavAd() {
//        maxNativeAdLoader = new MaxNativeAdLoader(unitBean.getId(), LifecyclerManager.INSTANCE.getApplication());
//        maxNativeAdLoader.setPlacement(placeBean.getPlace());
//
//        maxNativeAdLoader.setRevenueListener(new MaxAdRevenueListener() {
//            @Override
//            public void onAdRevenuePaid(MaxAd maxAd) {
//                LogUtils.e(TAG, "-->  onAdRevenuePaid() MaxNativeAd  maxAd=" + maxAd);
//                // 上报广告价值
//                AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue( AdjustConfig.AD_REVENUE_APPLOVIN_MAX );
//                adjustAdRevenue.setRevenue(maxAd.getRevenue(), "USD");
//                adjustAdRevenue.setAdRevenueNetwork(maxAd.getNetworkName());
//                adjustAdRevenue.setAdRevenueUnit(maxAd.getAdUnitId() );
//                adjustAdRevenue.setAdRevenuePlacement(maxAd.getPlacement());
//                Adjust.trackAdRevenue(adjustAdRevenue);
//            }
//        });
//
//        maxNativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
//            @Override
//            public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onNativeAdLoaded() MaxNativeAd nativeAd=" + maxAd);
//                status = Status.PULL_SUCCESS;
//                timeLoaded = SystemClock.elapsedRealtime();
//                EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//                ad = (T) maxAd;
//                if (listener != null) {
//                    listener.onPullSuccess(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onNativeAdLoadFailed(String s, MaxError maxError) {
//                LogUtils.e(TAG, "--> onNativeAdLoadFailed() MaxNativeAd maxError=" + maxError);
//
//                status = Status.PULL_FAILED;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onPullFailed(MaxResource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onNativeAdClicked(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdClicked()");
//                EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//                if (listener != null) {
//                    listener.onClick(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onNativeAdExpired(MaxAd maxAd) {
//                status =  Status.EXPIRED;
//                if (listener != null) {
//                    listener.onExpired(MaxResource.this);
//                }
//            }
//        });
//
//        maxNativeAdLoader.loadAd();
    }

    @Override
    protected boolean showOpenAd(Activity activity) {
//        MaxAppOpenAd maxAppOpenAd = (MaxAppOpenAd) ad;
//        boolean ready = maxAppOpenAd.isReady();
//        LogUtils.e(TAG, "-->  showOpenAd()  ready=" + ready);
//        if (ready) {
//            maxAppOpenAd.showAd(placeBean.getPlace());
//            status = Status.SHOW;
//            return true;
//        }
//
//        status = Status.UNSHOW;
//        if (listener != null) {
//            listener.onUnshow(MaxResource.this, -1, "MaxAppOpenAd is not Ready");
//        }
//        return false;
        return false;
    }

    @Override
    protected boolean showIntAd(Activity activity) {
//        MaxInterstitialAd maxInterstitialAd = (MaxInterstitialAd) ad;
//        boolean ready = maxInterstitialAd.isReady();
//        LogUtils.e(TAG, "-->  showIntAd()  ready=" + ready);
//        if (ready) {
//            maxInterstitialAd.showAd(placeBean.getPlace());
//            status = Status.SHOW;
//            return true;
//        }
//
//        status = Status.UNSHOW;
//        if (listener != null) {
//            listener.onUnshow(MaxResource.this, -1, "MaxInterstitialAd is not Ready");
//        }
//        return false;
        return false;
    }

    @Override
    protected boolean showNavAd(Activity activity, @Nullable ViewGroup container, @NonNull NativeAdViewCreator creator) {
//        if (container == null) {
//            return false;
//        }
//
//        MaxAd nativeAd = (MaxAd) ad;
//        if (maxNativeAdLoader == null || nativeAd.getNativeAd() == null || nativeAd.getNativeAd().isExpired()) {
//            status = Status.UNSHOW;
//            if (listener != null) {
//                String errorMsg = "errorMsg: {" + " maxNativeAdLoader=" + maxNativeAdLoader + "  nativeAd=" +nativeAd.getNativeAd() + "}";
//                listener.onUnshow(MaxResource.this, -1, errorMsg);
//            }
//            return false;
//        }
//
//        MaxNativeAdView maxNativeAdView = creator.create(Provider.MAX, activity);
//        maxNativeAdLoader.render(maxNativeAdView, nativeAd);
//        container.removeAllViews();
//        container.addView(maxNativeAdView);
//        status = Status.SHOW;
//        if (listener != null) {
//            listener.onShow(MaxResource.this);
//        }
//        return true;
        return false;
    }

    @Override
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
//        MaxAdView maxAdView = new MaxAdView(unitBean.getId(), activity);
//
//        maxAdView.setPlacement(placeBean.getPlace());
//
//        maxAdView.setRequestListener(new MaxAdRequestListener() {
//            @Override
//            public void onAdRequestStarted(String adUnitId) {
//                LogUtils.e(TAG, "-->  onAdRequestStarted() MaxAdView adUnitId=" + adUnitId);
//            }
//        });
//
//        maxAdView.setRevenueListener(new MaxAdRevenueListener() {
//            @Override
//            public void onAdRevenuePaid(MaxAd maxAd) {
//                LogUtils.e(TAG, "-->  onAdRevenuePaid() MaxAdView  maxAd=" + maxAd);
//                // 上报广告价值
//                AdjustAdRevenue adjustAdRevenue = new AdjustAdRevenue( AdjustConfig.AD_REVENUE_APPLOVIN_MAX );
//                adjustAdRevenue.setRevenue(maxAd.getRevenue(), "USD");
//                adjustAdRevenue.setAdRevenueNetwork(maxAd.getNetworkName());
//                adjustAdRevenue.setAdRevenueUnit(maxAd.getAdUnitId() );
//                adjustAdRevenue.setAdRevenuePlacement(maxAd.getPlacement());
//                Adjust.trackAdRevenue(adjustAdRevenue);
//            }
//        });
//
//        maxAdView.setListener(new MaxAdViewAdListener() {
//            @Override
//            public void onAdExpanded(MaxAd maxAd) {
//                LogUtils.e(TAG, "-->  onAdExpanded() MaxAdView  maxAd=" + maxAd);
//            }
//
//            @Override
//            public void onAdCollapsed(MaxAd maxAd) {
//                LogUtils.e(TAG, "-->  onAdCollapsed() MaxAdView  maxAd=" + maxAd);
//            }
//
//            @Override
//            public void onAdLoaded(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdLoaded() MaxAdView  maxAd=" + maxAd);
//
//                status = Status.PULL_SUCCESS;
//                timeLoaded = SystemClock.elapsedRealtime();
//
//                EventTracker.traceAdPullSuccess(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//
//                if (listener != null) {
//                    listener.onPullSuccess(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdLoadFailed(String s, MaxError maxError) {
//                LogUtils.e(TAG, "--> onAdLoadFailed() MaxAdView maxError=" + maxError);
//
//                status = Status.PULL_FAILED;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onPullFailed(MaxResource.this, code, errorMsg);
//                }
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdDisplayed() MaxAdView  maxAd=" + maxAd);
//
//                status = Status.SHOW;
//                if (listener != null) {
//                    listener.onShow(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdHidden(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdHidden() MaxAdView  maxAd=" + maxAd);
//
//                status = Status.DISMISS;
//                if (listener != null) {
//                    listener.onDismiss(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdClicked(MaxAd maxAd) {
//                LogUtils.e(TAG, "--> onAdClicked() MaxAdView  maxAd=" + maxAd);
//
//                EventTracker.traceAdClick(unitBean.getId(), placeBean.getPlace(), maxAd.getNetworkName());
//
//                if (listener != null) {
//                    listener.onClick(MaxResource.this);
//                }
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
//                LogUtils.e(TAG, "--> onAdDisplayFailed() MaxAdView maxAd=" + maxAd + "  maxError=" + maxError);
//
//                status = Status.UNSHOW;
//
//                int code = maxError.getCode();
//                String message = maxError.getMessage();
//                int mediatedNetworkErrorCode = maxError.getMediatedNetworkErrorCode();
//                String mediatedNetworkErrorMessage = maxError.getMediatedNetworkErrorMessage();
//                String errorMsg = String.format(Locale.getDefault(), "code: %d, message: %s, mediatedNetworkErrorCode: %d, mediatedNetworkErrorMessage: %s",
//                        code, message, mediatedNetworkErrorCode, mediatedNetworkErrorMessage);
//
//                if (listener != null) {
//                    listener.onUnshow(MaxResource.this, code, errorMsg);
//                }
//            }
//        });
//
//        maxAdView.setExtraParameter( "allow_pause_auto_refresh_immediately", "true" );
//        maxAdView.stopAutoRefresh();
//
//        status = Status.PULLING;
//        maxAdView.loadAd();
//
//        int widthPx = container.getLayoutParams().width;
//        if (widthPx <= 0) {
//            widthPx = ScreenUtils.getScreenWidth(); // dp
//        }
//        int heightPx = ConvertUtils.dp2px(50);
//
//        maxAdView.setLayoutParams(new FrameLayout.LayoutParams(widthPx, heightPx));
//        maxAdView.setBackgroundColor(Color.parseColor("#FFFFE7D6"));
//        container.removeAllViews();
//        container.addView(maxAdView);
//
//        maxAdView.startAutoRefresh();
//
//        ad = (T) maxAdView;
//        return true;
        return false;
    }
}
