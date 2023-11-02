package com.project_m1142.app.base.advert;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//import com.applovin.mediation.MaxAd;
//import com.applovin.mediation.ads.MaxAppOpenAd;
//import com.applovin.mediation.ads.MaxInterstitialAd;
import com.project_m1142.app.base.applovin.MaxResource;
import com.project_m1142.app.base.bean.gson.PlaceBean;
import com.project_m1142.app.base.bean.gson.UnitBean;
import com.project_m1142.app.base.utils.LogUtils;
//import com.google.android.gms.ads.appopen.AppOpenAd;
//import com.google.android.gms.ads.interstitial.InterstitialAd;
//import com.google.android.gms.ads.nativead.NativeAd;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ResourcePuller {

    protected static final String TAG = "ResourcePuller";

    protected final ExecutorService executorService = Executors.newCachedThreadPool();

    void pullAds(ResourceListener listener) {
        List<PlaceBean> placeBeans = AdConfig.getAllPlaceBeans();
        if (placeBeans == null || placeBeans.size() <= 0) {
            return;
        }

        LogUtils.d("##### 准备拉取所有广告 #####");

        for (PlaceBean placeBean : placeBeans) { // 遍历所有广告位
            if (!placeBean.isEnable()) { // 广告位关闭则不加载广告
                LogUtils.d("<" + placeBean.getPlace() + ">广告关闭");
                continue;
            }

            if (placeBean.isUnitBeansEmpty()) {
                continue;
            }
            UnitBean mostWeightUnitBean = placeBean.getMostWeightUnitBean();
            if (mostWeightUnitBean == null) {
                LogUtils.d("<" + placeBean.getPlace() + ">广告位中不存在可拉取的广告单元");
                continue;
            }
            pullAdInternal(placeBean, mostWeightUnitBean, listener); // 遍历每个广告位上的广告
        }

        LogUtils.d("#######################");
    }

    boolean pullAdInPlace(@NonNull PlaceType placeType, @Nullable UnitBean moreWeightUnitBean, ResourceListener listener) {
        PlaceBean placeBean = AdConfig.getPlaceBean(placeType); // 如果指定广告位上关闭了广告，返回 null

        if (placeBean == null || placeBean.isUnitBeansEmpty()) {
            return false;
        }

        UnitBean unitBean;

        if (moreWeightUnitBean == null) {
            unitBean = placeBean.getMostWeightUnitBean();
            if (unitBean == null) {
                LogUtils.d("<" + placeBean.getPlace() + ">广告位中不存在可拉取的广告单元");
                return false;
            }
        } else {
            unitBean = placeBean.getLessWeightUnitBean(moreWeightUnitBean);
            LogUtils.e(TAG, "--> pullAdInPlace getLessWeightUnitBean  moreWeightUnitBean=" + moreWeightUnitBean + "  lessWeightUnitBean=" + unitBean);
            if (unitBean == null) {
                LogUtils.d("<" + placeBean.getPlace() + ">广告位中不存在比广告单元 " + moreWeightUnitBean + " 权重更低的广告");
                return false;
            }
        }

        return pullAdInternal(placeBean, unitBean, listener); // 遍历指定广告位上的广告
    }

    protected boolean pullAdInternal(@NonNull PlaceBean placeBean, @NonNull UnitBean unitBean, ResourceListener listener) {

//        PlaceType placeType = PlaceType.convert(placeBean.getPlace());
//        UnitType unitType = UnitType.convert(unitBean.getType());
//
//        if (TextUtils.isEmpty(unitBean.getId()) || unitType == null) {
//            LogUtils.e(TAG, "--> pullAdInternal() with invalid params  unitBean=" + unitBean + "  unitType=" + unitType);
//            return false;
//        }
//
//        if (unitType == UnitType.BAN) {
//            LogUtils.e(TAG, "--> pullAdInternal() don't need to Pull BAN");
//            return false;
//        }
//
//
//        Resource<?> prepareRes = Buffer.get(placeType, Status.PREPARE);
//        Resource<?> pullingRes = Buffer.get(placeType, Status.PULLING);
//        Resource<?> pulledRes = Buffer.get(placeType, Status.PULL_SUCCESS);
//
//        LogUtils.e(TAG, "--> pullAdInternal()  placeType=" + placeType + " unitType=" + unitType
//                + " prepareRes=" + prepareRes
//                + " pullingRes=" + pullingRes
//                + " pulledRes=" + pulledRes);
//
//        if (pulledRes != null && pulledRes.isExpired()) {
//            Buffer.remove(placeType, Status.PULL_SUCCESS);
//            pulledRes = null;
//        }
//
//        if (prepareRes != null || pullingRes != null || pulledRes != null) {
//            // 若 广告位上有广告 正准备拉取 or 正在拉取 or 已缓存有拉取成功的广告，则不再拉取
//            return false;
//        }
//
////        if (type == BillType.NAV && isArrivedNavMaxClickTimes()) { // 当天 nav 广告的点击次数超上限时，不加载 nav 广告
////            Slog.e(TAG, "--> loadBill() don't load nav because of max click times");
////            Slog.td("类型为<" + type.type + ">的广告的单日点击次数达到上限，不再拉取该广告");
////            return;
////        }
//
//        Resource<?> resource = null;
//        switch (unitType) {
//            case START:
//                resource = UnitType.START.provider == Provider.ADMOB ? new Resource<AppOpenAd>(placeBean, unitBean, listener) : new MaxResource<MaxAppOpenAd>(placeBean, unitBean, listener);
//                break;
//            case INT:
//                resource = UnitType.INT.provider == Provider.ADMOB ? new Resource<InterstitialAd>(placeBean, unitBean, listener) : new MaxResource<MaxInterstitialAd>(placeBean, unitBean, listener);
//                break;
//            case NAV:
//                resource = UnitType.NAV.provider == Provider.ADMOB ? new Resource<NativeAd>(placeBean, unitBean, listener) : new MaxResource<MaxAd>(placeBean, unitBean, listener);
//                break;
//        }
//
//        LogUtils.e(TAG, "--> pullAdInternal()  AdResource=" + resource);
//
//        if (resource != null) {
//            LogUtils.d("开始拉取<" + placeBean.getPlace() + ">广告 ## " + unitBean);
//            executorService.execute(resource::pull);
//            Buffer.put(placeType, resource);
//            return true;
//        }
//
//        return false;
        return false;
    }
}
