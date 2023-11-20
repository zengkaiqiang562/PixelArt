package com.project_ci01.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageEntity;
import com.project_ci01.app.pixel.Props;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.databinding.ActivityPixelBinding;

public class PixelActivity extends BaseActivity {

    ActivityPixelBinding binding;

    private ImageEntity entity;

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

        init(getIntent());
    }

    private void init(Intent intent) {

        if (intent != null) {
            entity = intent.getParcelableExtra(IConfig.KEY_IMAGE_ENTITY);
        }

        if (entity != null) {
            uiHandler.post(() -> {
                binding.viewPixel.setImageEntity(entity);
            });
        }

        binding.btnBucket.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.BUCKET);
        });

        binding.btnWand.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.WAND);
        });

        binding.btnBrush.setOnClickListener(v -> {
            binding.viewPixel.setProps(Props.BRUSH);
        });

        binding.btnTip.setOnClickListener(v -> {
            binding.viewPixel.centerUndrawPixel();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.viewPixel.release();
    }
}
