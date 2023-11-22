package com.project_ci01.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.base.utils.MyTimeUtils;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.recyclerview.BaseAdapter;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;

public class HomeImageAdapter extends BaseAdapter<ImageEntityNew, HomeImageAdapter.HomeImageHolder> {

    public HomeImageAdapter(OnItemClickListener<HomeImageHolder> listener, Context context) {
        super(null, listener, context);
    }

    @NonNull
    @Override
    public HomeImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeImageHolder(parent);
    }

    public class HomeImageHolder extends BaseHolder<ImageEntityNew> {

        private final ImageView image;

        private final TextView newTag;

        public ImageEntityNew entity;

        public HomeImageHolder(ViewGroup parent) {
            super(parent, R.layout.item_home_image);
            image = getView(R.id.image);
            newTag = getView(R.id.new_tag);
        }

        @Override
        public void setData(ImageEntityNew data, int position) {
            entity = data;

            if (MyTimeUtils.isInToday(entity.createTime)) {
                newTag.setVisibility(View.VISIBLE);
            } else {
                newTag.setVisibility(View.GONE);
            }

            Glide.with(getContext())
                    .load(entity.colorImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                    .skipMemoryCache(true) // 不走内存缓存
                    .into(image);
        }
    }
}
