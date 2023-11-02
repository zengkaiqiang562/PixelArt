package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.dao.TestHistoryDbManager;
import com.project_m1142.app.dao.TestHistoryEntity;
import com.project_m1142.app.databinding.ActivityTestHistoryBinding;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.ui.history.HistoryAdapter;
import com.project_m1142.app.ui.history.HistoryItem;
import com.project_m1142.app.ui.history.IItem;

import java.util.ArrayList;
import java.util.List;

public class TestHistoryActivity extends BaseActivity implements OnItemClickListener<BaseHolder<IItem>> {

    private static final int WHAT_UPDATE_HISTORY = 3001;

    ActivityTestHistoryBinding binding;
    private HistoryAdapter adapter;

    @Override
    protected String tag() {
        return "TestHistoryActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityTestHistoryBinding.inflate(getLayoutInflater());
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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        adapter = new HistoryAdapter(this, this);
        binding.historyRv.setAdapter(adapter);
        binding.historyRv.setLayoutManager(new LinearLayoutManager(this));

        binding.historyBack.setOnClickListener(v -> {
            back();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendUpdateHistory();
    }

    public void sendUpdateHistory() {
        if (uiHandler.hasMessages(WHAT_UPDATE_HISTORY)) {
            uiHandler.removeMessages(WHAT_UPDATE_HISTORY);
        }
        uiHandler.sendEmptyMessageDelayed(WHAT_UPDATE_HISTORY, 200);
    }

    private void updateHistory() {
        LogUtils.e(TAG, "-->  updateHistory()");

        TestHistoryDbManager.getInstance().queryAll(entities -> {
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
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_UPDATE_HISTORY && ContextManager.isSurvival(this) && isResumed) {
            updateHistory();
        }
    }

    @Override
    public void onItemClick(int item, BaseHolder<IItem> holder) {
        if (holder instanceof HistoryAdapter.HistoryHolder) {
            startTestResultActivity(((HistoryAdapter.HistoryHolder) holder).item.entity);
        }
    }

    public void startTestResultActivity(TestHistoryEntity entity) {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestResultActivity.class);
            intent.putExtra(IntentConstants.EXTRA_HISTORY_ENTITY, entity);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST_RESULT);
        }
    }

    public void startTestExecActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestExecActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST_EXEC);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(TAG, "--> onActivityResult()  requestCode=" + requestCode + "  resultCode=" + resultCode + "  data=" + data);

        if (resultCode != RESULT_OK) {
            return;
        }
    }
}
