package com.project_m1142.app.ui.dialog;

import android.widget.TextView;

import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;
import com.project_m1142.app.wifi.ext.dao.WifiEntity;

public class WifiDialogOne extends BaseDialog {

    private WifiEntity wifiEntity;

    @Override
    protected String tag() {
        return "WifiDialogOne";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wifi_one;
    }

    @Override
    protected void init() {
        findViewById(R.id.wifi_dialog_enter_pwd).setOnClickListener(v -> {
            confirm = true;
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });

        findViewById(R.id.wifi_dialog_ignore).setOnClickListener(v -> {
            cancel = true;
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });

        findViewById(R.id.wifi_dialog_later).setOnClickListener(v -> {
            dismiss();
        });

        if (wifiEntity != null) {
            TextView tvSsid = findViewById(R.id.wifi_dialog_ssid);
            tvSsid.setText(wifiEntity.ssid);
        }
    }

    public void setWifiEntity(WifiEntity wifiEntity) {
        this.wifiEntity = wifiEntity;
    }

    public WifiEntity getWifiEntity() {
        return wifiEntity;
    }
}
