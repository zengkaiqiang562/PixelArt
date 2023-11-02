package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.project_m1142.app.base.advert.AdResourceActivity;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.databinding.ActivityUnconnectReportBinding;
import com.project_m1142.app.ui.constants.IntentConstants;

public class UnconnectReportActivity extends AdResourceActivity {

    ActivityUnconnectReportBinding binding;


    @Override
    protected String tag() {
        return "UnconnectReportActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityUnconnectReportBinding.inflate(getLayoutInflater());
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

        EventTracker.trackReportShow();
        EventTracker.trackUnconnectReportShow();
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

        binding.reportReconnect.setOnClickListener(v -> {
            back(true);
        });
    }

    @Override
    protected void back() {
        back(false);
    }

    private void back(boolean reconnect) {
        Intent intent = new Intent();
        intent.putExtra(IntentConstants.EXTRA_RECONNECT_FLAG, reconnect);
        setResult(RESULT_OK, intent);
        finish();
    }
}
