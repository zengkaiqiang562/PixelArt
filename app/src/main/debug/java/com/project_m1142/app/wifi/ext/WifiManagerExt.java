package com.project_m1142.app.wifi.ext;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.common.CompleteCallback;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.LifecyclerManager;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.wifi.ext.dao.WifiDbManager;
import com.project_m1142.app.wifi.ext.dao.WifiEntity;

import java.util.ArrayList;
import java.util.List;

public class WifiManagerExt {
    private static final String TAG = "WifiManagerExt";

    private volatile static WifiManagerExt instance;

    private final Context context;
    private final WifiManager wifiManager;
    private WifiReceiver wifiReceiver;

    private final List<WifiEntity> wifiEntities = new ArrayList<>();

    private final List<WifiEntity> ignoreWifiEntities = new ArrayList<>(); // 忽略wifi列表

    private final List<OnWifiStateChangeListener> onWifiStateChangeListeners = new ArrayList<>();
    private final List<OnNetworkStateChangeListener> onNetworkStateChangeListeners = new ArrayList<>();
    private final List<OnWifiListChangedListener> onWifiListChangedListeners = new ArrayList<>();

    private final MainHandler mainHandler;

    private WifiEntity connectingWifi; // 正在连接的wifi

    private WifiManagerExt() {
        mainHandler = new MainHandler();
        context = LifecyclerManager.INSTANCE.getApplication();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiManagerExt getInstance() {
        if (instance == null) {
            synchronized (WifiManagerExt.class) {
                if (instance == null) {
                    instance = new WifiManagerExt();
                }
            }
        }
        return instance;
    }

    public void registerWifiReceiver() {
        wifiReceiver = new WifiReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(wifiReceiver, filter);
    }

    public void unregisterWifiReceiver() {
        if (wifiReceiver != null) {
            context.unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
    }

    public boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }


    public boolean enableWifi() {
        if (wifiManager.isWifiEnabled()) {
            return true;
        } else {
            /*
            Compatibility Note:
            For applications targeting Build.VERSION_CODES.Q or above, this API will always fail and return false.
            If apps are targeting an older SDK (Build.VERSION_CODES.P or below), they can continue to use this API.
             */
            return wifiManager.setWifiEnabled(true); // 返回 false 表示请求打开wifi 失败，true表示请求成功，但wifi可能还没打开完成
        }
    }

    public boolean disableWifi() {
        if (!wifiManager.isWifiEnabled()) {
            return false;
        } else {
            /*
            Compatibility Note:
            For applications targeting Build.VERSION_CODES.Q or above, this API will always fail and return false.
            If apps are targeting an older SDK (Build.VERSION_CODES.P or below), they can continue to use this API.
             */
            return wifiManager.setWifiEnabled(false); // 返回 false 表示请求关闭wifi 失败，true表示请求成功，但wifi可能还没关闭完成
        }
    }

    public void scanWifi(BaseActivity baseActivity) {
        // 先获取位置服务权限
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsProviderEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        LogUtils.e(TAG, "--> scanWifi()  gpsProviderEnable=" + gpsProviderEnable + "  networkProviderEnable=" + networkProviderEnable);
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//                // 定位服务已打开，继续下一步操作
//            } else {
//                // 定位服务未打开，提示用户打开
//            }
        if (!gpsProviderEnable && !networkProviderEnable) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            baseActivity.startActivity(intent);
            baseActivity.setSkipStartLoading(true);
            return;
        }
        boolean result = wifiManager.startScan();
        LogUtils.e(TAG, "--> scanWifi()  result=" + result);
    }

    public boolean disConnectWifi() {
        /*
        Compatibility Note:
        For applications targeting Build.VERSION_CODES.Q or above,
        this API will always fail and return false.
         */
        return wifiManager.disconnect();
    }

    public boolean connectOpenWifi(WifiEntity wifiEntity) {
        return connectEncryptWifi(wifiEntity, null);
    }

