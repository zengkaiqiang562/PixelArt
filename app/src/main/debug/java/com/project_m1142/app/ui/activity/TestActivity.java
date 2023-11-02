package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.utils.MyConvertUtils;
import com.project_m1142.app.base.utils.MyDeviceUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.dao.TestHistoryDbManager;
import com.project_m1142.app.dao.TestHistoryEntity;
import com.project_m1142.app.databinding.ActivityTestBinding;
import com.project_m1142.app.ping.NormalPingCallback;
import com.project_m1142.app.ping.NormalPingCommand;
import com.project_m1142.app.ping.NormalPingExecutor;
import com.project_m1142.app.ui.helper.TestChartHelper;
import com.project_m1142.app.network.TrafficChartManager;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.ui.history.HistoryItem;
import com.project_m1142.app.ui.history.IItem;
import com.project_m1142.app.ui.history.TestAdapter;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends BaseActivity implements OnItemClickListener<BaseHolder<IItem>> {

    private static final int WHAT_UPDATE_TRAFFIC = 1002;
    private static final int WHAT_UPDATE_HISTORY = 1003;

    ActivityTestBinding binding;
    private TestAdapter adapter;

    @Override
    protected String tag() {
        return "TestActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityTestBinding.inflate(getLayoutInflater());
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
        TrafficChartManager.getInstance().startTraffic();

        EventTracker.trackTestShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        sendUpdateTraffic(true);

        adapter = new TestAdapter(this, this);
        binding.testRv.setAdapter(adapter);
        binding.testRv.setLayoutManager(new LinearLayoutManager(this));

        binding.testHistory.setOnClickListener(v -> {
            startTestHistoryActivity();
        });

        binding.testStart.setOnClickListener(v -> {
            startTestExecActivity();
        });

        binding.testBack.setOnClickListener(v -> {
            back();
        });

        binding.testDevice.setText(getString(R.string.test_device, MyDeviceUtils.getDeviceName()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendUpdateHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        TrafficChartManager.getInstance().stopTraffic();
        removeUpdateTraffic();
    }

    private void updateTrafficView() {
        long avgRxRate = TrafficChartManager.getInstance().avgRxRate();
        long avgTxRate = TrafficChartManager.getInstance().avgTxRate();
        String fitTxRate = MyConvertUtils.byte2FitMemorySize(avgTxRate, 1) + "ps";
        String fitRxRate = MyConvertUtils.byte2FitMemorySize(avgRxRate, 1) + "ps";
        binding.testTxValue.setText(fitTxRate);
        binding.testRxValue.setText(fitRxRate);
        TestChartHelper.updateChartView(this, binding.testChart, binding.testEmpty);

        NormalPingExecutor.INSTANCE.ping("www.kernel.org", 1, 3 * 1000, new NormalPingCallback() {
            @Override
            public void onCompleted(NormalPingCommand pingTask, String host, long ping, int index) {
                LogUtils.e(TAG, "--> ping onCompleted()  host=" + host + "  ping=" + ping + "  index=" + index);
                if (ContextManager.isSurvival(TestActivity.this)) {
                    binding.testDelay.post(() -> {
                        String fitPing = ping + "ms";
                        binding.testDelay.setText(fitPing);
                    });
                }
            }

            @Override
            public void onFailed(NormalPingCommand pingTask, String host, Exception e, int index) {
                LogUtils.e(TAG, "--> ping onFailed()  host=" + host + "  e=" + e + "  index=" + index);
                if (ContextManager.isSurvival(TestActivity.this)) {
                    binding.testDelay.post(() -> {
                        String fitPing = 1000 + "ms";
                        binding.testDelay.setText(fitPing);
                    });
                }
            }
        });
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
        } else if (msg.what == WHAT_UPDATE_HISTORY && ContextManager.isSurvival(this) && isResumed) {
            updateHistory();
        }
    }

    public void sendUpdateHistory() {
        if (uiHandler.hasMessages(WHAT_UPDATE_HISTORY)) {
            uiHandler.removeMessages(WHAT_UPDATE_HISTORY);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_UPDATE_HISTORY, 200);
    }

    private void updateHistory() {
        LogUtils.e(TAG, "-->  updateHistory()");

        TestHistoryDbManager.getInstance().queryLast1(entities -> {
            if (ContextManager.isSurvival(this) && adapter != null) {
                List<IItem> items = new ArrayList<>();
                for (TestHistoryEntity entity : entities) {
                    items.add(new HistoryItem(entity));
                }
                adapter.setItems(items);
            }
        });
    }

    @Override
    public void onItemClick(int item, BaseHolder<IItem> holder) {
        if (holder instanceof TestAdapter.HistoryHolder) {
            startTestResultActivity(((TestAdapter.HistoryHolder) holder).item.entity);
        }
    }

    public void startTestResultActivity(TestHistoryEntity entity) {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestResultActivity.class);
            intent.putExtra(IntentConstants.EXTRA_HISTORY_ENTITY, entity);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST_RESULT);
        }
    }

    public void startTestHistoryActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestHistoryActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST_HISTORY);
        }
    }

    public void startTestExecActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestExecActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST_EXEC);
        }
    }
}
