package com.project_ci01.app.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.permission.PermissionHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * https://developer.android.google.cn/reference/android/net/wifi/WifiInfo
 */

public class MyDeviceUtils {

    private static final String TAG = "MyDeviceUtils";

    private static final String WIFISSID_UNKNOW = "<unknown ssid>";
    private static final String UNKNOW = "unknown";

    private final static int sMaxLevel = 5;

    public static String getDeviceName() {
        return Build.MANUFACTURER + " " + Build.MODEL;
    }

    public static String getSsid(@NonNull Context context) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : connMgr.getAllNetworks()) {

            NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
//            NetworkCapabilities networkCapabilities = connMgr.getNetworkCapabilities(network);

//            LogUtils.e(TAG, "networkInfo=" + networkInfo);
//            LogUtils.e(TAG, "networkInfo.getType()=" + networkInfo.getType());
//            LogUtils.e(TAG, "networkInfo.getTypeName()=" + networkInfo.getTypeName());
//            LogUtils.e(TAG, "networkInfo.getExtraInfo()=" + networkInfo.getExtraInfo());
//            LogUtils.e(TAG, "networkInfo.isConnected()=" + networkInfo.isConnected());

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                LogUtils.e(TAG, "networkCapabilities.getSignalStrength()=" + networkCapabilities.getSignalStrength());
//            }

            if (networkInfo != null && networkInfo.isConnected() && !TextUtils.isEmpty(networkInfo.getExtraInfo())) {
                return networkInfo.getExtraInfo();
            }
        }

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return UNKNOW;
        }
//        LogUtils.e(TAG, "wifiInfo=" + wifiInfo.toString());
//        LogUtils.e(TAG, "SSID=" + wifiInfo.getSSID());
//        LogUtils.e(TAG, "Rssi=" + wifiInfo.getRssi());
//        int signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), sMaxLevel);
//        LogUtils.e(TAG, "signalLevel=" + signalLevel);

        if (!WIFISSID_UNKNOW.equals(wifiInfo.getSSID())) {
            return StringUtils.trimQuote(wifiInfo.getSSID());
        }

        /*
         *  遍历wifi列表来获取
         */
        String ssid = WIFISSID_UNKNOW;
        int networkId = wifiInfo.getNetworkId();
        if (PermissionHelper.checkLocationPermission(context)) { // 需要位置权限
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfiguration : configuredNetworks){
                if (wifiConfiguration.networkId == networkId){
                    ssid = StringUtils.trimQuote(wifiConfiguration.SSID);
                    break;
                }
            }
        }

        return ssid;
    }


    /**
     * https://android.googlesource.com/platform/frameworks/opt/net/wifi/+/c7411ff89358e51e519804d478080355442ad269/libs/WifiTrackerLib/res/values/strings.xml
     * https://developer.android.google.cn/reference/android/net/wifi/ScanResult#WIFI_STANDARD_11BE
     * com.android.wifitrackerlib.Utils#getStandardString
     */
    public static String getWifiStandard(@NonNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return UNKNOW;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            int wifiStandard = wifiInfo.getWifiStandard();
            switch (wifiStandard) {
                case ScanResult.WIFI_STANDARD_LEGACY:
                    return "Legacy";
                case ScanResult.WIFI_STANDARD_11N:
                    return "Wi-Fi 4(802.11n)";
                case ScanResult.WIFI_STANDARD_11AC:
                    return "Wi-Fi 5(802.11ac)";
                case ScanResult.WIFI_STANDARD_11AX:
                    return "Wi-Fi 6(802.11ax)";
                case ScanResult.WIFI_STANDARD_11AD:
                    return "WiGig(802.11ad)";
                case ScanResult.WIFI_STANDARD_11BE:
                    return "Wi-Fi 7(Wi-Fi 802.11be)";
                default:
                    return UNKNOW;
            }
        } else {
            return UNKNOW;
        }
    }

    public static String getPhySpeed(@NonNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null) {
            return UNKNOW;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return "↓ " + wifiInfo.getRxLinkSpeedMbps() + "Mbps ↑ " + wifiInfo.getTxLinkSpeedMbps()+ "Mbps";
        } else {
            return "↓ " + wifiInfo.getLinkSpeed() + "Mbps ↑ " + wifiInfo.getLinkSpeed()+ "Mbps";
        }
    }

    public static String getSignal(@NonNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getRssi() + "dBm";
        } else {
            return UNKNOW;
        }
    }


    public static String getDns(Context context) {
        /**
         * 获取dns
         */
        String[] dnsServers = getDnsFromCommand();
        if (dnsServers.length == 0) {
            dnsServers = getDnsFromConnectionManager(context);
        }
        /**
         * 组装
         */
        StringBuffer sb = new StringBuffer();
        if (dnsServers.length > 0) {
            sb.append(dnsServers[0]); // 可以获取多个DNS，本文只取第一个；读者根据需要可以进行遍历拿取
        }
        return sb.toString();
    }


    //通过 getprop 命令获取
    private static String[] getDnsFromCommand() {
        LinkedList<String> dnsServers = new LinkedList<>();
        try {
            Process process = Runtime.getRuntime().exec("getprop");
            InputStream inputStream = process.getInputStream();
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = lnr.readLine()) != null) {
                int split = line.indexOf("]: [");
                if (split == -1) continue;
                String property = line.substring(1, split);
                String value = line.substring(split + 4, line.length() - 1);
                if (property.endsWith(".dns")
                        || property.endsWith(".dns1")
                        || property.endsWith(".dns2")
                        || property.endsWith(".dns3")
                        || property.endsWith(".dns4")) {
                    InetAddress ip = InetAddress.getByName(value);
                    value = ip.getHostAddress();
                    if (value == null) continue;
                    if (value.length() == 0) continue;
                    dnsServers.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dnsServers.isEmpty() ? new String[0] : dnsServers.toArray(new String[dnsServers.size()]);
    }


    private static String[] getDnsFromConnectionManager(Context context) {
        LinkedList<String> dnsServers = new LinkedList<>();
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null) {
                    for (Network network : connectivityManager.getAllNetworks()) {
                        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
                        if (networkInfo != null && networkInfo.getType() == activeNetworkInfo.getType()) {
                            LinkProperties lp = connectivityManager.getLinkProperties(network);
                            if (lp == null) continue;
                            for (InetAddress addr : lp.getDnsServers()) {
                                dnsServers.add(addr.getHostAddress());
                            }
                        }
                    }
                }
            }
        }
        return dnsServers.isEmpty() ? new String[0] : dnsServers.toArray(new String[0]);
    }
}
