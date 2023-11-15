package com.project_ci01.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.project_ci01.app.banner.DailyBannerItem;
import com.project_ci01.app.banner.IBannerItem;
import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_ci01.app.fragment.category.AllFragment;
import com.project_ci01.app.fragment.category.CartoonFragment;
import com.project_ci01.app.fragment.category.FoodFragment;
import com.project_ci01.app.fragment.category.LoveFragment;
import com.project_ci01.app.indicator.ScaleTransitionPagerTitleView;
import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.databinding.FragmentHomeBinding;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeAnchor;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgePagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.badge.BadgeRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragment {

    private FragmentHomeBinding binding;

    private final List<BaseFragment> fragments = new ArrayList<>();
    private CategoryPagerAdapter pagerAdapter;

    private final Map<Integer, ScaleTransitionPagerTitleView> indicatorTitleMap = new HashMap<>();
    private MultiBannerAdapter bannerAdapter;

    @Override
    protected String tag() {
        return "HomeFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentHomeBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initBanner();
        initIndicator();
        initViewPager();
    }

    private void initBanner() {
        binding.banner.addBannerLifecycleObserver(this);
        bannerAdapter = new MultiBannerAdapter();
        binding.banner.setAdapter(bannerAdapter);
        binding.banner.setIntercept(false);
        binding.banner.setIndicator(new CircleIndicator(activity));
        binding.banner.setOnBannerListener(new OnBannerListener<IBannerItem>() {
            @Override
            public void onBannerClick(IBannerItem item, int position) {
                LogUtils.e(TAG, "--> onBannerClick()  item=" + item + "  position=" + position);
            }
        });

        List<IBannerItem> bannerItems = new ArrayList<>();
        bannerItems.add(new DailyBannerItem(R.drawable.image1));
        bannerItems.add(new DailyBannerItem(R.drawable.image2));
        bannerItems.add(new DailyBannerItem(R.drawable.image3));
        bannerAdapter.setDatas(bannerItems);
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
        fragments.add(new AllFragment());
        fragments.add(new LoveFragment());
        fragments.add(new CartoonFragment());
        fragments.add(new FoodFragment());
        pagerAdapter = new CategoryPagerAdapter(getChildFragmentManager());
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


    private enum CatTab { // 种类标签
        ALL("All"),
        LOVE("Love"),
        CARTOON("Cartoon"),
        FOOD("Food");
        final String tabName;

        CatTab(String tabName) {
            this.tabName = tabName;
        }
    }

    private class CategoryPagerAdapter extends FragmentPagerAdapter {

        public CategoryPagerAdapter(@NonNull FragmentManager fm) {
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
            return CatTab.values().length;
        }

        @Override
        public IPagerTitleView getTitleView(Context context, int index) {
//            BadgePagerTitleView badgePagerTitleView = new BadgePagerTitleView(context);

            ScaleTransitionPagerTitleView scaleTransitionPagerTitleView = new ScaleTransitionPagerTitleView(context);
            scaleTransitionPagerTitleView.setMinScale(0.94f);
            scaleTransitionPagerTitleView.setText(CatTab.values()[index].tabName);
            scaleTransitionPagerTitleView.setTextSize(18);
            int titleWidth = (int) (ScreenUtils.getScreenWidth() * 1f / CatTab.values().length);
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

//            badgePagerTitleView.setInnerPagerTitleView(scaleTransitionPagerTitleView);
//
//            // setup badge
//            if (index == 1) {
//                TextView badgeTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.view_indicator_badge, null);
//
//                //TODO test
//                badgeTextView.setText(String.valueOf(23));
//
//                badgePagerTitleView.setBadgeView(badgeTextView);
//                badgePagerTitleView.setXBadgeRule(new BadgeRule(BadgeAnchor.RIGHT, 0));
//                badgePagerTitleView.setYBadgeRule(new BadgeRule(BadgeAnchor.TOP, 0));
//            }
//
//            // cancel badge when click tab, default true
//            badgePagerTitleView.setAutoCancelBadge(true);
//
//            return badgePagerTitleView;
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

//        @Override
//        public float getTitleWeight(Context context, int index) {
//            if (index == 0) {
//                return 2.0f;
//            } else if (index == 1) {
//                return 1.2f;
//            } else {
//                return 1.0f;
//            }
//        }
    }
}
