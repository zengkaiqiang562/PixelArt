package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.AppUtils;
import com.project_m1142.app.base.advert.AdResourceActivity;
import com.project_m1142.app.base.config.AppConfig;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.view.dialog.DialogHelper;
import com.project_m1142.app.base.view.dialog.SimpleDialogListener;
import com.project_m1142.app.databinding.ActivityMineBinding;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.ui.dialog.RateDialog;
import com.project_m1142.app.ui.helper.TabSwitcher;

public class MineActivity extends AdResourceActivity {

    ActivityMineBinding binding;

    private RateDialog rateDialog;

    @Override
    protected String tag() {
        return "MineActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityMineBinding.inflate(getLayoutInflater());
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

        EventTracker.trackMoreShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {

        binding.homeMenuWifiParent.setOnClickListener(v -> {
            TabSwitcher.startWiFiActivity(this);
        });

        binding.homeMenuVpnParent.setOnClickListener(v -> {
            TabSwitcher.startHomeActivity(this);
        });

        binding.morePrivacyRoot.setOnClickListener(v -> {
            ContextManager.turn2BrowserWithHost(this, AppConfig.URL_PRIVACY);
        });

        binding.moreServiceRoot.setOnClickListener(v -> {
            ContextManager.turn2BrowserWithHost(this, AppConfig.URL_TERMS);
        });

        binding.moreRateRoot.setOnClickListener(v -> {
            showRateDialog();
        });

        binding.moreFeedbackRoot.setOnClickListener(v -> {
            ContextManager.turn2Email(this, AppConfig.FEEDBACK_EMAIL);
        });

        binding.moreShareRoot.setOnClickListener(v -> {
            ContextManager.turn2ShareApp(this, getPackageName());
        });

        binding.moreAboutRoot.setOnClickListener(v -> {
            startAboutActivity();
        });

        binding.moreButtonTest.setOnClickListener(v -> {
            startTestActivity();
        });

    }

    private void startAboutActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }

    private void startTestActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST);
        }
    }

    @Override
    protected void back() {
        ContextManager.INSTANCE.finishAll();
    }

    private void showRateDialog() {
        rateDialog = DialogHelper.showDialog(this, rateDialog, RateDialog.class, null);
    }
}
