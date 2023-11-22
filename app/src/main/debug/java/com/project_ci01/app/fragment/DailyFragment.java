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
import com.project_ci01.app.adapter.DailyImageAdapter;
import com.project_ci01.app.adapter.daily.IDailyItem;
import com.project_ci01.app.adapter.daily.ImageDailyItem;
import com.project_ci01.app.banner.DailyBannerItem;
import com.project_ci01.app.banner.IBannerItem;
import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.R;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.base.view.BaseFragment;
import com.project_ci01.app.databinding.FragmentDailyBinding;
import com.sunfusheng.GroupRecyclerViewAdapter;
import com.sunfusheng.StickyHeaderDecoration;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

public class DailyFragment extends BaseFragment implements GroupRecyclerViewAdapter.OnItemClickListener<IDailyItem>, ImageDbManager.OnImageDbChangedListener {

    private FragmentDailyBinding binding;

    private MultiBannerAdapter bannerAdapter;

    private DailyImageAdapter adapter;
    
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
        update(true);
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
            public void onBannerClick(RecyclerView.ViewHolder holder, IBannerItem item, int position) {
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

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter == null) {
                    return 1;
                }

                List<List<IDailyItem>> groups = adapter.getGroups();
                if (groups == null || groups.isEmpty() || position >= adapter.getItemCount()) {
                    return 1;
                }

                int groupPosition = adapter.getGroupPosition(position);
                int childPosition = adapter.getGroupChildPosition(groupPosition, position);
                IDailyItem item = adapter.getItem(groupPosition, childPosition);

                int type = item.type();
                if (type == IDailyItem.TYPE_HEADER) {
                    return 2; // empty item 占 2 个item的位置，即独占一行
                } else {
                    return 1;
                }
            }
        });
        binding.dailyRv.setLayoutManager(gridLayoutManager);
        adapter = new DailyImageAdapter(activity);
        adapter.setOnItemClickListener(this);
        binding.dailyRv.setAdapter(adapter);
        binding.dailyRv.addItemDecoration(new StickyHeaderDecoration() {
            final int dp16 = ConvertUtils.dp2px(16);
            final int dp20 = ConvertUtils.dp2px(20);

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                if (adapter == null || adapter.getGroups() == null || adapter.getGroups().isEmpty()) {
                    outRect.set(0, 0, 0, 0);
                    return;
                }

                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanIndex = layoutParams.getSpanIndex();
                int spanSize = layoutParams.getSpanSize();

                int position = parent.getChildLayoutPosition(view);
                int groupPosition = adapter.getGroupPosition(position);
                int childPosition = adapter.getGroupChildPosition(groupPosition, position);
                if (groupPosition == -1 || childPosition == -1) {
                    outRect.set(0, 0, 0, 0);
                    return;
                }
                IDailyItem item = adapter.getItem(groupPosition, childPosition);
                int type = item.type();

                if (spanSize == 2 && type == IDailyItem.TYPE_HEADER) { // view 为 header item
                    outRect.set(0, 0, 0, 0);
                    return;
                }

                if (spanSize == 1 && spanIndex == 0) { // view 为左侧的 image item
                    outRect.set(dp20, 0, dp16 / 2, 0);
                    return;
                }

                if (spanSize == 1 && spanIndex == 1) { // view 为右侧的 image item
                    outRect.set(dp16 / 2, 0, dp20, 0);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void update(boolean reset) {
        ImageDbManager.getInstance().queryByCategory(Category.DAILY.catName, entities -> {
            if (ContextManager.isSurvival(activity) && adapter != null) {
                adapter.setImageEntities(entities, reset);
            }
        });
    }



    @Override
    public void onImageDbChanged() {
        if (bannerAdapter != null) {
            bannerAdapter.notifyDataSetChanged();
        }
        sendUpdateDailyMsg();
    }

    @Override
    public void onItemClick(GroupRecyclerViewAdapter groupAdapter, IDailyItem data, int groupPosition, int childPosition) {

        if (groupAdapter.isHeader(groupPosition, childPosition)) {
            if (adapter.isExpand(groupPosition)) {
                adapter.collapseGroup(groupPosition, true);
            } else {
                adapter.expandGroup(groupPosition, true);
            }

            // withAnim == true 时
            adapter.updateItem(groupPosition, childPosition, adapter.getItem(groupPosition, childPosition));
            return;
        }

        if (data instanceof ImageDailyItem) {
            startPixelActivity(((ImageDailyItem) data).entity);
        }
    }

    private void startPixelActivity(@NonNull ImageEntityNew entity) {
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
            update(false);
        }
    }
}
