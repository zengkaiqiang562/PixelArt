package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.permission.PermissionHelper;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.base.view.dialog.DialogHelper;
import com.project_m1142.app.base.view.dialog.SimpleDialogListener;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.databinding.ActivityWifiBinding;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.ui.dialog.CompletedDialog;
import com.project_m1142.app.ui.dialog.WifiDialogOne;
import com.project_m1142.app.ui.dialog.WifiDialogTwo;
import com.project_m1142.app.ui.helper.TabSwitcher;
import com.project_m1142.app.ui.wifi.IItem;
import com.project_m1142.app.ui.wifi.WifiAdapter;
import com.project_m1142.app.ui.wifi.WifiItem;
import com.project_m1142.app.wifi.ext.NetworkState;
import com.project_m1142.app.wifi.ext.WifiHelper;
import com.project_m1142.app.wifi.ext.WifiManagerExt;
import com.project_m1142.app.wifi.ext.dao.WifiEntity;

import java.util.ArrayList;
import java.util.List;

public class WiFiActivity extends BaseActivity implements OnItemClickListener<BaseHolder<IItem>>
        , WifiManagerExt.OnWifiStateChangeListener, WifiManagerExt.OnNetworkStateChangeListener, WifiManagerExt.OnWifiListChangedListener {

    ActivityWifiBinding binding;

    private WifiAdapter adapter;
//    private WifiManager wifiManager;

    private WifiDialogOne wifiDialogOne;
    private WifiDialogTwo wifiDialogTwo;
//    private ConnectivityManager.NetworkCallback networkCallback;

    private CompletedDialog completedDialog;

    @Override
    protected String tag() {
        return "WiFiActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityWifiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getIntent());
        initWifi();

        EventTracker.trackWifiShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void initWifi() {
//        wifiManager = WifiManager.create(this);
//        wifiManager.setOnWifiChangeListener(wifis -> { // onWifiChanged(List<IWifi> wifis)
////            LogUtils.e(TAG, "--> onWifiChanged()  wifis.size=" + wifis.size());
////            for (IWifi wifi : wifis) {
////                LogUtils.e(TAG, "--> onWifiChanged()  wifi=" + wifi);
////            }
//            if (ContextManager.isSurvival(this) && adapter != null) {
//                List<IItem> items = new ArrayList<>();
//                for (IWifi iWifi : wifis) {
//                    if (iWifi instanceof Wifi) {
//                        items.add(new WifiItem((Wifi) iWifi));
//                    }
//                }
//                adapter.setItems(items);
//            }
//        });


//        wifiManager.setOnWifiConnectListener(status -> { // onConnectChanged(boolean status)
//            LogUtils.e(TAG, "--> onConnectChanged()  status=" + status);
//        });
//
//        wifiManager.setOnWifiStateChangeListener(state -> { // onStateChanged(State state)
//            LogUtils.e(TAG, "--> onStateChanged()  state=" + state);
//        });

        WifiManagerExt.getInstance().addOnWifiStateChangeListener(this);
        WifiManagerExt.getInstance().addOnNetworkStateChangeListener(this);
        WifiManagerExt.getInstance().addOnWifiListChangedListener(this);

        WifiManagerExt.getInstance().registerWifiReceiver();

        WifiManagerExt.getInstance().updateWifiList(true);
    }

    private void destoryWifi() {
//        if (wifiManager != null) {
//            wifiManager.destroy();
//        }
        WifiManagerExt.getInstance().unregisterWifiReceiver();
        WifiManagerExt.getInstance().removeOnWifiStateChangeListener(this);
        WifiManagerExt.getInstance().removeOnNetworkStateChangeListener(this);
        WifiManagerExt.getInstance().removeOnWifiListChangedListener(this);
    }

    private void init(Intent intent) {

        adapter = new WifiAdapter(this, this);
        binding.wifiRv.setAdapter(adapter);
        binding.wifiRv.setLayoutManager(new LinearLayoutManager(this));

        binding.homeMenuVpnParent.setOnClickListener(v -> {
            TabSwitcher.startHomeActivity(this);
        });

        binding.homeMenuMineParent.setOnClickListener(v -> {
            TabSwitcher.startMineActivity(this);
        });
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        if (!PermissionHelper.checkLocationPermission(this)) {
            PermissionHelper.applyLocationPermission(this, false, granted -> {
                if (granted) {
                    scanWifi();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionHelper.checkLocationPermission(this)) {
            scanWifi();
        }
    }

    @Override
    protected void back() {
        ContextManager.INSTANCE.finishAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destoryWifi();
    }

    @Override
    public void onItemClick(int item, BaseHolder<IItem> holder) {
        if (holder instanceof WifiAdapter.WifiHolder) {
            WifiEntity wifiEntity = ((WifiAdapter.WifiHolder) holder).wifiItem.wifiEntity;
//            // TODO 直接跳转到wifi设置页
//            Intent settingsIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
//            startActivityForResult(settingsIntent, 11);
            if (WifiHelper.isWifiConnected(this, wifiEntity)) { // 已连接的wifi不处理
                ToastUtils.showShort(wifiEntity.ssid + " is Connected");
                return;
            }

            showWifiDialogOne(wifiEntity);
        }
    }

    public void scanWifi() {
        WifiManagerExt.getInstance().scanWifi(this);
    }

    private void showWifiDialogOne(@NonNull WifiEntity wifiEntity) {
        wifiDialogOne = DialogHelper.showDialog(this, wifiDialogOne, WifiDialogOne.class, new SimpleDialogListener<WifiDialogOne>() {
            @Override
            public void onShowBefore(WifiDialogOne dialog) {
                dialog.setWifiEntity(wifiEntity);
            }

            @Override
            public void onConfirm() {
                // Show Dialog two (input pwd)
                showWifiDialogTwo(wifiDialogOne.getWifiEntity());
            }

            @Override
            public void onCancel() {
                // Ignore wifi
                WifiManagerExt.getInstance().addIgnoreWifi(wifiEntity);
            }
        });

        EventTracker.trackWifiDialogShow();
    }

    public void showWifiDialogTwo(@NonNull WifiEntity wifiEntity) {
        wifiDialogTwo = DialogHelper.showDialog(this, wifiDialogTwo, WifiDialogTwo.class, new SimpleDialogListener<WifiDialogTwo>() {
            @Override
            public void onShowBefore(WifiDialogTwo dialog) {
                dialog.setWifiEntity(wifiEntity);
            }

            @Override
            public void onConfirm() {
                // Connect wifi
                WifiEntity connWifi = wifiDialogTwo.getWifiEntity();
                String password = wifiDialogTwo.getPassword();
                LogUtils.e(TAG, "--> WifiDialogTwo onConfirm()  password=" + password + ", connWifi=" + connWifi);
//                boolean result = false;
//                if (wifiManager != null) {
//                    result = wifiManager.connectEncryptWifi(connWifi, pwd);
//                    LogUtils.e(TAG, "--> WifiDialogTwo connectEncryptWifi()  result=" + result);
//                }
//
//                if (!result) {
//                    connectWifi_2(connWifi, pwd);
////                    connectwifi(connWifi, pwd);
//                }

                boolean result;
                if (connWifi.isEncrypt) {
                    result = WifiManagerExt.getInstance().connectEncryptWifi(connWifi, password);
                } else {
                    result = WifiManagerExt.getInstance().connectOpenWifi(connWifi);
                }
                LogUtils.e(TAG, "--> WifiDialogTwo connectEncryptWifi()  result=" + result);

            }

            @Override
            public void onCancel() {}
        });

        EventTracker.trackWifiDialogShow();
    }

    private void startWifiConnectingActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, WifiConnectingActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_WIFI_CONNECTING);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(TAG, "--> onActivityResult()  requestCode=" + requestCode + "  resultCode=" + resultCode + "  data=" + data);

        if (requestCode == IntentConstants.REQUEST_CODE_WIFI_SETTING) {
            showCompletedDialog();
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == IntentConstants.REQUEST_CODE_WIFI_CONNECTING) {
            if (data != null && data.getBooleanExtra(IntentConstants.EXTRA_COMPLETE_FLAG, false)) {
                showCompletedDialog();
            }
        }
    }

    private void showCompletedDialog() { // 是否返回来源页
        completedDialog = DialogHelper.showDialog(this, completedDialog, CompletedDialog.class, new SimpleDialogListener<CompletedDialog>() {
            @Override
            public void onDismiss() {}
        });
    }

    /*==================== Wifi 监听器 =================*/

    @Override
    public void onWifiStateChanged(int wifiState) {
        /*
         * WifiManager.WIFI_STATE_DISABLED = 1;
         * WifiManager.WIFI_STATE_DISABLING = 0;
         * WifiManager.WIFI_STATE_ENABLED = 3;
         * WifiManager.WIFI_STATE_ENABLING = 2;
         * WifiManager.WIFI_STATE_UNKNOWN = 4;
         */
        LogUtils.e(TAG, "--> onWifiStateChanged()  wifiState=" + wifiState);
    }

    @Override
    public void onNetworkStateChanged(@NonNull NetworkState networkState) {
        LogUtils.e(TAG, "--> onNetworkStateChanged()  networkState=" + networkState);
        if (networkState == NetworkState.START_CONNECT) {
            startWifiConnectingActivity();
        }/* else if (networkState == NetworkState.CONNECTED) {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort(R.string.wifi_connect_exception_prompt); // 回调wifi连接成功，但无网络时弹Toast
            }
        }*/

    }

    @Override
    public void onWifiListChanged(@NonNull List<WifiEntity> wifiEntities) {
        LogUtils.e(TAG, "--> onWifiListChanged()  wifiEntities.size=" + wifiEntities.size());
        if (ContextManager.isSurvival(this) && adapter != null) {
            List<IItem> items = new ArrayList<>();
            for (WifiEntity wifiEntity : wifiEntities) {
                items.add(new WifiItem(wifiEntity));
            }
            adapter.setItems(items);
            binding.wifiRv.scrollToPosition(0);
        }
    }

    //    private void connectwifi(Wifi wifi, String pwd) {
//        WifiNetworkSuggestion suggestion1 = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            suggestion1 = new WifiNetworkSuggestion.Builder()
//                    .setSsid(wifi.scanResult.SSID)
//                    .setBssid(MacAddress.fromString(wifi.scanResult.BSSID))
//                    .setWpa2Passphrase(pwd)
//                    .setIsAppInteractionRequired(true) // Optional (Needs location permission)
//                    .build();
//
//            List<WifiNetworkSuggestion> suggestionsList = new ArrayList<WifiNetworkSuggestion>();
//            suggestionsList.add(suggestion1);
//
//            android.net.wifi.WifiManager wifiManager1 = (android.net.wifi.WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            int status = wifiManager1.addNetworkSuggestions(suggestionsList);
//
//            if (status != android.net.wifi.WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//                LogUtils.e(TAG, "--> connectwifi()  status Failed");
//            } else {
//                LogUtils.e(TAG, "--> connectwifi()  status Success");
//            }
//
//            final IntentFilter intentFilter = new IntentFilter(android.net.wifi.WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
//
//            final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    if (!intent.getAction().equals(android.net.wifi.WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
//                        return;
//                    }
//                    // Post connection
//                    LogUtils.e(TAG, "--> connectwifi()  post connection");
//                }
//            };
//            getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
//        }
//
//    }
//
//    private void connectWifi_2(Wifi wifi, String pwd) {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            if (networkCallback != null) {
//                connectivityManager.unregisterNetworkCallback(networkCallback);
//                networkCallback = null;
//                return;
//            }
//
//            uiHandler.postDelayed(() -> {
//
//                WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
//                        .setSsid(wifi.scanResult.SSID)
//                        .setBssid(MacAddress.fromString(wifi.scanResult.BSSID))
//                        .setWpa2Passphrase(pwd)
//                        .build();
//
//                //网络请求
//                NetworkRequest request = new NetworkRequest.Builder()
//                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//                        .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
//                        .setNetworkSpecifier(wifiNetworkSpecifier)
//                        .build();
//                //网络回调处理
//                networkCallback = new ConnectivityManager.NetworkCallback() {
//                    @Override
//                    public void onAvailable(@NonNull Network network) {
//                        super.onAvailable(network);
//                        LogUtils.e(TAG, "--> connectWifi_2 onAvailable()  network=" + network);
////                    connectivityManager.bindProcessToNetwork(network);
//                    }
//
//                    @Override
//                    public void onUnavailable() {
//                        super.onUnavailable();
//                        LogUtils.e(TAG, "--> connectWifi_2 onUnavailable()");
//                    }
//
//                    @Override
//                    public void onLosing(@NonNull Network network, int maxMsToLive) {
//                        super.onLosing(network, maxMsToLive);
//                        LogUtils.e(TAG, "--> connectWifi_2 onLosing()  netWork=" + network);
//                    }
//
//                    @Override
//                    public void onLost(@NonNull Network network) {
//                        super.onLost(network);
//                        LogUtils.e(TAG, "--> connectWifi_2 onLost()  netWork=" + network);
//                    }
//
//                    @Override
//                    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
//                        super.onCapabilitiesChanged(network, networkCapabilities);
//                        LogUtils.e(TAG, "--> connectWifi_2 onCapabilitiesChanged()  netWork=" + network + ",  networkCapabilities=" + networkCapabilities);
//                    }
//
//                    @Override
//                    public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
//                        super.onLinkPropertiesChanged(network, linkProperties);
//                        LogUtils.e(TAG, "--> connectWifi_2 onLinkPropertiesChanged()  netWork=" + network + ",  linkProperties=" + linkProperties);
//                    }
//
//                    @Override
//                    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
//                        super.onBlockedStatusChanged(network, blocked);
//                        LogUtils.e(TAG, "--> connectWifi_2 onBlockedStatusChanged()  netWork=" + network + ",  blocked=" + blocked);
//                    }
//                };
//                //请求连接网络
//                connectivityManager.requestNetwork(request, networkCallback);
//            }, 200);
//        }
//
//    }
}
