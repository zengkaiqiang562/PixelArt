package com.project_ci01.app.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.activity.PixelActivity;
import com.project_ci01.app.adapter.HomeImageAdapter;
import com.project_ci01.app.banner.DailyBannerItem;
import com.project_ci01.app.banner.IBannerItem;
import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.databinding.FragmentDailyBinding;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class DailyFragment extends BaseFragment implements OnItemClickListener<HomeImageAdapter.HomeImageHolder>, ImageDbManager.OnImageDbChangedListener {

    private FragmentDailyBinding binding;

    private MultiBannerAdapter bannerAdapter;

    private HomeImageAdapter adapter;
    
    @Override
    protected String tag() {
        return "DailyFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentDailyBinding.inflate(inflater);
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
        initRecyclerView();
        update();
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

    private void initRecyclerView() {
        binding.dailyRv.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.dailyRv.setAdapter(adapter = new HomeImageAdapter(this, activity));
        binding.dailyRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            final int dp16 = ConvertUtils.dp2px(16);
            final int dp20 = ConvertUtils.dp2px(20);

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                outRect.left = 0;
                outRect.right = 0;
                outRect.top = 0;

                if (parent.getChildLayoutPosition(view) % 2 == 0) {
                    outRect.left = dp20;
                    outRect.right = dp16 / 2;
                }
                if (parent.getChildLayoutPosition(view) % 2 == 1) {
                    outRect.left = dp16 / 2;
                    outRect.right = dp20;
                }
            }
        });
    }

    public void update() {
        ImageDbManager.getInstance().queryByCategory(Category.DAILY.catName, entities -> {
            if (ContextManager.isSurvival(activity) && adapter != null) {
                adapter.setDatasAndNotify(entities);
            }
        });
    }

    @Override
    public void onImageDbChanged() {
        sendUpdateDailyMsg();
    }

    @Override
    public void onItemClick(int item, HomeImageAdapter.HomeImageHolder holder) {
        if (holder.entity != null) {
            startPixelActivity(holder.entity);
        }
    }

    private void startPixelActivity(@NonNull ImageEntity entity) {
        if (canTurn()) {
            Intent intent = new Intent(activity, PixelActivity.class);
            intent.putExtra(IConfig.KEY_IMAGE_ENTITY, entity);
            activity.startActivityForResult(intent, IConfig.REQUEST_PIXEL_ACTIVITY);
        }
    }

    /*===================================*/

    private void sendUpdateDailyMsg() {
        if (uiHandler.hasMessages(MSG_UPDATE_DAILY)) {
            uiHandler.removeMessages(MSG_UPDATE_DAILY);
        }
        uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_DAILY, 500); // 延迟更新，避免数据库频繁操作导致的UI频繁更新
    }

    private static final int MSG_UPDATE_DAILY = 2010;

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_UPDATE_DAILY) {
            update();
        }
    }
}
