package com.project_ci01.app.fragment.mine;

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
import com.project_ci01.app.adapter.MineImageAdapter;
import com.project_ci01.app.adapter.mine.EmptyMineItem;
import com.project_ci01.app.adapter.mine.IMineItem;
import com.project_ci01.app.adapter.mine.ImageMineItem;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.databinding.FragmentInProgressBinding;

import java.util.ArrayList;
import java.util.List;

public class InProgressFragment extends BaseFragment implements OnItemClickListener<BaseHolder<IMineItem>>, ImageDbManager.OnImageDbChangedListener {

    private FragmentInProgressBinding binding;
    private MineImageAdapter adapter;

    @Override
    protected String tag() {
        return "InProgressFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentInProgressBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter == null) {
                    return 1;
                }

                List<IMineItem> datas = adapter.getDatas();
                if (datas == null || datas.isEmpty() || position >= datas.size()) {
                    return 1;
                }

                int type = datas.get(position).type();
                if (type == IMineItem.TYPE_EMPTY) {
                    return 2; // empty item 占 2 个item的位置，即独占一行
                } else {
                    return 1;
                }
            }
        });

        // 必须设置后才能嵌套滑动
        binding.inProgressRv.setLayoutManager(gridLayoutManager);
        binding.inProgressRv.setAdapter(adapter = new MineImageAdapter(this, activity));
        binding.inProgressRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            final int dp16 = ConvertUtils.dp2px(16);
            final int dp20 = ConvertUtils.dp2px(20);

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

                if (adapter == null || adapter.getDatas() == null || adapter.getDatas().isEmpty()) {
                    outRect.set(0, 0, 0, 0);
                    return;
                }

                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanIndex = layoutParams.getSpanIndex();
                int spanSize = layoutParams.getSpanSize();
                List<IMineItem> datas = adapter.getDatas();
                int position = parent.getChildLayoutPosition(view);
                int type = datas.get(position).type();

                if (spanSize == 2 && type == IMineItem.TYPE_EMPTY) { // view 为 empty item
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

        update();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void update() {
        ImageDbManager.getInstance().queryInProgress(entities -> {
            if (ContextManager.isSurvival(activity) && adapter != null) {
                List<IMineItem> items = new ArrayList<>();

                if (entities == null || entities.isEmpty()) {
                    items.add(new EmptyMineItem("No pictures in progress"));
                } else {
                    for (ImageEntity entity : entities) {
                        items.add(new ImageMineItem(entity));
                    }
                }
                adapter.setDatasAndNotify(items);
            }
        });
    }

    @Override
    public void onImageDbChanged() {
        sendUpdateInProgressMsg();
    }

    @Override
    public void onItemClick(int item, BaseHolder<IMineItem> holder) {
        if (holder instanceof MineImageAdapter.MineImageHolder) {
            ImageEntity entity = ((MineImageAdapter.MineImageHolder) holder).entity;
            if (entity != null) {
                startPixelActivity(entity);
            }
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

    private void sendUpdateInProgressMsg() {
        if (uiHandler.hasMessages(MSG_UPDATE_IN_PROGRESS)) {
            uiHandler.removeMessages(MSG_UPDATE_IN_PROGRESS);
        }
        uiHandler.sendEmptyMessageDelayed(MSG_UPDATE_IN_PROGRESS, 500); // 延迟更新，避免数据库频繁操作导致的UI频繁更新
    }

    private static final int MSG_UPDATE_IN_PROGRESS = 2006;

    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == MSG_UPDATE_IN_PROGRESS) {
            update();
        }
    }
}
