package com.project_m1142.app.ui.dialog;

import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;
import com.project_m1142.app.wifi.ext.dao.WifiEntity;

public class WifiDialogTwo extends BaseDialog {

    private WifiEntity wifiEntity;

    private EditText pwdInput;
    private ImageView pwdDisplay;

    private boolean pwdShow = false;

    @Override
    protected String tag() {
        return "WifiDialogOne";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wifi_two;
    }

    @Override
    protected void init() {

        pwdShow = false;
        pwdInput = findViewById(R.id.wifi_dialog_pwd_input);
        pwdDisplay = findViewById(R.id.wifi_dialog_pwd_display);

        findViewById(R.id.wifi_dialog_connection).setOnClickListener(v -> {
            confirm = true;
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });

        findViewById(R.id.wifi_dialog_cancel).setOnClickListener(v -> {
            cancel = true;
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });

        pwdDisplay.setOnClickListener(v -> {
            pwdShow = !pwdShow;
            pwdDisplay.setSelected(pwdShow);
            updatePwdInput();
        });


        if (wifiEntity != null) {
            TextView tvSsid = findViewById(R.id.wifi_dialog_ssid);
            tvSsid.setText(wifiEntity.ssid);
            if (!TextUtils.isEmpty(wifiEntity.password)) {
                pwdInput.setText(wifiEntity.password);
            }
        }
        pwdDisplay.setSelected(pwdShow);
        updatePwdInput();
    }

    private void updatePwdInput() {
        if (pwdInput == null) return;
        if (pwdShow) {
            pwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        } else {
            pwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        pwdInput.invalidate();
    }

    public void setWifiEntity(WifiEntity wifiEntity) {
        this.wifiEntity = wifiEntity;
    }

    public WifiEntity getWifiEntity() {
        return wifiEntity;
    }

    public String getPassword() {
        if (pwdInput == null || pwdInput.getText() == null) {
            return "";
        } else {
            return pwdInput.getText().toString();
        }
    }
}