    public boolean connectEncryptWifi(WifiEntity wifiEntity, String password) {
        connectingWifi = null; // reset
        if (WifiHelper.isWifiConnected(context, wifiEntity)) {
            return true;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
//            connectWifiByNetworkRequest(wifiEntity, password);
//            notifyNetworkStateChange(NetworkState.START_CONNECT);
//            connectingWifi = WifiEntity.copy(wifiEntity);
//            connectingWifi.password = password;
            // TODO 直接跳设置页
            Intent settingsIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
            Activity topActivity = ContextManager.INSTANCE.topActivity();
            if (topActivity instanceof BaseActivity) {
                topActivity.startActivityForResult(settingsIntent, IntentConstants.REQUEST_CODE_WIFI_SETTING);
                ((BaseActivity) topActivity).setSkipStartLoading(true); // 跳过启动加载流程
                connectingWifi = WifiEntity.copy(wifiEntity);
                connectingWifi.password = password;
            }
            return true;
        } else { // Android 10 以下
            int networkId = WifiHelper.findNetworkIdOfWifiConfiguration(context, wifiEntity, password);
            /*
            Compatibility Note:
            For applications targeting Build.VERSION_CODES.Q or above,
            this API will always fail and return false.
             */
            boolean ret = wifiManager.enableNetwork(networkId, true);
            if (ret) {
                notifyNetworkStateChange(NetworkState.START_CONNECT);
                connectingWifi = WifiEntity.copy(wifiEntity);
                connectingWifi.password = password;
            }
            return ret;
        }
    }

    public boolean connectSavedWifi(WifiEntity wifiEntity) {
        return connectEncryptWifi(wifiEntity, wifiEntity.password);
    }

    public boolean removeWifi(WifiEntity wifiEntity) {
        boolean ret = WifiHelper.deleteWifiConfiguration(context, wifiEntity);
        updateWifiList(false);
        return ret;
    }

    public List<WifiEntity> getWifiEntities() {
        return wifiEntities;
    }

    public void addIgnoreWifi(@NonNull WifiEntity wifiEntity) {
        if (!ignoreWifiEntities.contains(wifiEntity)) {
            ignoreWifiEntities.add(wifiEntity);
        }
        updateWifiList(true);
    }

    public void addOnWifiStateChangeListener(@NonNull OnWifiStateChangeListener listener) {
        if (!onWifiStateChangeListeners.contains(listener)) {
            onWifiStateChangeListeners.add(listener);
        }
    }

    public void removeOnWifiStateChangeListener(OnWifiStateChangeListener listener) {
        onWifiStateChangeListeners.remove(listener);
    }

    private void notifyWifiStateChange(int wifiState) {
        connectingWifi = null; // wifi功能的开关状态变化时，重置正在连接的wifi
        for (OnWifiStateChangeListener listener : onWifiStateChangeListeners) {
            listener.onWifiStateChanged(wifiState);
        }
    }

    public void addOnWifiListChangedListener(@NonNull OnWifiListChangedListener listener) {
        if (!onWifiListChangedListeners.contains(listener)) {
            onWifiListChangedListeners.add(listener);
        }
    }

    public void removeOnWifiListChangedListener(OnWifiListChangedListener listener) {
        onWifiListChangedListeners.remove(listener);
    }

    private void notifyWifiListChange(@NonNull List<WifiEntity> wifiEntities) {
        for (OnWifiListChangedListener listener : onWifiListChangedListeners) {
            listener.onWifiListChanged(wifiEntities);
        }
    }

    public void addOnNetworkStateChangeListener(@NonNull OnNetworkStateChangeListener listener) {
        if (!onNetworkStateChangeListeners.contains(listener)) {
            onNetworkStateChangeListeners.add(listener);
        }
    }

    public void removeOnNetworkStateChangeListener(OnNetworkStateChangeListener listener) {
        onNetworkStateChangeListeners.remove(listener);
    }

    private void notifyNetworkStateChange(@NonNull NetworkState networkState) {
        if (connectingWifi != null && networkState == NetworkState.CONNECTED) {
            if (WifiHelper.isWifiConnected(context, connectingWifi)) {
                int index = wifiEntities.indexOf(connectingWifi);
                if (index != -1) {
                    WifiEntity wifiEntity = wifiEntities.get(index);
                    wifiEntity.password = connectingWifi.password; // 密码保存在内存的集合中
                    WifiDbManager.getInstance().updateWifi(connectingWifi); // 密码更新到数据库
                }
                updateWifiList(true);
            } else {
                networkState = NetworkState.FAILED; // 连接到其他wifi了
            }

        }
        for (OnNetworkStateChangeListener listener : onNetworkStateChangeListeners) {
            listener.onNetworkStateChanged(networkState);
        }
    }

