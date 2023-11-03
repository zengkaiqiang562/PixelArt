package com.project_ci01.app.activity;

import android.view.View;

import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.databinding.ActivityPixelBinding;

public class PixelActivity extends BaseActivity {

    ActivityPixelBinding binding;

    @Override
    protected String tag() {
        return "PixelActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityPixelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }
}
