package com.project_m1142.app.network;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.TimeUtils;

public class TrafficRateBean {
    public final long time;
    public final long rxRate;
    public final long txRate;

    public TrafficRateBean(long time, long rxRate, long txRate) {
        this.time = time;
        this.rxRate = rxRate;
        this.txRate = txRate;
    }


    @Override
    public String toString() {
        String fitRxRate = ConvertUtils.byte2FitMemorySize(rxRate);
        String fitTxRate = ConvertUtils.byte2FitMemorySize(txRate);

        return "TrafficBean{" +
                "time=" + TimeUtils.millis2String(time, "HH:mm:ss") +
                ", rxRate=" + fitRxRate +
                ", txRate=" + fitTxRate +
                '}';
    }
}