    private void queryLocalWifiEntities(@NonNull CompleteCallback<List<WifiEntity>> callback) {
        if (!wifiEntities.isEmpty()) {
            callback.onCompleted(wifiEntities);
            return;
        }
        WifiDbManager.getInstance().queryAll(entities -> {
            wifiEntities.clear();
            if (entities != null && !entities.isEmpty()) {
                wifiEntities.addAll(entities);
            }
            callback.onCompleted(wifiEntities);
        });
    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mainHandler == null || intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }

            String action = intent.getAction();
            LogUtils.e(TAG, "--> WifiReceiver onReceive()  action=" + action);
            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:  // android.net.wifi.WIFI_STATE_CHANGED
                    mainHandler.sendWifiStateChanged(intent);
                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:  // android.net.wifi.SCAN_RESULTS
                    mainHandler.sendScanResultsAvailable(intent);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:  // android.net.wifi.STATE_CHANGE
                    mainHandler.sendNetworkStateChanged(intent);
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:  // android.net.wifi.supplicant.STATE_CHANGE
                    mainHandler.sendSupplicantStateChanged(intent);
                    break;
            }
        }
    }

    private class MainHandler extends Handler {

        static final int WHAT_WIFI_STATE_CHANGED = 5001;
        static final int WHAT_SCAN_RESULTS_AVAILABLE = 5002;
        static final int WHAT_NETWORK_STATE_CHANGED = 5003;
        static final int WHAT_SUPPLICANT_STATE_CHANGED = 5004;

        MainHandler() {
            super(Looper.getMainLooper());
        }

        private void sendWifiStateChanged(@NonNull Intent intent) {
            if (hasMessages(WHAT_WIFI_STATE_CHANGED)) {
                removeMessages(WHAT_WIFI_STATE_CHANGED);
            }
            Message message = obtainMessage();
            message.what = WHAT_WIFI_STATE_CHANGED;
            message.obj = intent;
            sendMessage(message);
        }

        private void sendScanResultsAvailable(@NonNull Intent intent) {
            if (hasMessages(WHAT_SCAN_RESULTS_AVAILABLE)) {
                removeMessages(WHAT_SCAN_RESULTS_AVAILABLE);
            }
            Message message = obtainMessage();
            message.what = WHAT_SCAN_RESULTS_AVAILABLE;
            message.obj = intent;
            sendMessage(message);
        }

        private void sendNetworkStateChanged(@NonNull Intent intent) {
            if (hasMessages(WHAT_NETWORK_STATE_CHANGED)) {
                removeMessages(WHAT_NETWORK_STATE_CHANGED);
            }
            Message message = obtainMessage();
            message.what = WHAT_NETWORK_STATE_CHANGED;
            message.obj = intent;
            sendMessage(message);
        }

        private void sendSupplicantStateChanged(@NonNull Intent intent) {
            if (hasMessages(WHAT_SUPPLICANT_STATE_CHANGED)) {
                removeMessages(WHAT_SUPPLICANT_STATE_CHANGED);
            }
            Message message = obtainMessage();
            message.what = WHAT_SUPPLICANT_STATE_CHANGED;
            message.obj = intent;
            sendMessage(message);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (!(msg.obj instanceof Intent)) return;
            switch (msg.what) {
                case WHAT_WIFI_STATE_CHANGED:
                    handleWifiStateChanged((Intent) msg.obj);
                    break;
                case WHAT_SCAN_RESULTS_AVAILABLE:
                    handleScanResultsAvailable((Intent) msg.obj);
                    break;
                case WHAT_NETWORK_STATE_CHANGED:
                    handleNetworkStateChanged((Intent) msg.obj);
                    break;
                case WHAT_SUPPLICANT_STATE_CHANGED:
                    handleSupplicantStateChanged((Intent) msg.obj);
                    break;
            }
        }

        private void handleWifiStateChanged(@NonNull Intent intent) {
            /*
            WIFI_STATE_DISABLED = 1;
            WIFI_STATE_DISABLING = 0;
            WIFI_STATE_ENABLED = 3;
            WIFI_STATE_ENABLING = 2;
            WIFI_STATE_UNKNOWN = 4;
             */
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            LogUtils.e(TAG, "--> handleWifiStateChanged()  wifiState=" + wifiState);
            notifyWifiStateChange(wifiState);
        }


        private void handleScanResultsAvailable(@NonNull Intent intent) {
            boolean isUpdated = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            LogUtils.e(TAG, "--> handleScanResultsAvailable()  isUpdated=" + isUpdated);
            if (!isUpdated) {
                return;
            }
            updateWifiList(false);
        }

        private void handleNetworkStateChanged(@NonNull Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo == null) {
                LogUtils.e(TAG, "--> handleNetworkStateChanged()  networkInfo == null !!!");
                return;
            }
            NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
            String ssid = networkInfo.getExtraInfo();
            LogUtils.e(TAG, "--> handleNetworkStateChanged()  ssid=" + ssid + ",  detailedState=" + detailedState);
//            if (TextUtils.isEmpty(ssid)) return;
            NetworkState networkState = null;
            switch (detailedState) {
                case IDLE:
                    networkState = NetworkState.IDLE;
                    break;
                case SCANNING:
                    networkState = NetworkState.SCANNING;
                    break;
                case AUTHENTICATING:
                    networkState = NetworkState.AUTHENTICATING;
                    break;
                case OBTAINING_IPADDR:
                    networkState = NetworkState.OBTAINING_IPADDR;
                    break;
                case CONNECTED:
                    networkState = NetworkState.CONNECTED;
                    break;
                case SUSPENDED:
                    networkState = NetworkState.SUSPENDED;
                    break;
                case DISCONNECTING:
                    networkState = NetworkState.DISCONNECTING;
                    break;
                case DISCONNECTED:
                    networkState = NetworkState.DISCONNECTED;
                    break;
                case FAILED:
                    networkState = NetworkState.FAILED;
                    break;
                case BLOCKED:
                    networkState = NetworkState.BLOCKED;
                    break;
                case VERIFYING_POOR_LINK:
                    networkState = NetworkState.VERIFYING_POOR_LINK;
                    break;
                case CAPTIVE_PORTAL_CHECK:
                    networkState = NetworkState.CAPTIVE_PORTAL_CHECK;
                    break;
            }

            if (networkState != null) {
                notifyNetworkStateChange(networkState);
            }
        }

        private void handleSupplicantStateChanged(@NonNull Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo == null) {
                LogUtils.e(TAG, "--> handleSupplicantStateChanged()  networkInfo == null !!!");
                return;
            }
            NetworkInfo.DetailedState detailedState = networkInfo.getDetailedState();
            String ssid = networkInfo.getExtraInfo();
            LogUtils.e(TAG, "--> handleSupplicantStateChanged  ssid=" + ssid + "  detailedState=" + detailedState);
            if (TextUtils.isEmpty(ssid)) return;
            int errorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
            NetworkState networkState;
            if (errorCode == WifiManager.ERROR_AUTHENTICATING) {
                networkState = NetworkState.SUPPLICANT_ERROR_AUTHENTICATING;
            } else {
                networkState = NetworkState.SUPPLICANT_ERROR_OTHER;
            }

            notifyNetworkStateChange(networkState);
        }
    }

    public void updateWifiList(boolean onlyLocal) { // true，只更新缓存的，不从 scanResults 获取
        queryLocalWifiEntities(localWifiEntities -> {

            if (onlyLocal) { // 只更新缓存的话，只需要考虑是否去掉已忽略的wifi
                for (int index = localWifiEntities.size() - 1; index >= 0; index--) {
                    WifiEntity localWifiEntity = localWifiEntities.get(index);
                    if (ignoreWifiEntities.contains(localWifiEntity)) { // 参考 WifiEntity.equals 方法，仅根据 ssid 判断是否相同
                        localWifiEntities.remove(index);
                        WifiDbManager.getInstance().deleteBySsid(localWifiEntity.ssid);
                    }
                }
                notifyWifiListChange(localWifiEntities);
                return;
            }

            List<ScanResult> scanResults = wifiManager.getScanResults();
            LogUtils.e(TAG, "--> updateWifiList()  scanResults.size=" + scanResults.size());
            if (scanResults.isEmpty()) {
                return;
            }

            List<WifiEntity> newWifiEntities = new ArrayList<>();
            boolean isEncrypt;
            String encryption;
            int wifiStandard;
            for (ScanResult scanResult : scanResults) {
                if (TextUtils.isEmpty(scanResult.SSID)) {
                    continue;
                }
                isEncrypt = WifiHelper.isWifiEncrypt(scanResult);
                encryption = WifiHelper.getEncryptionType(scanResult);

                wifiStandard = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    wifiStandard = scanResult.getWifiStandard();
                }

                newWifiEntities.add(new WifiEntity(System.currentTimeMillis(), scanResult.SSID, scanResult.BSSID, scanResult.level, wifiStandard,
                        null, isEncrypt, encryption,
                        scanResult.capabilities, scanResult.centerFreq0, scanResult.centerFreq1, scanResult.frequency));
            }

            // localWifiEntities 和 newWifiEntities 的差异对比更新
            // 1. 先删除 localWifiEntities 中的过时的wifi，（即 newWifiEntities 中不存在的）
            // 执行完后， localWifiEntities 是 newWifiEntities 的子集
            for (int index = localWifiEntities.size() - 1; index >= 0; index--) {
                WifiEntity localWifiEntity = localWifiEntities.get(index);
                if (!newWifiEntities.contains(localWifiEntity)) { // 参考 WifiEntity.equals 方法，仅根据 ssid 判断是否相同
                    localWifiEntities.remove(index);
//                    WifiDbManager.getInstance().deleteBySsid(localWifiEntity.ssid); // newWifiEntities 中可能没有把所有的wifi都扫描到，所以还是不从数据库中删除，除非用户自己忽略
                }
            }

            // 2. 再将 newWifiEntities 中新增的wifi 添加到 localWifiEntities 中，
            //  或更新 localWifiEntities 已存在的wifi
            for (WifiEntity newWifiEntity : newWifiEntities) {
                if (localWifiEntities.contains(newWifiEntity)) { // 包含则更新（不需要更新密码）
                    int index = localWifiEntities.indexOf(newWifiEntity);
                    if (index == -1) continue;
                    WifiEntity localWifiEntity = localWifiEntities.get(index);
                    localWifiEntity.updateTime = newWifiEntity.updateTime;
//                        localWifiEntity.ssid = newWifiEntity.ssid; // 判断相同的依据，肯定一致，不需要更新
                    localWifiEntity.bssid = newWifiEntity.bssid;
                    localWifiEntity.level = newWifiEntity.level;
                    localWifiEntity.wifiStandard = newWifiEntity.wifiStandard;
//                        localWifiEntity.password = newWifiEntity.password;
                    localWifiEntity.isEncrypt = newWifiEntity.isEncrypt;
                    localWifiEntity.encryption = newWifiEntity.encryption;
                    localWifiEntity.capabilities = newWifiEntity.capabilities;
                    localWifiEntity.centerFreq0 = newWifiEntity.centerFreq0;
                    localWifiEntity.centerFreq1 = newWifiEntity.centerFreq1;
                    localWifiEntity.frequency = newWifiEntity.frequency;
                    WifiDbManager.getInstance().updateWifi(localWifiEntity);

                } else { // 不包含则直接添加
                    localWifiEntities.add(newWifiEntity);
                    WifiDbManager.getInstance().addWifi(newWifiEntity);
                }
            }

            // 执行到这里 newWifiEntities 中的wifi 已经全部更新或添加到 localWifiEntities 中了。
            // 然后去掉已忽略的wifi
            for (int index = localWifiEntities.size() - 1; index >= 0; index--) {
                WifiEntity localWifiEntity = localWifiEntities.get(index);
                if (ignoreWifiEntities.contains(localWifiEntity)) { // 参考 WifiEntity.equals 方法，仅根据 ssid 判断是否相同
                    localWifiEntities.remove(index);
                    WifiDbManager.getInstance().deleteBySsid(localWifiEntity.ssid);
                }
            }
            notifyWifiListChange(localWifiEntities);
        });
    }

    public interface OnWifiStateChangeListener {
        /**
         * @param wifiState
         *        WifiManager.WIFI_STATE_DISABLED = 1;
         *        WifiManager.WIFI_STATE_DISABLING = 0;
         *        WifiManager.WIFI_STATE_ENABLED = 3;
         *        WifiManager.WIFI_STATE_ENABLING = 2;
         *        WifiManager.WIFI_STATE_UNKNOWN = 4;
         */
        void onWifiStateChanged(int wifiState);
    }

    public interface OnNetworkStateChangeListener {
        void onNetworkStateChanged(@NonNull NetworkState networkState);
    }

    public interface OnWifiListChangedListener {
        void onWifiListChanged(@NonNull List<WifiEntity> wifiEntities);
    }


    /*===================== Android 10 及以上的 wifi 连接方式 =====================*/
    private void connectWifiBySuggestion(WifiEntity wifiEntity, String password) {
        WifiNetworkSuggestion suggestion = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            suggestion = new WifiNetworkSuggestion.Builder()
                    .setSsid(wifiEntity.ssid)
                    .setBssid(MacAddress.fromString(wifiEntity.bssid))
                    .setWpa2Passphrase(password)
                    .setIsAppInteractionRequired(true) // Optional (Needs location permission)
                    .build();

            List<WifiNetworkSuggestion> suggestionsList = new ArrayList<>();
            suggestionsList.add(suggestion);

            int status = wifiManager.addNetworkSuggestions(suggestionsList);

            if (status != android.net.wifi.WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                LogUtils.e(TAG, "--> connectWifiBySuggestion()  status Failed");
            } else {
                LogUtils.e(TAG, "--> connectWifiBySuggestion()  status Success");
            }

            final IntentFilter intentFilter = new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
            SuggestionWifiReceiver suggestionWifiReceiver = new SuggestionWifiReceiver();
            context.registerReceiver(suggestionWifiReceiver, intentFilter);
        }
    }

    private class SuggestionWifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mainHandler == null || intent == null || TextUtils.isEmpty(intent.getAction())) {
                return;
            }

            String action = intent.getAction();
            LogUtils.e(TAG, "--> SuggestionWifiReceiver onReceive()  action=" + action);
            if (action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                LogUtils.e(TAG, "--> connectWifiBySuggestion()  post connection");
            }
        }
    }

    private ConnectivityManager.NetworkCallback networkCallback;
    private void connectWifiByNetworkRequest(WifiEntity wifiEntity, String password) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                networkCallback = null;
