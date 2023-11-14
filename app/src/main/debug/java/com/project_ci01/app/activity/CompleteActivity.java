package com.project_ci01.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.databinding.ActivityCompleteBinding;

public class CompleteActivity extends BaseActivity {

    private ActivityCompleteBinding binding;

    private ImageEntity entity;

    @Override
    protected String tag() {
        return "CompleteActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityCompleteBinding.inflate(getLayoutInflater());
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

    private void init(Intent intent) {
        if (intent != null) {
            entity = intent.getParcelableExtra(IConfig.KEY_IMAGE_ENTITY);
        }
    }
}
