package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// import com.library.ssr.bg.BaseService;
// import com.library.ssr.ext.SsrManager;
import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.utils.MyTimeUtils;
import com.project_m1142.app.databinding.ActivityConnectedReportBinding;
import com.project_m1142.app.network.TrafficChartManager;
//import com.project_m1142.app.network.TrafficManager;

public class ConnectedReportActivity extends SsrActivity/*  implements SsrManager.OnConnectedTimerListener */ {

    private static final int WHAT_UPDATE_TRAFFIC = 1001;

    ActivityConnectedReportBinding binding;

    @Override
    protected String tag() {
        return "ConnectedReportActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityConnectedReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SsrManager.getInstance().addOnConnectedTimerListener(this);
        init(getIntent());

        EventTracker.trackReportShow();
        EventTracker.trackConnectedReportShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        binding.reportBack.setOnClickListener(v -> {
            back();
        });

        updateTrafficView();
        sendUpdateTraffic(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SsrManager.getInstance().removeOnConnectedTimerListener(this);
    }

//     @Override
//     protected void changeState(BaseService.State state, @Nullable String msg, Boolean animate, boolean fromListener) {
//         BaseService.State lastState = this.state;
//         Log.e(TAG, "--> changeState()  lastState=" + lastState + "  state=" + state
//                 + ", msg=" + msg + ", animate=" + animate + ", fromListener=" + fromListener);

//         if (state == BaseService.State.Connected) {
//             binding.reportIcon.setImageResource(R.drawable.report_success);
//             binding.reportText.setText(R.string.connect_success);
//         } else {
//             // 更新状态
//             switch (state) {
//                 case Connecting:
//                     break;
//                 case Stopping:
//                     break;
//                 default: // Idle / Stopped
//                     binding.reportIcon.setImageResource(R.drawable.report_failed);
//                     binding.reportText.setText(R.string.connect_failed);
//                     removeUpdateTraffic();
//                     break;
//             }
//         }

//         this.state = state;
//     }

//     @Override
//     protected void updateTraffic(long txRate, long rxRate, long txTotal, long rxTotal) {
// //        String fitTxRate = Formatter.formatFileSize(this, txRate) + "/S";
// //        String fitRxRate = Formatter.formatFileSize(this, rxRate) + "/S";
// //        Log.e(TAG, "--> updateTraffic()  " +
// //                "txTotal=" + Formatter.formatFileSize(this, txTotal) +
// //                ", rxTotal=" + Formatter.formatFileSize(this, rxTotal) +
// //                ", txRate=" + fitTxRate +
// //                ", rxRate=" + fitRxRate
// //        );
// //
// //        binding.reportUploadValue.setText(fitTxRate);
// //        binding.reportDownloadValue.setText(fitRxRate);
//     }

//     @Override
//     public void onDurationChanged(long duration) {
//         String fitDuration = MyTimeUtils.millis2StringGMT(duration, "HH:mm:ss");
//         binding.reportDurationValue.setText(fitDuration);
//     }

    /*======================================*/

    private void updateTrafficView() {
//        long avgRxRate = TrafficManager.getInstance().avgRxRate();
//        long avgTxRate = TrafficManager.getInstance().avgTxRate();
        long avgRxRate = TrafficChartManager.getInstance().avgRxRate();
        long avgTxRate = TrafficChartManager.getInstance().avgTxRate();
        String fitTxRate = Formatter.formatFileSize(this, avgTxRate) + "/S";
        String fitRxRate = Formatter.formatFileSize(this, avgRxRate) + "/S";
        if (TextUtils.isEmpty(fitTxRate)) fitTxRate = "--";
        if (TextUtils.isEmpty(fitRxRate)) fitRxRate = "--";
        binding.reportUploadValue.setText(fitTxRate.toUpperCase());
        binding.reportDownloadValue.setText(fitRxRate.toUpperCase());
    }

    private void sendUpdateTraffic(boolean fromUser) {
        if (uiHandler.hasMessages(WHAT_UPDATE_TRAFFIC)) {
            uiHandler.removeMessages(WHAT_UPDATE_TRAFFIC);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_UPDATE_TRAFFIC, fromUser ? 0 : 500L);
    }

    private void removeUpdateTraffic() {
        if (uiHandler.hasMessages(WHAT_UPDATE_TRAFFIC)) {
            uiHandler.removeMessages(WHAT_UPDATE_TRAFFIC);
        }
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_UPDATE_TRAFFIC) {
            updateTrafficView();
            sendUpdateTraffic(false);
        }
    }

}
