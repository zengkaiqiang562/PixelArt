package com.project_ci01.app.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.databinding.ActivityCompleteBinding;
import com.project_ci01.app.databinding.ActivityCompleteDisplayBinding;

public class CompleteDisplayActivity extends BaseActivity {

    private ActivityCompleteDisplayBinding binding;

    private ImageEntityNew entity;

    @Override
    protected String tag() {
        return "CompleteDisplayActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityCompleteDisplayBinding.inflate(getLayoutInflater());
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
            Glide.with(this)
                    .load(entity.colorImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                    .skipMemoryCache(true) // 不走内存缓存
                    .into(binding.completeImage);
        }

        binding.animDisplay.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                startCompleteActivity(entity);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.animDisplay.postDelayed(this::startAnim, 100);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAnim();
    }

    private void startAnim() {
        if (binding.animDisplay.isAnimating()) {
            binding.animDisplay.cancelAnimation();
        }
        binding.animDisplay.playAnimation();
    }

    private void stopAnim() {
        if (binding.animDisplay.isAnimating()) {
            binding.animDisplay.cancelAnimation();
        }
    }

    private void startCompleteActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(this, CompleteActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            startActivity(intent);
            finish();
        }
    }
}
