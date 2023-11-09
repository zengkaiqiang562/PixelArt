package com.project_ci01.app.fragment.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.databinding.FragmentAllBinding;

public class AllFragment extends BaseFragment {

    private FragmentAllBinding binding;

    @Override
    protected String tag() {
        return "AllFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentAllBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        binding.allRv.setLayoutManager(new LinearLayoutManager(activity));
        binding.allRv.setAdapter(new MultiBannerAdapter());
    }
}
