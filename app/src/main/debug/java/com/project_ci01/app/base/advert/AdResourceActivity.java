package com.project_ci01.app.base.advert;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_ci01.app.base.bean.event.ConfigEvent;
import com.project_ci01.app.base.user.User;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.base.bean.event.AdResourceEvent;
import com.project_ci01.app.base.event.IEventListener;
import com.project_ci01.app.base.user.UserService;
import com.project_ci01.app.base.utils.LogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class AdResourceActivity extends BaseActivity implements IEventListener {

    protected Resource<?> nbRes; // 嵌入在 View 树中的广告，如 nav 广告，ban 广告
    protected Resource<?> connRes; // Connect 广告
    protected Resource<?> infoRes; // Info 广告
    protected Resource<?> extRes; // Extra广告

    protected PlaceType placeType;

    protected ViewGroup nbContainer;
    protected View nbStub;

    private NativeAdViewCreator creator;

    // 如果广告显示失败（返回 false），跳下一页；如果显示成功（返回 true），则在广告关闭的回调中跳下一页
    public boolean showAdInInfoPlace() {
        LogUtils.e(TAG, "--> showAdInInfoPlace()");

        // 迭代 1.0.5：自然用户：只有connect，原生广告，banner, loading写死3s；
        if (UserService.getService().getUser() == User.ORGANIC) {
            return false;
        }

//        Resource<?> res = AdResourceManager.INSTANCE.findResource(PlaceType.INFO);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(PlaceType.INFO);
        boolean adShowed = false;
        if (res != null && res.show(this, null)) {
            adShowed = true;
            infoRes = res;
        }
        return adShowed;
    }

    // 如果广告显示失败（返回 false），跳下一页；如果显示成功（返回 true），则在广告关闭的回调中跳下一页
    public boolean showAdInConnPlace() {
        LogUtils.e(TAG, "--> showAdInConnPlace()");
//        Resource<?> res = AdResourceManager.INSTANCE.findResource(PlaceType.Connect);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(PlaceType.Connect);
        boolean adShowed = false;
        if (res != null && res.show(this, null)) {
            adShowed = true;
            connRes = res;
        }
//        if (adShowed) {
//            EventTracker.trackConnectAdShow();
//        }
        return adShowed;
    }

    public boolean showAdInExtPlace() {
        LogUtils.e(TAG, "--> showAdInExtPlace()");

        // 迭代 1.0.5：自然用户：只有connect，原生广告，banner, loading写死3s；
        if (UserService.getService().getUser() == User.ORGANIC) {
            return false;
        }

//        Resource<?> res = AdResourceManager.INSTANCE.findResource(PlaceType.Connect);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(PlaceType.Connect, true);
        boolean adShowed = false;
        if (res != null && res.show(this, null)) {
            adShowed = true;
            extRes = res;
        }
        return adShowed;
    }

    protected void showAdInView(@NonNull PlaceType placeType, @NonNull ViewGroup container, View stub, NativeAdViewCreator creator) { // onStart 中调用，保证界面每次从后台到前台都加载新的广告
        this.placeType = placeType;
        this.nbContainer = container;
        this.nbStub = stub;
        this.creator = creator;

        LogUtils.e(TAG, "--> showAdInView()  placeType=" + placeType + "  container=" + container + "  nbRes=" + nbRes);

        if (nbRes == null) {
//            Resource<?> res = AdResourceManager.INSTANCE.findResource(placeType);
            Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(placeType);
            if (res != null && res.show(this, container, creator)) {
                nbRes = res;
                container.setVisibility(View.VISIBLE);
                if (stub != null) {
                    stub.setVisibility(View.GONE);
                }
            }
        }
    }

    public Resource<?> showAdInListView(@NonNull PlaceType placeType, @NonNull ViewGroup container, View stub, NativeAdViewCreator creator) {
        LogUtils.e(TAG, "--> showAdInListView()  placeType=" + placeType + "  container=" + container);
//        Resource<?> res = AdResourceManager.INSTANCE.findResource(placeType);
        Resource<?> res = AdResourceManager.INSTANCE.findMostWeightResource(placeType);
        if (res != null && res.show(this, container, creator)) {
            container.setVisibility(View.VISIBLE);
            if (stub != null) {
                stub.setVisibility(View.GONE);
            }
            return res;
        }
        return null;
    }

    protected void destroyAdInView(PlaceType placeType, ViewGroup container, View stub) {
        LogUtils.e(TAG, "--> destroyAdInView()  placeType=" + placeType + "  container=" + container + "  nbRes=" + nbRes);

        if (placeType == null) {
            return;
        }

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EventService.register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nbRes != null) {
            nbRes.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nbRes != null) {
            nbRes.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
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
                if (infoRes != null && infoRes == event.getData()) {
                    infoRes = null;
                }
                if (connRes != null && connRes == event.getData()) {
                    connRes = null;
                }
                if (extRes != null && extRes == event.getData()) {
                    extRes = null;
                }
                break;
            case TYPE_AD_UNPULL:
                if (event.getData() != null && UnitType.BAN == UnitType.convert(event.getData().getUnitBean().getType())) {
                    if (nbStub != null) {
                        nbStub.setVisibility(View.VISIBLE);
                    }
                    if (nbContainer != null && nbContainer.getVisibility() != View.GONE) {
                        nbContainer.setVisibility(View.GONE);
                    }
                }
                break;
            case TYPE_AD_SHOW:
                if (event.getData() != null && UnitType.BAN == UnitType.convert(event.getData().getUnitBean().getType())) {
                    if (nbStub != null) {
                        nbStub.setVisibility(View.GONE);
                    }
                    if (nbContainer != null && nbContainer.getVisibility() != View.VISIBLE) {
                        nbContainer.setVisibility(View.VISIBLE);
                    }
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
//        UnitType unitType = AdConfig.getUnitType(placeType);
//        if (unitType == null) {
//            return;
//        }
//
//        if (!UnitType.BAN.type.equals(unitType.type)) {
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
