package com.project_m1142.app.wifi.ext;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.utils.StringUtils;
import com.project_m1142.app.wifi.ext.dao.WifiEntity;

import java.util.List;
import java.util.Locale;

public class WifiHelper {

    public static final String WEP = "WEP";
    public static final String PSK = "PSK";
    public static final String EAP = "EAP";
    public static final String WPA = "WPA";

    public static WifiManager sWifiManager;

    private static WifiManager getWifiManager(@NonNull Context context) {
        if (sWifiManager == null) {
            sWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        return sWifiManager;
    }

    @Nullable
    public static WifiInfo getConnectionWifiInfo(@NonNull Context context) {
        WifiManager wifiManager = getWifiManager(context);
        return wifiManager.getConnectionInfo();
    }

    public static boolean isWifiConnected(@NonNull Context context, @NonNull WifiEntity wifiEntity) {
        WifiManager wifiManager = getWifiManager(context);
        if (wifiManager.getConnectionInfo() == null) {
            return false;
        }
        String connectedSsid = StringUtils.trimQuote(wifiManager.getConnectionInfo().getSSID());
        return wifiEntity.ssid.equals(connectedSsid);
    }

    public static String getConnectedIpAddress(@NonNull Context context) {
        WifiManager wifiManager = getWifiManager(context);
        if (wifiManager.getConnectionInfo() == null) {
            return "";
        }
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
//        return String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return String.format(Locale.ENGLISH, "%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }

    public static boolean isWifiEncrypt(@NonNull ScanResult scanResult) {
        boolean isEncrypt = false;
        if (scanResult.capabilities.toUpperCase().contains("WPA2-PSK")
                && scanResult.capabilities.toUpperCase().contains("WPA-PSK")) {
            isEncrypt = true;
        } else if (scanResult.capabilities.toUpperCase().contains("WPA-PSK")) {
            isEncrypt = true;
        } else if (scanResult.capabilities.toUpperCase().contains("WPA2-PSK")) {
            isEncrypt = true;
        }
        return isEncrypt;
    }

    public static String getEncryptionType(@NonNull ScanResult scanResult) {
        String encryption = "";
        if (scanResult.capabilities.toUpperCase().contains("WPA2-PSK")
                && scanResult.capabilities.toUpperCase().contains("WPA-PSK")) {
            encryption = "WPA/WPA2";
        } else if (scanResult.capabilities.toUpperCase().contains("WPA-PSK")) {
            encryption = "WPA";
        } else if (scanResult.capabilities.toUpperCase().contains("WPA2-PSK")) {
            encryption = "WPA2";
        }
        return encryption;
    }


    /*==========================================*/

    /**
     * @return 返回 WifiConfiguration 的 networkId
     */
    public static int findNetworkIdOfWifiConfiguration(Context context, WifiEntity wifiEntity, String password) {
        WifiManager wifiManager = getWifiManager(context);
        /*
        Compatibility Note:
        For applications targeting Build.VERSION_CODES.Q or above,
        this API will always fail and return an empty list.
         */
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : configurations) { // 已保存的wifi直接从这里拿得到
//            if (configuration.SSID.equals(wifiEntity.ssid))
            if (wifiEntity.ssid.equals(StringUtils.trimQuote(configuration.SSID)))
                return configuration.networkId;
        }

        WifiConfiguration configuration = createWifiConfiguration(wifiEntity, password);
        return saveWifiConfiguration(context, configuration);
    }

    public static boolean deleteWifiConfiguration(Context context, WifiEntity wifiEntity) {
        WifiManager wifiManager = getWifiManager(context);
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration configuration : configurations) {
            if (wifiEntity.ssid.equals(StringUtils.trimQuote(configuration.SSID))) {
                /*
                Compatibility Note:
                For applications targeting Build.VERSION_CODES.Q or above,
                this API will always fail and return false.
                 */
                boolean ret = wifiManager.removeNetwork(configuration.networkId);
                ret = ret & wifiManager.saveConfiguration();
                return ret;
            }
        }
        return false;
    }

    private static WifiConfiguration createWifiConfiguration(WifiEntity wifiEntity, String password) {
        WifiConfiguration configuration = new WifiConfiguration();
        if (password == null) {
            configuration.hiddenSSID = false;
            configuration.status = WifiConfiguration.Status.ENABLED;
            configuration.SSID = "\"" + StringUtils.trimQuote(wifiEntity.ssid) + "\"";
            if (wifiEntity.capabilities.contains(WEP)) {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.wepTxKeyIndex = 0;
                configuration.wepKeys[0] = "";
            } else if (wifiEntity.capabilities.contains(PSK)) {
                configuration.preSharedKey = "";
            } else if (wifiEntity.capabilities.contains(EAP)) {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                configuration.preSharedKey = "";
            } else {
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.preSharedKey = null;
            }
        } else {
            configuration.allowedAuthAlgorithms.clear();
            configuration.allowedGroupCiphers.clear();
            configuration.allowedKeyManagement.clear();
            configuration.allowedPairwiseCiphers.clear();
            configuration.allowedProtocols.clear();
            configuration.SSID = "\"" + StringUtils.trimQuote(wifiEntity.ssid) + "\"";
            if (wifiEntity.capabilities.contains(WEP)) {
                configuration.preSharedKey = "\"" + password + "\"";
                configuration.hiddenSSID = true;
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.wepTxKeyIndex = 0;
            } else if (wifiEntity.capabilities.contains(WPA)) {
                configuration.hiddenSSID = true;
                configuration.preSharedKey = "\"" + password + "\"";
                configuration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                configuration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                configuration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            } else {
                configuration.wepKeys[0] = "";
                configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                configuration.wepTxKeyIndex = 0;
            }
        }

        return configuration;
    }


    private static int saveWifiConfiguration(Context context, WifiConfiguration configuration) {
        WifiManager wifiManager = getWifiManager(context);
        /*
        Compatibility Note:
        For applications targeting Build.VERSION_CODES.Q or above,
        this API will always fail and return -1.
         */
        int networkId = wifiManager.addNetwork(configuration);
        /*
        This method was deprecated in API level 26.
        There is no need to call this method，因为如下三个方法已经自动保存了
            addNetwork(android.net.wifi.WifiConfiguration),
            updateNetwork(android.net.wifi.WifiConfiguration)
            removeNetwork(int)
         */
        wifiManager.saveConfiguration();
        return networkId;
    }
}
