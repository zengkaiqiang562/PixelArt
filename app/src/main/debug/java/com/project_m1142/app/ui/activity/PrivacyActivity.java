package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.project_m1142.app.R;
import com.project_m1142.app.base.config.AppConfig;
import com.project_m1142.app.base.constants.SPConstants;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends BaseActivity {

    private ActivityPrivacyBinding binding;

    private boolean isAgreed = true; // 默认同意

    @Override
    protected String tag() {
        return "PrivacyActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init(getIntent());

        EventTracker.trackPrivacyShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        binding.privacyStatus.setSelected(isAgreed); // init

        binding.privacyStatus.setOnClickListener(v -> {
            isAgreed = !isAgreed;
            binding.privacyStatus.setSelected(isAgreed);
        });

        binding.privacyStart.setOnClickListener(v -> {
            if (!isAgreed) {
                ToastUtils.showShort(R.string.privacy_toast);
                return;
            }
            SPUtils.getInstance().put(SPConstants.SP_AGREE_PROTOCOL, true);
            back();
        });

        binding.privacyTerms.setOnClickListener(v -> {
            ContextManager.turn2BrowserWithHost(this, AppConfig.URL_TERMS);
        });

        binding.privacyPolicy.setOnClickListener(v -> {
            ContextManager.turn2BrowserWithHost(this, AppConfig.URL_PRIVACY);
        });
    }

    @Override
    public void onBackPressed() {
        /* forbidden back button */
    }

}
