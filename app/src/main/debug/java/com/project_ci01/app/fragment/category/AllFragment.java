package com.project_ci01.app.fragment.category;

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
import com.project_ci01.app.adapter.HomeImageAdapter;
import com.project_ci01.app.base.view.FragmentPresenter;
import com.project_ci01.app.base.view.RefreshPresenter;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;
import com.project_ci01.app.databinding.FragmentAllBinding;
import com.project_ci01.app.fragment.BaseImageFragment;
import com.project_ci01.app.presenter.AllPresenter;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class AllFragment extends BaseImageFragment implements OnItemClickListener<HomeImageAdapter.HomeImageHolder>,
        RefreshPresenter.OnDataChangedListener<ImageEntityNew>, OnRefreshLoadMoreListener {

    private FragmentAllBinding binding;

    private HomeImageAdapter adapter;

    private AllPresenter presenter;

    @Override
    protected String tag() {
        return "AllFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentAllBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AllPresenter(this);
        presenter.setOnDataChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        binding.allRefresh.setOnRefreshLoadMoreListener(this);
        // 必须设置后才能嵌套滑动
        binding.allRv.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.allRv.setAdapter(adapter = new HomeImageAdapter(this, activity));
        binding.allRv.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        tryAutoRefresh();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onImageAdded(String category, int imageId) {
        sendAutoRefreshMsg(500);
    }

    @Override
    public void onImageUpdated(String category, int imageId) {
        updateItem(category, imageId);
    }

    @Override
    public void onItemClick(int item, HomeImageAdapter.HomeImageHolder holder) {
        if (holder.entity != null) {
            if (holder.entity.completed) {
                startCompleteActivity(holder.entity);
            } else {
                startPixelActivity(holder.entity);
            }
        }
    }

    public void tryAutoRefresh() {
        if (ContextManager.isSurvival(activity) && presenter != null && presenter.checkAutoRefresh()) {
            binding.allRefresh.autoRefresh(0, 50, 0.1f ,false);
        }
    }

    public void updateItem(String category, int imageId) {
        if (!Category.DAILY.catName.equals(category)) {
            ImageDbManager.getInstance().queryByImageId(imageId, entities -> {
                if (!ContextManager.isSurvival(activity) || adapter == null || entities.isEmpty()) {
                    return;
                }

                ImageEntityNew entity = entities.get(0);
                List<ImageEntityNew> datas = adapter.getDatas();
                int index = datas.indexOf(entity);
                if (index != -1) {
                    adapter.notifyItemChanged(index);
                }
            });
        }
    }


    /*================== 上拉刷新 & 下拉加载更多 =================*/

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        binding.allRefresh.setEnableLoadMore(true);
        if (presenter != null) {
            presenter.refreshData();
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (presenter != null) {
            presenter.loadMoreData();
        }
    }

    @Override
    public void onDataChanged(ArrayList<ImageEntityNew> data) {
        if (!ContextManager.isSurvival(activity) || presenter == null) {
            return;
        }

        if (binding.allRefresh.isRefreshing()) {
            binding.allRefresh.finishRefresh();
        }
        if (binding.allRefresh.isLoading()) {
            binding.allRefresh.finishLoadMore();
        }

        if (!presenter.hasMoreData()) {
            binding.allRefresh.setEnableLoadMore(false);
        }

        if (adapter != null) {
            List<ImageEntityNew> showData = adapter.getDatas();
            if (showData == null || showData.isEmpty()) { // 第一次加载
                adapter.setDatas(data);
                adapter.notifyItemRangeChanged(0, data.size());
                return;
            }

            if (data.size() < showData.size()) { // 下拉刷新
                adapter.setDatas(data);
                adapter.notifyItemRangeRemoved(data.size(), showData.size() - data.size());
                adapter.notifyItemRangeChanged(0, data.size());
            } else if (data.size() > showData.size()){ // 上拉加载更多
                adapter.setDatas(data);
                adapter.notifyItemRangeChanged(showData.size(), data.size() - showData.size());
            }
        }
    }
    /*=================== handler ================*/

    private void sendAutoRefreshMsg(long delay) {
        if (uiHandler.hasMessages(MSG_AUTO_REFRESH)) {
            return; // 有相同时消息不处理
        }
        uiHandler.sendEmptyMessageDelayed(MSG_AUTO_REFRESH, delay); // 延迟更新，避免数据库频繁操作导致的UI频繁更新
    }

    private static final int MSG_AUTO_REFRESH = 2001;

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_AUTO_REFRESH) {
            tryAutoRefresh();
        }
    }
}
