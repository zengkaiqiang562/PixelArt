package com.project_ci01.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.activity.PixelActivity;
import com.project_ci01.app.banner.DailyBannerItem;
import com.project_ci01.app.banner.IBannerItem;
import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_ci01.app.base.common.CompleteCallback;
import com.project_ci01.app.indicator.MyNavigator;
import com.project_ci01.app.indicator.MyPagerIndicator;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.fragment.category.AllFragment;
import com.project_ci01.app.fragment.category.CartoonFragment;
import com.project_ci01.app.fragment.category.FoodFragment;
import com.project_ci01.app.fragment.category.LoveFragment;
import com.project_ci01.app.indicator.MyPagerTitleView;
import com.project_ci01.app.R;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.databinding.FragmentHomeBinding;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.listener.OnBannerListener;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends BaseFragment implements ImageDbManager.OnImageDbChangedListener {

    private FragmentHomeBinding binding;

    private final List<BaseFragment> fragments = new ArrayList<>();
    private CategoryPagerAdapter pagerAdapter;

    private final Map<Integer, MyPagerTitleView> indicatorTitleMap = new HashMap<>();
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
    protected void initView(View view, Bundle savedInstanceState) {
        initBanner();
        initIndicator();
        initViewPager();

        ImageDbManager.getInstance().queryHomeCategories(categories -> {
            LogUtils.e(TAG, "--> queryHomeCategories()  categories=" + categories);
        });
    }

    private void initBanner() {
        binding.banner.addBannerLifecycleObserver(this);
        bannerAdapter = new MultiBannerAdapter();
        binding.banner.setAdapter(bannerAdapter);
        binding.banner.setIntercept(false);
        binding.banner.setIndicator(new RectangleIndicator(activity));
        binding.banner.setIndicatorNormalWidth(ConvertUtils.dp2px(8));
        binding.banner.setIndicatorSelectedWidth(ConvertUtils.dp2px(8));
        binding.banner.setIndicatorSpace(ConvertUtils.dp2px(8));
        IndicatorConfig.Margins margins = new IndicatorConfig.Margins();
        margins.bottomMargin = ConvertUtils.dp2px(32);
        binding.banner.setIndicatorMargins(margins);
        binding.banner.setOnBannerListener(new OnBannerListener<IBannerItem>() {
            @Override
            public void onBannerClick(RecyclerView.ViewHolder holder,  IBannerItem item, int position) {
                LogUtils.e(TAG, "--> onBannerClick()  item=" + item + "  position=" + position);
                if (holder instanceof MultiBannerAdapter.DailyHolder) {
                    ImageEntityNew entity = ((MultiBannerAdapter.DailyHolder) holder).entity;
                    if (entity != null) {
                        startPixelActivity(entity);
                    }
                }
            }
        });

        List<IBannerItem> bannerItems = new ArrayList<>();
        bannerItems.add(new DailyBannerItem());
        bannerItems.add(new DailyBannerItem());
        bannerAdapter.setDatas(bannerItems);
    }

    private void initIndicator() {
        MyNavigator navigator = new MyNavigator(getActivity());
        navigator.setIndicatorOnTop(true);
        navigator.setTitleTextSpace(ConvertUtils.dp2px(18));
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

    @Override
    public void onImageDbChanged() {
        if (bannerAdapter != null) {
            bannerAdapter.notifyDataSetChanged();
        }
    }

    private void startPixelActivity(@NonNull ImageEntityNew entity) {
        if (canTurn()) {
            Intent intent = new Intent(activity, PixelActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            activity.startActivityForResult(intent, IConfig.REQUEST_PIXEL_ACTIVITY);
        }
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

            MyPagerTitleView myPagerTitleView = new MyPagerTitleView(context);
            myPagerTitleView.setMinWidth(ConvertUtils.dp2px(68));
            myPagerTitleView.setMinScale(0.94f);
            myPagerTitleView.setText(CatTab.values()[index].tabName);
            myPagerTitleView.setTextSize(16);
//            int titleWidth = (int) (ScreenUtils.getScreenWidth() * 1f / CatTab.values().length);


            myPagerTitleView.setNormalColor(Color.parseColor("#FF9D9A9B"));
            myPagerTitleView.setSelectedColor(ContextCompat.getColor(context, R.color.black_2e2a2b));
//            scaleTransitionPagerTitleView.setNormalFontResId(R.font.pingfang_medium);
//            scaleTransitionPagerTitleView.setSelectFontResId(R.font.pingfang_bold);
            myPagerTitleView.setOnClickListener(new View.OnClickListener() {
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

            indicatorTitleMap.put(index, myPagerTitleView);

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
            return myPagerTitleView;
        }

        @Override
        public IPagerIndicator getIndicator(Context context) {
//            LinePagerIndicator indicator = new LinePagerIndicator(context);
////            indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
//            indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
//            indicator.setLineWidth(UIUtil.dip2px(context, 12));
//            indicator.setStartInterpolator(new AccelerateInterpolator());
//            indicator.setEndInterpolator(new DecelerateInterpolator(1.6f));
////            indicator.setXOffset(UIUtil.dip2px(context, 12));
//            indicator.setYOffset(UIUtil.dip2px(context, 6));
//            int lineHeight = UIUtil.dip2px(context, 3);
//            indicator.setLineHeight(lineHeight);
//            indicator.setRoundRadius(lineHeight / 2f);
//            int colorBlack = ContextCompat.getColor(context, R.color.black);
//            indicator.setColors(colorBlack, colorBlack, colorBlack);
//            return indicator;
            return new MyPagerIndicator(context);
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
