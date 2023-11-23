package com.project_ci01.app.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.dao.ImageDbManager;

public abstract class BaseImageFragment extends BaseFragment implements ImageDbManager.OnImageDbChangedListener {

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
