package com.project_ci01.app.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.adapter.mine.EmptyMineItem;
import com.project_ci01.app.adapter.mine.IMineItem;
import com.project_ci01.app.adapter.mine.ImageMineItem;
import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.recyclerview.BaseAdapter;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.project_ci01.app.base.view.recyclerview.OnItemClickListener;

public class MineImageAdapter extends BaseAdapter<IMineItem, BaseHolder<IMineItem>> {

    public MineImageAdapter(OnItemClickListener<BaseHolder<IMineItem>> listener, Context context) {
        super(null, listener, context);
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).type();
    }

    @NonNull
    @Override
    public BaseHolder<IMineItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMineItem.TYPE_EMPTY) {
            return new MineEmptyHolder(parent);
        } else {
            return new MineImageHolder(parent);
        }
    }

    public class MineImageHolder extends BaseHolder<IMineItem> {

        private final ImageView image;

        public ImageEntityNew entity;

        public MineImageHolder(ViewGroup parent) {
            super(parent, R.layout.item_mine_image);
            image = getView(R.id.image);
        }

        @Override
        public void setData(IMineItem item, int position) {

            if (!(item instanceof ImageMineItem)) {
                return;
            }

            entity = ((ImageMineItem) item).entity;

            Glide.with(getContext())
                    .load(entity.colorImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                    .skipMemoryCache(true) // 不走内存缓存
                    .into(image);
        }
    }

    public class MineEmptyHolder extends BaseHolder<IMineItem> {

        private final TextView text;

        public MineEmptyHolder(ViewGroup parent) {
            super(parent, R.layout.item_mine_empty);
            text = getView(R.id.empty_text);
        }

        @Override
        public void setData(IMineItem item, int position) {

            if (!(item instanceof EmptyMineItem)) {
                return;
            }

            text.setText(((EmptyMineItem) item).text);
        }
    }
}
