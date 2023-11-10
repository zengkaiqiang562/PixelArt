package com.project_ci01.app.fragment.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.databinding.FragmentLoveBinding;

public class LoveFragment extends BaseFragment {

    private FragmentLoveBinding binding;
    
    @Override
    protected String tag() {
        return "LoveFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentLoveBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        // 必须设置后才能嵌套滑动
        binding.loveRv.setLayoutManager(new LinearLayoutManager(activity));
        binding.loveRv.setAdapter(new MultiBannerAdapter());
    }
}
