package com.project_m1142.app.base.advert;

import android.app.Activity;

import androidx.annotation.LayoutRes;

import com.project_m1142.app.base.applovin.MaxNavRender;
//import com.applovin.mediation.nativeAds.MaxNativeAdView;
//import com.google.android.gms.ads.nativead.NativeAd;
//import com.google.android.gms.ads.nativead.NativeAdView;

public abstract class NativeAdViewCreator {

    private static final String TAG = "NativeAdViewCreator";

//    NativeAdView create(Provider provider, NativeAd ad, Activity activity) {
//        return AdmobNavRender.render(getLayoutId(provider), hasMediaView(), ad, activity);
//    }
//
//    public MaxNativeAdView create(Provider provider, Activity activity) {
//        return MaxNavRender.render(getLayoutId(provider), hasMediaView(), activity);
//    }

    protected abstract boolean hasMediaView();

    protected abstract @LayoutRes int getLayoutId(Provider provider);
}
