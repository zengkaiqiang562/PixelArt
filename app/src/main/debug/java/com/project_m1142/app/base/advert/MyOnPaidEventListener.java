package com.project_m1142.app.base.advert;

import com.project_m1142.app.base.bean.gson.PlaceBean;
import com.project_m1142.app.base.bean.gson.UnitBean;
//import com.google.android.gms.ads.AdValue;
//import com.google.android.gms.ads.OnPaidEventListener;


public class MyOnPaidEventListener/* implements OnPaidEventListener*/ {

    private final PlaceBean placeBean;
    private final UnitBean unitBean;
    private final String adapter;

    public MyOnPaidEventListener(PlaceBean placeBean, UnitBean unitBean, String adapter) {
        this.placeBean = placeBean;
        this.unitBean = unitBean;
        this.adapter = adapter == null ? "" : adapter;
    }

//    @Override
//    public void onPaidEvent(@NonNull AdValue adValue) {
//        String network = AdConfig.getAdChannel(adapter); // 广告源渠道
//
//        if (placeBean == null || unitBean == null) {
//            return;
//        }
//
//        ThirdSdkManager.reportRevenue(adValue.getValueMicros(), adValue.getCurrencyCode(), network, unitBean.getId(), placeBean.getPlace());
//    }
}
