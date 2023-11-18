package com.project_ci01.app.base.advert;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_ci01.app.base.bean.event.ConfigEvent;
import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.base.bean.event.AdResourceEvent;
import com.project_ci01.app.base.utils.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class AdResourceFragment extends BaseFragment {
    protected Resource<?> nbRes; // 嵌入在 View 树中的广告，如 nav 广告，ban 广告
    protected Resource<?> connRes; // Connect 广告
    protected Resource<?> extRes; // Extra广告

    protected PlaceType placeType;

    protected ViewGroup nbContainer;
    protected ViewGroup nbStub;

    private NativeAdViewCreator creator;

    // 如果广告显示失败（返回 false），跳下一页；如果显示成功（返回 true），则在广告关闭的回调中跳下一页
    protected boolean showAdInConnPlace() {
        LogUtils.e(TAG, "--> showAdInConnPlace()");
//        Resource<?> res = AdResourceManager.INSTANCE.findResource(PlaceType.Connect);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(PlaceType.Connect);
        boolean adShowed = false;
        if (res != null && res.show(activity, null)) {
            adShowed = true;
            connRes = res;
        }
        return adShowed;
    }

    protected boolean showAdInExtPlace() {
        LogUtils.e(TAG, "--> showAdInExtPlace()");
//        Resource<?> res = AdResourceManager.INSTANCE.findResource(PlaceType.Connect);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(PlaceType.Connect, true);
//        if (res == null && AdConfig.getUnitType(PlaceType.START) == UnitType.INT) {
//            // bug #32085 ：start广告位类型为int，start广告位开启，connect广告位关闭，拉取到start广告，额外广告展示start广告
//            res = AdResourceManager.INSTANCE.findResource(PlaceType.START);
//        }
        boolean adShowed = false;
        if (res != null && res.show(activity, null)) {
            adShowed = true;
            extRes = res;
        }
        return adShowed;
    }

    protected void showAdInView(@NonNull PlaceType placeType, @NonNull ViewGroup container, ViewGroup stub, NativeAdViewCreator creator) { // onStart 中调用，保证界面每次从后台到前台都加载新的广告
        this.placeType = placeType;
        this.nbContainer = container;
        this.nbStub = stub;
        this.creator = creator;

        LogUtils.e(TAG, "--> showAdInView()  placeType=" + placeType + "  container=" + container + "  nbRes=" + nbRes);

        if (nbRes == null) {
//            Resource<?> res = AdResourceManager.INSTANCE.findResource(placeType);
            Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(placeType);
            if (res != null && res.show(activity, container, creator)) {
                nbRes = res;
                container.setVisibility(View.VISIBLE);
                if (stub != null) {
                    stub.setVisibility(View.GONE);
                }
            }
        }
    }

    public Resource<?> showAdInListView(@NonNull PlaceType placeType, @NonNull ViewGroup container, ViewGroup stub) {
        LogUtils.e(TAG, "--> showAdInListView()  placeType=" + placeType + "  container=" + container);
//        Resource<?> res = AdResourceManager.INSTANCE.findResource(placeType);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(placeType);
        if (res != null && res.show(activity, container)) {
            container.setVisibility(View.VISIBLE);
            if (stub != null) {
                stub.setVisibility(View.GONE);
            }
            return res;
        }
        return null;
    }

    protected void destroyAdInView(PlaceType placeType, ViewGroup container, ViewGroup stub) {
        LogUtils.e(TAG, "--> destroyAdInView()  placeType=" + placeType + "  container=" + container + "  nbRes=" + nbRes);

        if (nbRes != null) {
            nbRes.destroy();
            nbRes = null;
        }

        if (container != null) {
            container.removeAllViews();
            container.setVisibility(View.GONE);
        }

        if (stub != null) {
            stub.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventService.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (nbRes != null) {
            nbRes.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (nbRes != null) {
            nbRes.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyAdInView(placeType, nbContainer, nbStub);
//        EventService.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AdResourceEvent event) {
        LogUtils.e(TAG, "--> onEvent()  AdResourceEvent=" + event);
        switch (event.getType()) {
            case TYPE_AD_DISMISS:
            case TYPE_AD_UNSHOW:
                if (connRes != null && connRes == event.getData()) {
                    connRes = null;
                }
                if (extRes != null && extRes == event.getData()) {
                    extRes = null;
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConfigEvent event) {
//        LogUtils.e(TAG, "--> onEvent()  ConfigEvent=" + event);
//        if (placeType == null || nbContainer == null) {
//            return;
//        }
//        UnitType type = AdConfig.getUnitType(placeType);
//        if (type == null) {
//            return;
//        }
//
//        if (!UnitType.BAN.type.equals(type.type)) {
//            return;
//        }
//
//        // 处理 ban
//        boolean isOpen = AdConfig.isPlaceOpen(placeType);
//        LogUtils.e(TAG, "--> onEvent()  placeType=" + placeType + "  isOpen=" + isOpen);
//        if (!isOpen) {
//            uiHandler.post(() -> {
//                destroyAdInView(placeType, nbContainer, nbStub);
//            });
//        } else if (nbRes != null && UnitType.BAN.type.equals(nbRes.getUnitBean().getType())){ // 避免 nav 切 ban 的情况，只考虑 ban 广告的开关
//            uiHandler.post(() -> {
//                showAdInView(placeType, nbContainer, nbStub, creator);
//            });
//        }
    }
}
