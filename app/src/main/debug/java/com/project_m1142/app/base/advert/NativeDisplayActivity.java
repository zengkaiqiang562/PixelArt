package com.project_m1142.app.base.advert;

import android.view.View;
import android.view.ViewGroup;

import com.project_m1142.app.base.bean.event.AdResourceEvent;
import com.project_m1142.app.base.event.EventType;
import com.project_m1142.app.base.utils.LogUtils;

public abstract class NativeDisplayActivity extends AdResourceActivity {

    protected ViewGroup navContainer() { return null; }
    protected View navStub() { return null; }
    protected PlaceType navPlace() { return null; }
    protected NativeAdViewCreator navCreator() { return null; }

    protected boolean navEnable() {
        return navContainer() != null && navStub() != null && navPlace() != null && navCreator() != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showNative();
    }

    protected void showNative() {
        if (navEnable()) {
            uiHandler.postDelayed(() -> {
                LogUtils.e(TAG, "-->  showNative() postDelayed  isResumed=" + isResumed + "  nbRes=" + nbRes);
                if (isResumed && (nbRes == null)) {
                    showAdInView(navPlace(), navContainer(), navStub(), navCreator());
                }
            }, 300); // 延迟 300ms，避免插页广告在 onResume 之后才展示出来，导致原生广告在插页广告显示时就显示出来
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyAdInView();
    }

    @Override
    public void onEvent(AdResourceEvent event) {
        if (navEnable()) {
            // 原生广告加载成功
            if (event.getData() != null && event.getData().getPlaceBean() != null
                    && EventType.TYPE_AD_PULLED == event.getType()
                    && UnitType.NAV == UnitType.convert(event.getData().getUnitBean().getType())) {
                showNative();
            }
        }

        super.onEvent(event);
    }

    protected void updateNative() {
        if (!navEnable()) {
            return;
        }
        destroyAdInView(navPlace(), navContainer(), navStub());
        showNative();
    }

    protected void destroyAdInView() {
        destroyAdInView(navPlace(), navContainer(), navStub());
    }
}
