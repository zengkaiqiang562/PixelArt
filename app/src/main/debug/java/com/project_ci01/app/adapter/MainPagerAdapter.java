package com.project_ci01.app.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.project_ci01.app.base.view.BaseFragment;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> list;
    public MainPagerAdapter(FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        this.list=list;
    }

    public MainPagerAdapter(FragmentManager fm, int behavior, List<BaseFragment> list) {
        super(fm, behavior);
        this.list=list;
    }

    public void setList(List<BaseFragment> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BaseFragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }


}
