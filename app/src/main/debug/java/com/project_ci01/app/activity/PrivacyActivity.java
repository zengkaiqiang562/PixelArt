package com.project_ci01.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.project_ci01.app.R;
import com.project_ci01.app.base.config.AppConfig;
import com.project_ci01.app.base.constants.SPConstants;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.manage.EventTracker;
import com.project_ci01.app.base.utils.TextBuilder;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.databinding.ActivityPrivacyBinding;

public class PrivacyActivity extends BaseActivity {

    private ActivityPrivacyBinding binding;

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

        binding.privacyAccept.setOnClickListener(v -> {
            SPUtils.getInstance().put(SPConstants.SP_ACCEPT_PROTOCOL, true);
            back();
        });

        String content = getString(R.string.privacy_content);
        String privacy = getString(R.string.privacy_privacy_policy);
        String terms = getString(R.string.privacy_terms_of_use);
        int indexTerms = content.indexOf(terms);
        int indexPrivacy = content.indexOf(privacy);
        TextBuilder builder = new TextBuilder(content);

        builder.setClick(view -> {
            ContextManager.turn2BrowserWithHost(this, AppConfig.URL_TERMS);
        }, indexTerms, indexTerms + terms.length());

        builder.setClick(view -> {
            ContextManager.turn2BrowserWithHost(this, AppConfig.URL_PRIVACY);
        }, indexPrivacy, indexPrivacy + privacy.length());

        binding.privacyContent.setText(builder.build());

    }

    @Override
    public void onBackPressed() {
        /* forbidden back button */
    }

}
