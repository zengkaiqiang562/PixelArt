package com.project_m1142.app.ui.wifi;


import com.project_m1142.app.wifi.ext.dao.WifiEntity;

public class WifiItem implements IItem {

    public final WifiEntity wifiEntity;

    public WifiItem(WifiEntity wifiEntity) {
        this.wifiEntity = wifiEntity;
    }

    @Override
    public int getType() {
        return TYPE_WIFI;
    }
}
