package com.project_ci01.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ScreenUtils;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.fragment.mine.CompletedFragment;
import com.project_ci01.app.fragment.mine.InProgressFragment;
import com.project_ci01.app.indicator.ScaleTransitionPagerTitleView;
import com.project_ci01.app.R;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.databinding.FragmentMineBinding;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MineFragment extends BaseFragment {

    private FragmentMineBinding binding;

    private final List<BaseFragment> fragments = new ArrayList<>();

    private MinePagerAdapter pagerAdapter;

    private final Map<Integer, ScaleTransitionPagerTitleView> indicatorTitleMap = new HashMap<>();
    
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
        initIndicator();
        initViewPager();
    }

    private void initIndicator() {
        CommonNavigator navigator = new CommonNavigator(getActivity());
//        navigator.setAdjustMode(true); // Tab 固定，宽度平分
        IndicatorAdapter indicatorAdapter = new IndicatorAdapter();
        navigator.setAdapter(indicatorAdapter);
        binding.indicator.setNavigator(navigator);
    }

    private void initViewPager() {
        fragments.clear();
        fragments.add(new InProgressFragment());
        fragments.add(new CompletedFragment());
        pagerAdapter = new MinePagerAdapter(getChildFragmentManager());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setOffscreenPageLimit(fragments.size() - 1);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.indicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                if (!ContextManager.isSurvival(activity)) {
                    return;
                }
                binding.indicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                binding.indicator.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        ImageDbManager.getInstance().countInProgressAndCompleted(result -> {
            if (ContextManager.isSurvival(activity) && result[0] > 0 && result[1] == 0) { // 只有 Completed 时
                binding.viewPager.setCurrentItem(MineTab.COMPLETED.ordinal(), false);
            } else {
                binding.viewPager.setCurrentItem(MineTab.IN_PROGRESS.ordinal(), false);
            }
        });
    }

    /*==============================*/

    private enum MineTab {
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed");

        final String tabName;

        MineTab(String tabName) {
            this.tabName = tabName;
        }
    }

    private class MinePagerAdapter extends FragmentPagerAdapter {

        public MinePagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    private class IndicatorAdapter extends CommonNavigatorAdapter {

        @Override
        public int getCount() {
            return MineTab.values().length;
        }

        @Override
        public IPagerTitleView getTitleView(Context context, int index) {
//            BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

            ScaleTransitionPagerTitleView scaleTransitionPagerTitleView = new ScaleTransitionPagerTitleView(context);
            scaleTransitionPagerTitleView.setMinScale(0.94f);
            scaleTransitionPagerTitleView.setText(MineTab.values()[index].tabName);
            scaleTransitionPagerTitleView.setTextSize(18);
            int titleWidth = (int) (ScreenUtils.getScreenWidth() * 1f / MineTab.values().length);
            scaleTransitionPagerTitleView.setWidth(titleWidth); // 设置每个 Title 的宽度

            scaleTransitionPagerTitleView.setNormalColor(ContextCompat.getColor(context, R.color.black));
            scaleTransitionPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.purple_200));
//            scaleTransitionPagerTitleView.setNormalFontResId(R.font.pingfang_medium);
//            scaleTransitionPagerTitleView.setSelectFontResId(R.font.pingfang_bold);
            scaleTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int currentItem = binding.viewPager.getCurrentItem();
                    LogUtils.e(TAG + "--> Indicator Title onClick() index : " + index + "  currentItem : " + currentItem);
                    if (index != currentItem) {
                        binding.viewPager.setCurrentItem(index);
                    } else {
                        fragments.get(index).reload();
                    }
                }
            });

            indicatorTitleMap.put(index, scaleTransitionPagerTitleView);

            return scaleTransitionPagerTitleView;
        }

        @Override
        public IPagerIndicator getIndicator(Context context) {
            LinePagerIndicator indicator = new LinePagerIndicator(context);
//            indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
            indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
            indicator.setLineWidth(UIUtil.dip2px(context, 12));
            indicator.setStartInterpolator(new AccelerateInterpolator());
            indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
//            indicator.setXOffset(UIUtil.dip2px(context, 12));
            indicator.setYOffset(UIUtil.dip2px(context, 6));
            int lineHeight = UIUtil.dip2px(context, 3);
            indicator.setLineHeight(lineHeight);
            indicator.setRoundRadius(lineHeight / 2f);
            int colorBlack = ContextCompat.getColor(context, R.color.black);
            indicator.setColors(colorBlack, colorBlack, colorBlack);
            return indicator;
        }
    }
}
