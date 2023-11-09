package com.project_ci01.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.databinding.FragmentMineBinding;

public class MineFragment extends BaseFragment {

    private FragmentMineBinding binding;
    
    @Override
    protected String tag() {
        return "MineFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentMineBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }
}
