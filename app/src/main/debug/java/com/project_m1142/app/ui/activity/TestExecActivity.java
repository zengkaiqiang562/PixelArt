package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.net.NetTestExecutor;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.utils.MyConvertUtils;
import com.project_m1142.app.base.utils.MyDeviceUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.dao.TestHistoryDbManager;
import com.project_m1142.app.dao.TestHistoryEntity;
import com.project_m1142.app.databinding.ActivityTestExecBinding;
import com.project_m1142.app.network.TrafficChartManager;
import com.project_m1142.app.ping.NormalPingCallback;
import com.project_m1142.app.ping.NormalPingCommand;
import com.project_m1142.app.ping.NormalPingExecutor;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.ui.helper.TestExecChartHelper;

public class TestExecActivity extends BaseActivity {

    private static final int WHAT_UPDATE_CHART = 1002;
    private static final int WHAT_TEST_TX_TRAFFIC = 1003;
    private static final int WHAT_TEST_RX_TRAFFIC = 1004;

    ActivityTestExecBinding binding;


    private long rxRate;
    private long txRate;
    private long delay;

    @Override
    protected String tag() {
        return "TestExecActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityTestExecBinding.inflate(getLayoutInflater());
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

        EventTracker.trackTestExecShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {

        binding.testExecBack.setOnClickListener(v -> {
            back();
        });

        sendUpdateTraffic(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetTestExecutor.INSTANCE.test();
        startTest();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetTestExecutor.INSTANCE.cancel();
        stopTest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeUpdateTraffic();
    }

    private void updateTrafficView() {
        if (uiHandler.hasMessages(WHAT_TEST_TX_TRAFFIC)) {
            long lastTxRate = TrafficChartManager.getInstance().lastTxRate();
            binding.testExecName.setText(R.string.result_upload);
            binding.testExecValue.setText(MyConvertUtils.byte2FitMemoryValue(lastTxRate, 1));
            binding.testExecUnit.setText(MyConvertUtils.byte2FitMemoryUnit(lastTxRate) + "ps");
            TestExecChartHelper.updateTxChartView(this, binding.testExecTxChart);
        }
        if (uiHandler.hasMessages(WHAT_TEST_RX_TRAFFIC)) {
            long lastRxRate = TrafficChartManager.getInstance().lastRxRate();
            binding.testExecName.setText(R.string.result_download);
            binding.testExecValue.setText(MyConvertUtils.byte2FitMemoryValue(lastRxRate, 1));
            binding.testExecUnit.setText(MyConvertUtils.byte2FitMemoryUnit(lastRxRate) + "ps");
            TestExecChartHelper.updateRxChartView(this, binding.testExecRxChart);
        }

        NormalPingExecutor.INSTANCE.ping("www.kernel.org", 1, 3 * 1000, new NormalPingCallback() {
            @Override
            public void onCompleted(NormalPingCommand pingTask, String host, long ping, int index) {
                LogUtils.e(TAG, "--> ping onCompleted()  host=" + host + "  ping=" + ping + "  index=" + index);
                if (ContextManager.isSurvival(TestExecActivity.this)) {
                    binding.testExecDelay.post(() -> {
                        String fitPing = ping + "ms";
                        binding.testExecDelay.setText(fitPing);
                        delay = ping;
                    });
                }
            }

            @Override
            public void onFailed(NormalPingCommand pingTask, String host, Exception e, int index) {
                LogUtils.e(TAG, "--> ping onFailed()  host=" + host + "  e=" + e + "  index=" + index);
                if (ContextManager.isSurvival(TestExecActivity.this)) {
                    binding.testExecDelay.post(() -> {
                        String fitPing = 1000 + "ms";
                        binding.testExecDelay.setText(fitPing);
                        delay = 1000;
                    });
                }
            }
        });
    }

    private void sendUpdateTraffic(boolean fromUser) {
        if (uiHandler.hasMessages(WHAT_UPDATE_CHART)) {
            uiHandler.removeMessages(WHAT_UPDATE_CHART);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_UPDATE_CHART, fromUser ? 0 : 500L);
    }

    private void removeUpdateTraffic() {
        if (uiHandler.hasMessages(WHAT_UPDATE_CHART)) {
            uiHandler.removeMessages(WHAT_UPDATE_CHART);
        }
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_UPDATE_CHART) {
            updateTrafficView();
            sendUpdateTraffic(false);
        } else if (msg.what == WHAT_TEST_TX_TRAFFIC) {
            long maxTxRate = TrafficChartManager.getInstance().maxTxRate();
            String fitTxRate = MyConvertUtils.byte2FitMemorySize(maxTxRate, 1) + "ps";
            binding.testExecTxValue.setText(fitTxRate);
            txRate = maxTxRate;
            sendTestRX();
        } else if (msg.what == WHAT_TEST_RX_TRAFFIC) {
            long maxRxRate = TrafficChartManager.getInstance().maxRxRate();
            String fitRxRate = MyConvertUtils.byte2FitMemorySize(maxRxRate, 1) + "ps";
            binding.testExecRxValue.setText(fitRxRate);
            rxRate = maxRxRate;
            uiHandler.postDelayed(() -> {
                handleExecResult();
            }, 300);
        }
    }


    private void startTest() {
        stopTest();

        binding.testExecRxValue.setText("--");
        binding.testExecTxValue.setText("--");
        binding.testExecValue.setText("--");
        binding.testExecUnit.setText("--");
        binding.testExecName.setText(R.string.result_upload);
        binding.testExecRxChart.setVisibility(View.GONE);
        binding.testExecTxChart.setVisibility(View.GONE);

        sendTestTX();
    }

    private void stopTest() {
        removeTestTX();
        removeTestRX();
    }

    private void sendTestTX() {
        binding.testExecRxChart.setVisibility(View.GONE);
        binding.testExecTxChart.setVisibility(View.VISIBLE);
        if (uiHandler.hasMessages(WHAT_TEST_TX_TRAFFIC)) {
            uiHandler.removeMessages(WHAT_TEST_TX_TRAFFIC);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_TEST_TX_TRAFFIC, 5 * 1000L); // TX 测 5s
    }

    private void removeTestTX() {
        if (uiHandler.hasMessages(WHAT_TEST_TX_TRAFFIC)) {
            uiHandler.removeMessages(WHAT_TEST_TX_TRAFFIC);
        }
    }

    private void sendTestRX() {
        binding.testExecRxChart.setVisibility(View.VISIBLE);
        binding.testExecTxChart.setVisibility(View.GONE);
        if (uiHandler.hasMessages(WHAT_TEST_RX_TRAFFIC)) {
            uiHandler.removeMessages(WHAT_TEST_RX_TRAFFIC);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_TEST_RX_TRAFFIC, 5 * 1000L); // TX 测 5s
    }

    private void removeTestRX() {
        if (uiHandler.hasMessages(WHAT_TEST_RX_TRAFFIC)) {
            uiHandler.removeMessages(WHAT_TEST_RX_TRAFFIC);
        }
    }

    private void handleExecResult() {
        long createTime = System.currentTimeMillis();
        String device = MyDeviceUtils.getDeviceName();
        long delay = this.delay;
        long rxRate = this.rxRate;
        long txRate = this.txRate;
        String netName = MyDeviceUtils.getSsid(this);
        String netMode = MyDeviceUtils.getWifiStandard(this);
        String netSpeed = MyDeviceUtils.getPhySpeed(this);
        String signal = MyDeviceUtils.getSignal(this);
        String dns = MyDeviceUtils.getDns(this);
        TestHistoryEntity entity = new TestHistoryEntity(createTime, device, delay, rxRate, txRate, netName, netMode, netSpeed, signal, dns);
        LogUtils.e(TAG, "--> handleExecResult()  entity=" + entity);
        TestHistoryDbManager.getInstance().addHistory(entity);
        startTestResultActivity(entity);
    }

    public void startTestResultActivity(@NonNull TestHistoryEntity entity) {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestResultActivity.class);
            intent.putExtra(IntentConstants.EXTRA_HISTORY_ENTITY, entity);
            startActivity(intent);
            finish();
        }
    }
}
