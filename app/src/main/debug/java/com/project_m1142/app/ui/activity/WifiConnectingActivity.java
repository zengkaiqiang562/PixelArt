package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.project_m1142.app.base.config.AppConfig;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.databinding.ActivityAboutBinding;
import com.project_m1142.app.databinding.ActivityWifiConnectingBinding;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.wifi.ext.NetworkState;
import com.project_m1142.app.wifi.ext.WifiManagerExt;

public class WifiConnectingActivity extends BaseActivity implements WifiManagerExt.OnNetworkStateChangeListener {

    private static final int WHAT_CONNECTING_TIMEOUT = 3001;

    ActivityWifiConnectingBinding binding;

    @Override
    protected String tag() {
        return "WifiConnectingActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityWifiConnectingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WifiManagerExt.getInstance().addOnNetworkStateChangeListener(this);
        init(getIntent());

        EventTracker.trackWifiConnectingShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        binding.wifiConnectingBack.setOnClickListener(v -> {
            back();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendConnectingTimeout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeConnectingTimeout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiManagerExt.getInstance().removeOnNetworkStateChangeListener(this);
    }

    @Override
    protected void back() {
        back(false);
    }

    private void back(boolean complete) {
        Intent intent = new Intent();
        intent.putExtra(IntentConstants.EXTRA_COMPLETE_FLAG, complete);
        setResult(RESULT_OK, intent);
        finish();
    }


    private void sendConnectingTimeout() {
        if (uiHandler.hasMessages(WHAT_CONNECTING_TIMEOUT)) {
            uiHandler.removeMessages(WHAT_CONNECTING_TIMEOUT);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_CONNECTING_TIMEOUT, 30 * 1000L); // 30s timeout
    }

    private void removeConnectingTimeout() {
        if (uiHandler.hasMessages(WHAT_CONNECTING_TIMEOUT)) {
            uiHandler.removeMessages(WHAT_CONNECTING_TIMEOUT);
        }
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_CONNECTING_TIMEOUT) {
            back();
            ToastUtils.showShort("Connecting Timeout");
        }
    }

    /*=============== wifi 监听 ============*/

    @Override
    public void onNetworkStateChanged(@NonNull NetworkState networkState) {
        LogUtils.e(TAG, "--> onNetworkStateChanged()  networkState=" + networkState);
        if (networkState == NetworkState.CONNECTED
                || networkState == NetworkState.FAILED
                || networkState == NetworkState.BLOCKED
                || networkState == NetworkState.CAPTIVE_PORTAL_CHECK
                || networkState == NetworkState.SUPPLICANT_ERROR_AUTHENTICATING
                || networkState == NetworkState.SUPPLICANT_ERROR_OTHER
        ) {
            back(true);
        }
    }
}
