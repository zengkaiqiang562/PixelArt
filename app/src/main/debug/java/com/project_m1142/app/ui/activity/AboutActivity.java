package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.project_m1142.app.base.config.AppConfig;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.databinding.ActivityAboutBinding;

public class AboutActivity extends BaseActivity {

    ActivityAboutBinding binding;

    @Override
    protected String tag() {
        return "AboutActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
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

        binding.aboutOfficial.setText(AppConfig.URL_OFFICIAL);
        binding.aboutEmail.setText(AppConfig.FEEDBACK_EMAIL);

        String version = "current version number：" + AppUtils.getAppVersionName();
        binding.aboutVersion.setText(version);


        binding.aboutBack.setOnClickListener(v -> {
            back();
        });

        binding.aboutOfficial.setOnClickListener(v -> {
            ContextManager.turn2Email(this, AppConfig.URL_OFFICIAL);
        });

        binding.aboutEmail.setOnClickListener(v -> {
            ContextManager.turn2Email(this, AppConfig.FEEDBACK_EMAIL);
        });
    }
}
