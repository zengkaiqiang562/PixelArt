package com.project_m1142.app.dao;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.blankj.utilcode.util.TimeUtils;

@Entity(tableName = "table_test_history")
public class TestHistoryEntity implements Parcelable {

    @Ignore
    private static final String TAG = "DrinkEntity";

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long createTime;

    public String device;

    public long delay;

    public long rxRate; // b/s

    public long txRate; // b/s

    public String netName; // wifi 名称

    public String netMode; // wifi 协议，如：802.11ac

    public String netSpeed; // ↓ 20Mbps ↑ 10Mbps

    public String signal;

    public String dns;

    public TestHistoryEntity(long id, long createTime, String device, long delay, long rxRate, long txRate, String netName, String netMode, String netSpeed, String signal, String dns) {
        this.id = id;
        this.createTime = createTime;
        this.device = device;
        this.delay = delay;
        this.rxRate = rxRate;
        this.txRate = txRate;
        this.netName = netName;
        this.netMode = netMode;
        this.netSpeed = netSpeed;
        this.signal = signal;
        this.dns = dns;
    }

    @Ignore
    public TestHistoryEntity(long createTime, String device, long delay, long rxRate, long txRate, String netName, String netMode, String netSpeed, String signal, String dns) {
        this.createTime = createTime;
        this.device = device;
        this.delay = delay;
        this.rxRate = rxRate;
        this.txRate = txRate;
        this.netName = netName;
        this.netMode = netMode;
        this.netSpeed = netSpeed;
        this.signal = signal;
        this.dns = dns;
    }

    @Ignore
    public TestHistoryEntity() {
    }

    @Ignore
    protected TestHistoryEntity(Parcel in) {
        id = in.readLong();
        createTime = in.readLong();
        device = in.readString();
        delay = in.readLong();
        rxRate = in.readLong();
        txRate = in.readLong();
        netName = in.readString();
        netMode = in.readString();
        netSpeed = in.readString();
        signal = in.readString();
        dns = in.readString();
    }

    public static final Creator<TestHistoryEntity> CREATOR = new Creator<TestHistoryEntity>() {
        @Override
        public TestHistoryEntity createFromParcel(Parcel in) {
            return new TestHistoryEntity(in);
        }

        @Override
        public TestHistoryEntity[] newArray(int size) {
            return new TestHistoryEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(createTime);
        dest.writeString(device);
        dest.writeLong(delay);
        dest.writeLong(rxRate);
        dest.writeLong(txRate);
        dest.writeString(netName);
        dest.writeString(netMode);
        dest.writeString(netSpeed);
        dest.writeString(signal);
        dest.writeString(dns);
    }

    @Override
    public String toString() {
        return "TestHistoryEntity{" +
                "id=" + id +
                ", createTime=" + TimeUtils.millis2String(createTime, "yyyy/MM/dd HH:mm:ss") +
                ", device='" + device + '\'' +
                ", delay=" + delay +
                ", rxRate=" + rxRate +
                ", txRate=" + txRate +
                ", netName='" + netName + '\'' +
                ", netMode='" + netMode + '\'' +
                ", netSpeed='" + netSpeed + '\'' +
                ", signal='" + signal + '\'' +
                ", dns='" + dns + '\'' +
                '}';
    }
}
