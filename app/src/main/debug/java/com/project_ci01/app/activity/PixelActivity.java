package com.project_ci01.app.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.project_ci01.app.pixel.Props;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.btnBucket.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.BUCKET);
        });
    }
}
