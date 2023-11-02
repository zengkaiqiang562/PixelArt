package com.project_m1142.app.wifi.ext.dao;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_wifi")
public class WifiEntity implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long updateTime; // 更新时间

    public String ssid;
    public String bssid;
    public int level;

    /**
     ScanResult.WIFI_STANDARD_UNKNOWN,
     ScanResult.WIFI_STANDARD_LEGACY,
     ScanResult.WIFI_STANDARD_11N,
     ScanResult.WIFI_STANDARD_11AC,
     ScanResult.WIFI_STANDARD_11AX,
     ScanResult.WIFI_STANDARD_11AD,
     ScanResult.WIFI_STANDARD_11BE
     */
    public int wifiStandard;

    public String password; // wifi 密码（自己保存在数据库）
    public boolean isEncrypt; // 是否加密
    public String encryption; // 加密类型

    public String capabilities;

    public int centerFreq0;
    public int centerFreq1;
    public int frequency;

    public WifiEntity(long id, long updateTime,
                      String ssid, String bssid, int level, int wifiStandard,
                      String password, boolean isEncrypt, String encryption,
                      String capabilities, int centerFreq0, int centerFreq1, int frequency) {
        this.id = id;
        this.updateTime = updateTime;
        this.ssid = ssid;
        this.bssid = bssid;
        this.level = level;
        this.wifiStandard = wifiStandard;
        this.password = password;
        this.isEncrypt = isEncrypt;
        this.encryption = encryption;
        this.capabilities = capabilities;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.frequency = frequency;
    }

    @Ignore
    public WifiEntity(long updateTime, String ssid, String bssid, int level, int wifiStandard,
                      String password, boolean isEncrypt, String encryption,
                      String capabilities, int centerFreq0, int centerFreq1, int frequency) {
        this.updateTime = updateTime;
        this.ssid = ssid;
        this.bssid = bssid;
        this.level = level;
        this.wifiStandard = wifiStandard;
        this.password = password;
        this.isEncrypt = isEncrypt;
        this.encryption = encryption;
        this.capabilities = capabilities;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.frequency = frequency;
    }

    @Ignore
    public WifiEntity() {}

    /**
     * @return 是否该 wifi 是否保存了密码在数据库
     */
    public boolean isSaved() {
        return !TextUtils.isEmpty(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WifiEntity that = (WifiEntity) o;

//        if (wifiStandard != that.wifiStandard) return false;
//        if (isEncrypt != that.isEncrypt) return false;
//        if (level != that.level) return false;
//        if (centerFreq0 != that.centerFreq0) return false;
//        if (centerFreq1 != that.centerFreq1) return false;
//        if (frequency != that.frequency) return false;
//        if (!ssid.equals(that.ssid)) return false;
//        if (!bssid.equals(that.bssid)) return false;
//        if (!password.equals(that.password)) return false;
//        if (!encryption.equals(that.encryption)) return false;
//        return !capabilities.equals(that.capabilities);
        return ssid.equals(that.ssid);
    }

    @Override
    public int hashCode() {
//        int result = ssid.hashCode();
//        result = 31 * result + bssid.hashCode();
//        result = 31 * result + level;
//        result = 31 * result + wifiStandard;
//        result = 31 * result + password.hashCode();
//        result = 31 * result + (isEncrypt ? 1 : 0);
//        result = 31 * result + encryption.hashCode();
//        result = 31 * result + capabilities.hashCode();
//        result = 31 * result + centerFreq0;
//        result = 31 * result + centerFreq1;
//        result = 31 * result + frequency;
//        return result;
        return ssid.hashCode();
    }

    @Override
    public String toString() {
        return "WifiEntity{" +
                "ssid='" + ssid + '\'' +
                ", bssid='" + bssid + '\'' +
                ", level=" + level +
                ", wifiStandard=" + wifiStandard +
                ", password='" + password + '\'' +
                ", isEncrypt=" + isEncrypt +
                ", encryption='" + encryption + '\'' +
                '}';
    }

    public static WifiEntity copy(WifiEntity src) {
        return new WifiEntity(src.updateTime, src.ssid, src.bssid, src.level, src.wifiStandard,
                src.password, src.isEncrypt, src.encryption,
                src.capabilities, src.centerFreq0, src.centerFreq1, src.frequency);
    }


    /*====================== Parcelable ==================*/

    @Ignore
    protected WifiEntity(Parcel in) {
        id = in.readLong();
        updateTime = in.readLong();
        ssid = in.readString();
        bssid = in.readString();
        level = in.readInt();
        wifiStandard = in.readInt();
        password = in.readString();
        isEncrypt = in.readByte() != 0;
        encryption = in.readString();
        capabilities = in.readString();
        centerFreq0 = in.readInt();
        centerFreq1 = in.readInt();
        frequency = in.readInt();
    }

    public static final Creator<WifiEntity> CREATOR = new Creator<WifiEntity>() {
        @Override
        public WifiEntity createFromParcel(Parcel in) {
            return new WifiEntity(in);
        }

        @Override
        public WifiEntity[] newArray(int size) {
            return new WifiEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(updateTime);
        dest.writeString(ssid);
        dest.writeString(bssid);
        dest.writeInt(level);
        dest.writeInt(wifiStandard);
        dest.writeString(password);
        dest.writeByte((byte) (isEncrypt ? 1 : 0));
        dest.writeString(encryption);
        dest.writeString(capabilities);
        dest.writeInt(centerFreq0);
        dest.writeInt(centerFreq1);
        dest.writeInt(frequency);
    }
}
