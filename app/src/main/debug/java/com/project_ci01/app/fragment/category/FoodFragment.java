package com.project_ci01.app.fragment.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.databinding.FragmentFoodBinding;

public class FoodFragment extends BaseFragment {

    private FragmentFoodBinding binding;
    
    @Override
    protected String tag() {
        return "FoodFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentFoodBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        // 必须设置后才能嵌套滑动
        binding.foodRv.setLayoutManager(new LinearLayoutManager(activity));
        binding.foodRv.setAdapter(new MultiBannerAdapter());
    }
}
