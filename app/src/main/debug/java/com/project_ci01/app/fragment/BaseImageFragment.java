package com.project_ci01.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_ci01.app.activity.CompleteActivity;
import com.project_ci01.app.activity.PixelActivity;
import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;

public abstract class BaseImageFragment extends BaseFragment implements ImageDbManager.OnImageDbChangedListener {

    protected void startCompleteActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(activity, CompleteActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            activity.startActivityForResult(intent, IConfig.REQUEST_COMPLETE_ACTIVITY);
        }
    }

    protected void startPixelActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(activity, PixelActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            activity.startActivityForResult(intent, IConfig.REQUEST_PIXEL_ACTIVITY);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageDbManager.getInstance().addOnDbChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImageDbManager.getInstance().removeOnDbChangedListener(this);
    }

    @Override
    public void onImageAdded(String category, int imageId) {

    }

    @Override
    public void onImageUpdated(String category, int imageId) {

    }

    @Override
    public void onDailyChanged() {

    }
}
