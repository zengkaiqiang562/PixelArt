package com.project_ci01.app.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.dao.ImageEntity;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.recyclerview.BaseAdapter;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;

public class HomeImageAdapter extends BaseAdapter<ImageEntity, HomeImageAdapter.HomeImageHolder> {

    public HomeImageAdapter(OnItemClickListener<HomeImageHolder> listener, Context context) {
        super(null, listener, context);
    }

    @NonNull
    @Override
    public HomeImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeImageHolder(parent);
    }

    public class HomeImageHolder extends BaseHolder<ImageEntity> {

        private final ImageView image;

        public ImageEntity entity;

        public HomeImageHolder(ViewGroup parent) {
            super(parent, R.layout.item_home_image);
            image = getView(R.id.image);
        }

        @Override
        public void setData(ImageEntity data, int position) {
            entity = data;

            Glide.with(getContext())
                    .load(entity.colorImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                    .skipMemoryCache(true) // 不走内存缓存
                    .into(image);
        }
    }
}
