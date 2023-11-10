package com.project_ci01.app.fragment.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.databinding.FragmentCartoonBinding;

public class CartoonFragment extends BaseFragment {

    private FragmentCartoonBinding binding;
    
    @Override
    protected String tag() {
        return "CartoonFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentCartoonBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        // 必须设置后才能嵌套滑动
        binding.cartoonRv.setLayoutManager(new LinearLayoutManager(activity));
        binding.cartoonRv.setAdapter(new MultiBannerAdapter());
    }
}
