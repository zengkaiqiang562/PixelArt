package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.utils.MyConvertUtils;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.dao.TestHistoryEntity;
import com.project_m1142.app.databinding.ActivityTestResultBinding;
import com.project_m1142.app.ui.constants.IntentConstants;

public class TestResultActivity extends BaseActivity {

    ActivityTestResultBinding binding;

    private TestHistoryEntity entity;

    @Override
    protected String tag() {
        return "TestResultActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityTestResultBinding.inflate(getLayoutInflater());
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

        EventTracker.trackTestResultShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {

        binding.testResultBack.setOnClickListener(v -> {
            back();
        });

        if (intent != null) {
            entity = intent.getParcelableExtra(IntentConstants.EXTRA_HISTORY_ENTITY);
        }

        if (entity == null) {
            return;
        }

        String fitTxRate = MyConvertUtils.byte2FitMemorySize(entity.txRate, 1) + "ps";
        binding.testResultTxValue.setText(fitTxRate);
        String fitRxRate = MyConvertUtils.byte2FitMemorySize(entity.rxRate, 1) + "ps";
        binding.testResultRxValue.setText(fitRxRate);
        String fitPing = entity.delay + "ms";
        binding.testResultDelay.setText(fitPing);
        binding.testResultClientName.setText(entity.device);
        binding.testResultSsidValue.setText(entity.netName);
        binding.testResultModeValue.setText(entity.netMode);
        binding.testResultSpeedValue.setText(entity.netSpeed);
        binding.testResultSignalValue.setText(entity.signal);
        binding.testResultDnsValue.setText(TextUtils.isEmpty(entity.dns) ? "--" : entity.dns);
    }
}
