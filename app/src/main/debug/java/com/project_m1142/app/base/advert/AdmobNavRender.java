package com.project_m1142.app.base.advert;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.project_m1142.app.base.utils.LogUtils;
//import com.google.android.gms.ads.MediaContent;
//import com.google.android.gms.ads.nativead.MediaView;
//import com.google.android.gms.ads.nativead.NativeAd;
//import com.google.android.gms.ads.nativead.NativeAdView;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class AdmobNavRender {

    private static final String TAG = "AdmobNavRender";

//    static NativeAdView render(int layoutId, boolean hasMediaView, NativeAd ad, Activity activity) {
//
//        NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(layoutId, null);
//        if (hasMediaView) {
//            initWithMediaView(ad, adView, activity);
//        } else {
//            initNoMediaView(ad, adView, activity);
//        }
//        return adView;
//    }

//    private static void initWithMediaView(NativeAd nativeAd, NativeAdView adView, Activity activity) {
//
//        setTitle(nativeAd, adView);
//        setBody(nativeAd,adView);
//        setInstallButton(nativeAd, adView);
//        setLogo(nativeAd, adView, activity);
//        setMediaView(nativeAd, adView);
//
//        // This method tells the Google Mobile Ads SDK that you have finished populating your
//        // native ad view with this native ad.
//        adView.setNativeAd(nativeAd);
//    }

//    private static void initNoMediaView(NativeAd nativeAd, NativeAdView adView, Activity activity) {
//
//        setTitle(nativeAd, adView);
//        setBody(nativeAd,adView);
//        setInstallButton(nativeAd, adView);
//        setLogo(nativeAd, adView, activity);
//
//        // This method tells the Google Mobile Ads SDK that you have finished populating your
//        // native ad view with this native ad.
//        adView.setNativeAd(nativeAd);
//    }

//    private static void setTitle(NativeAd nativeAd, NativeAdView adView) {
//        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
//        View headlineView = adView.getHeadlineView();
//        if (headlineView instanceof TextView) {
//            ((TextView) headlineView).setText(nativeAd.getHeadline());
//        }
//    }

//    private static void setBody(NativeAd nativeAd, NativeAdView adView) {
//        adView.setBodyView(adView.findViewById(R.id.ad_body));
//        View bodyView = adView.getBodyView();
//        String body = nativeAd.getBody();
//        if (bodyView instanceof TextView) {
//            boolean empty = TextUtils.isEmpty(body);
//            bodyView.setVisibility(empty ? View.INVISIBLE : View.VISIBLE);
//            if (!empty) {
//                ((TextView) bodyView).setText(body);
//            }
//        }
//    }

//    private static void setInstallButton(NativeAd nativeAd, NativeAdView adView) {
//        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
//        View callToActionView = adView.getCallToActionView();
//        String callToAction = nativeAd.getCallToAction();
//        if (callToActionView instanceof TextView) {
//            boolean empty = TextUtils.isEmpty(callToAction);
//            callToActionView.setVisibility(empty ? View.INVISIBLE : View.VISIBLE);
//            if (!empty) {
//                ((TextView) callToActionView).setText(callToAction);
//            }
//        }
//    }

//    private static void setLogo(NativeAd nativeAd, NativeAdView adView, Activity activity) {
//        View view = adView.findViewById(R.id.ad_app_icon);
//        if (view == null) {
//            return;
//        }
//        adView.setIconView(view);
//        View iconView = adView.getIconView();
//
//        // 小图标
//        NativeAd.Image icon = nativeAd.getIcon();
//        if (iconView instanceof ImageView) {
//            boolean empty = icon == null;
//            iconView.setVisibility(empty ? View.INVISIBLE : View.VISIBLE);
//            if (!empty) {
//                Drawable iconDrawable = icon.getDrawable();
//
//                LogUtils.e(TAG, "--> setLogo()  iconDrawable=" + iconDrawable);
//
//                RoundedCornersTransformation transformation =
//                        new RoundedCornersTransformation(ConvertUtils.dp2px(8), 0);
//
//                RequestOptions options = new RequestOptions()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .transform(new CenterCrop(), transformation);
//
//                Glide.with(activity)
//                        .asDrawable()
//                        .apply(options)
//                        .load(iconDrawable)
//                        .into((ImageView) iconView);
//            }
//        }
//    }

//    private static void setMediaView(NativeAd nativeAd, NativeAdView adView) {
//        adView.setMediaView(adView.findViewById(R.id.ad_media));
//        MediaView mediaView = adView.getMediaView();
//        MediaContent mediaContent = nativeAd.getMediaContent();
//        if (mediaView != null && mediaContent != null) {
//            mediaView.setMediaContent(mediaContent);
//        }
//    }
}
