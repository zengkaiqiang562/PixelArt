package com.project_ci01.app.fragment.category;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.activity.PixelActivity;
import com.project_ci01.app.adapter.HomeImageAdapter;
import com.project_ci01.app.banner.MultiBannerAdapter;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.base.view.BaseFragment;
import com.project_m1142.app.base.view.recyclerview.OnItemClickListener;
import com.project_m1142.app.databinding.FragmentLoveBinding;

public class LoveFragment extends BaseFragment implements OnItemClickListener<HomeImageAdapter.HomeImageHolder> {

    private FragmentLoveBinding binding;

    private HomeImageAdapter adapter;
    
    @Override
    protected String tag() {
        return "LoveFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = FragmentLoveBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        // 必须设置后才能嵌套滑动
        binding.loveRv.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.loveRv.setAdapter(adapter = new HomeImageAdapter(this, activity));
        binding.loveRv.addItemDecoration(new RecyclerView.ItemDecoration() {
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

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public void update() {
        ImageDbManager.getInstance().queryByCategory(Category.LOVE.catName, entities -> {
            adapter.setDatasAndNotify(entities);
        });
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
}