//                return;
            }

            mainHandler.postDelayed(() -> {

                WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
                builder.setSsid(wifiEntity.ssid);
                builder.setBssid(MacAddress.fromString(wifiEntity.bssid));
                if (TextUtils.isEmpty(password)) {
                    builder.setIsEnhancedOpen(true);
                } else {
                    builder.setWpa2Passphrase(password);
                }

                WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

                //网络请求
                NetworkRequest request = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                        .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
//                        .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                        .setNetworkSpecifier(wifiNetworkSpecifier)
                        .build();

                //网络回调处理
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onAvailable()  network=" + network);
                        boolean result = connectivityManager.bindProcessToNetwork(network);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest bindProcessToNetwork()  result=" + result);
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onUnavailable()");
                    }

                    @Override
                    public void onLosing(@NonNull Network network, int maxMsToLive) {
                        super.onLosing(network, maxMsToLive);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onLosing()  netWork=" + network);
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        super.onLost(network);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onLost()  netWork=" + network);
                    }

                    @Override
                    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                        super.onCapabilitiesChanged(network, networkCapabilities);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onCapabilitiesChanged()  netWork=" + network + ",  networkCapabilities=" + networkCapabilities);
                    }

                    @Override
                    public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                        super.onLinkPropertiesChanged(network, linkProperties);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onLinkPropertiesChanged()  netWork=" + network + ",  linkProperties=" + linkProperties);
                    }

                    @Override
                    public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
                        super.onBlockedStatusChanged(network, blocked);
                        LogUtils.e(TAG, "--> connectWifiByNetworkRequest onBlockedStatusChanged()  netWork=" + network + ",  blocked=" + blocked);
                    }
                };
                //请求连接网络
                /*
                适用于需要连接到对等设备的应用，例如在配置 IoT 设备或将文件传输到相机时。
                在这种情况下，对等设备会启动 SoftAP，并且该 API 会允许应用引导用户连接到该设备。
                生成的网络并非用于提供互联网访问权限，也无法供系统使用，也无法供除配置应用之外的任何应用使用。
                 */
                connectivityManager.requestNetwork(request, networkCallback);
            }, 200);
        }
    }
}
