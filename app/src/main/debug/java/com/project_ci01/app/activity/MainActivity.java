package com.project_ci01.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentPagerAdapter;

import com.project_ci01.app.adapter.MainPagerAdapter;
import com.project_ci01.app.databinding.ActivityMainBinding;
import com.project_ci01.app.fragment.DailyFragment;
import com.project_ci01.app.fragment.HomeFragment;
import com.project_ci01.app.fragment.MineFragment;
import com.project_ci01.app.R;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.base.view.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    private MainPagerAdapter pagerAdapter;

    private HomeFragment homeFragment;
    private DailyFragment dailyFragment;
    private MineFragment mineFragment;

    @Override
    protected String tag() {
        return "MainActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {

        initViewPager();

        binding.menuHomeClick.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() != BottomMenu.HOME.ordinal()) {
                setBottomMenu(BottomMenu.HOME);
            }
        });

        binding.menuDailyClick.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() != BottomMenu.DAILY.ordinal()) {
                setBottomMenu(BottomMenu.DAILY);
            }
        });

        binding.menuMineClick.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() != BottomMenu.MINE.ordinal()) {
                setBottomMenu(BottomMenu.MINE);
            }
        });
    }

    private void initViewPager() {
        List<BaseFragment> fragmentList = new ArrayList<>();
        homeFragment = new HomeFragment();
        fragmentList.add(homeFragment);
        dailyFragment = new DailyFragment();
        fragmentList.add(dailyFragment);
        mineFragment = new MineFragment();
        fragmentList.add(mineFragment);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentList);
        binding.viewPager.setAdapter(pagerAdapter);
        setBottomMenu(BottomMenu.HOME);
        binding.viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*================================ Bottom Menu Operation =============================*/
    private void resetBottom() {
        binding.menuHomeIcon.setSelected(false);
        binding.menuDailyIcon.setSelected(false);
        binding.menuMineIcon.setSelected(false);

        binding.menuHomeText.setTextColor(ContextCompat.getColor(this, R.color.grey_9d9a9b));
        binding.menuDailyText.setTextColor(ContextCompat.getColor(this, R.color.grey_9d9a9b));
        binding.menuMineText.setTextColor(ContextCompat.getColor(this, R.color.grey_9d9a9b));
    }

    private void setBottomMenu(BottomMenu bottomMenu) {
        if (!ContextManager.isSurvival(this)) {
            return;
        }
        LogUtils.e(TAG, "--> setBottomMenu()  bottomMenu=" + bottomMenu);
        resetBottom();
        binding.viewPager.setCurrentItem(bottomMenu.ordinal(), false);
        switch (bottomMenu) {
            case HOME:
                binding.menuHomeIcon.setSelected(true);
                binding.menuHomeText.setTextColor(ContextCompat.getColor(this, R.color.black_2e3234));
                break;
            case DAILY:
                binding.menuDailyIcon.setSelected(true);
                binding.menuDailyText.setTextColor(ContextCompat.getColor(this, R.color.black_2e3234));
                break;
            case MINE:
                binding.menuMineIcon.setSelected(true);
                binding.menuMineText.setTextColor(ContextCompat.getColor(this, R.color.black_2e3234));
                break;
        }
    }
    
    private enum BottomMenu {
        HOME,
        DAILY,
        MINE
    }
}
