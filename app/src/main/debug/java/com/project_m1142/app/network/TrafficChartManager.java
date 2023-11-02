package com.project_m1142.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.manage.LifecyclerManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrafficChartManager {
    private static final String TAG = "TrafficChartManager";

    private volatile static TrafficChartManager instance;

    private final List<TrafficRateBean> trafficRateBeans = new ArrayList<>();

    private final TrafficChartHandler trafficChartHandler;

    private TrafficChartManager() {
        trafficChartHandler = new TrafficChartHandler();
    }

    public static TrafficChartManager getInstance() {
        if (instance == null) {
            synchronized (TrafficChartManager.class) {
                if (instance == null) {
                    instance = new TrafficChartManager();
                }
            }
        }
        return instance;
    }

    public void startTraffic() {
        trafficChartHandler.sendRateStatistic(0);
    }

    public void stopTraffic() {
        trafficChartHandler.removeRateStatistic();
    }

    public long avgRxRate() {
        long sum = 0;
        int count = 0;
        for (int i = 0; i < trafficRateBeans.size(); i++) {
            long rxRate = trafficRateBeans.get(i).rxRate;
            if (rxRate > 0) {
                sum += rxRate;
                ++count;
            }
        }
        return  count == 0 ? 0 : Math.round(sum * 1.0f / count);
    }

    public long avgTxRate() {
        long sum = 0;
        int count = 0;
        for (int i = 0; i < trafficRateBeans.size(); i++) {
            long txRate = trafficRateBeans.get(i).txRate;
            if (txRate > 0) {
                sum += txRate;
                ++count;
            }
        }
        return  count == 0 ? 0 : Math.round(sum * 1.0f / count);
    }

    public long maxRxRate() {
        long max = 0;
        for (int i = 0; i < trafficRateBeans.size(); i++) {
            long rxRate = trafficRateBeans.get(i).rxRate;
            max = Math.max(max, rxRate);
        }
        return max;
    }

    public long maxTxRate() {
        long max = 0;
        for (int i = 0; i < trafficRateBeans.size(); i++) {
            long txRate = trafficRateBeans.get(i).txRate;
            max = Math.max(max, txRate);
        }
        return max;
    }

    public long minRxRate() {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < trafficRateBeans.size(); i++) {
            long rx = trafficRateBeans.get(i).rxRate;
            min = Math.min(min, rx);
        }

        if (min == Long.MAX_VALUE) {
            min = 0;
        }
        return min;
    }

    public long minTxRate() {
        long min = Long.MAX_VALUE;
        for (int i = 0; i < trafficRateBeans.size(); i++) {
            long txRate = trafficRateBeans.get(i).txRate;
            min = Math.min(min, txRate);
        }

        if (min == Long.MAX_VALUE) {
            min = 0;
        }
        return min;
    }

    public long lastRxRate() {
        long result = 0;
        if (!trafficRateBeans.isEmpty()) {
            result = trafficRateBeans.get(trafficRateBeans.size() - 1).rxRate;
        }

        return result;
    }

    public long lastTxRate() {
        long result = 0;
        if (!trafficRateBeans.isEmpty()) {
            result = trafficRateBeans.get(trafficRateBeans.size() - 1).txRate;
        }
        return result;
    }

    public List<TrafficRateBean> getTrafficRateBeans() {
        return trafficRateBeans;
    }

    private class TrafficChartHandler extends Handler {
        private static final int WHAT_RATE_STATISTIC = 1001;

        private long oldRx = Long.MIN_VALUE;
        private long oldTx = Long.MIN_VALUE;
        TrafficChartHandler() {
            super(Looper.getMainLooper());
        }

        public void sendRateStatistic(long delay) {
            if (hasMessages(WHAT_RATE_STATISTIC)) {
                removeMessages(WHAT_RATE_STATISTIC);
            }
            sendEmptyMessageDelayed(WHAT_RATE_STATISTIC, delay);
        }

        private void removeRateStatistic() {
            if (hasMessages(WHAT_RATE_STATISTIC)) {
                removeMessages(WHAT_RATE_STATISTIC);
            }
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == WHAT_RATE_STATISTIC) {

                /**
                 * 每 500ms 统计一次
                 */

                if (oldRx == Long.MIN_VALUE || oldTx == Long.MIN_VALUE) { // 第一次记录
                    oldRx = TrafficStats.getTotalRxBytes();
                    oldTx = TrafficStats.getTotalTxBytes();
                    sendRateStatistic(500);
                    return;
                }

                long nowRx = TrafficStats.getTotalRxBytes();
                long rxRate = (nowRx - oldRx) * 2; // rx / s
                oldRx = nowRx;

                long nowTx = TrafficStats.getTotalTxBytes();
                long txRate = (nowTx - oldTx) * 2; // rx / s
                oldTx = nowTx;

//                LogUtils.e(TAG, "--> rxRate=" + rxRate + " txRate=" + txRate);

                trafficRateBeans.add(new TrafficRateBean(System.currentTimeMillis(), rxRate * 8, txRate * 8)); // 乘以 8 表示 bit

                handleRateStatistic();


//                ConnectivityManager cm = (ConnectivityManager) LifecyclerManager.INSTANCE.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
//                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                //should check null because in airplane mode it will be null
//                NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
//                int downSpeed = nc.getLinkDownstreamBandwidthKbps();
//                int upSpeed = nc.getLinkUpstreamBandwidthKbps();


//                LogUtils.e(TAG, "--> downSpeed=" + downSpeed + " upSpeed=" + upSpeed);


                sendRateStatistic(500);
            }
        }

        private void handleRateStatistic() {
            int size = trafficRateBeans.size();
            if (size < 2) {
                return;
            }

            TrafficRateBean lastRateBean = trafficRateBeans.get(size - 2);
            TrafficRateBean newRateBean = trafficRateBeans.get(size - 1);

            Calendar lastCalendar = Calendar.getInstance();
            lastCalendar.setTimeInMillis(lastRateBean.time);

            Calendar newCalendar = Calendar.getInstance();
            newCalendar.setTimeInMillis(newRateBean.time);

            int lastDay = lastCalendar.get(Calendar.DAY_OF_MONTH);
            int newDay = newCalendar.get(Calendar.DAY_OF_MONTH);

//            LogUtils.e(TAG, "--> lastDay=" + lastDay + " newDay=" + newDay);

            if (lastDay != newDay) { // 不是同一天重新计算
                trafficRateBeans.clear();
                trafficRateBeans.add(newRateBean);
            } else if (size > 10) { // 5s 内采样
                trafficRateBeans.remove(0);
            }
        }
    }
}
