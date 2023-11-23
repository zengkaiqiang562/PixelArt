package com.project_ci01.app.banner;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.project_ci01.app.R;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.MyTimeUtils;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.project_ci01.app.dao.Category;
import com.project_ci01.app.dao.ImageDbManager;
import com.project_ci01.app.dao.ImageEntityNew;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;

import java.util.Locale;

public class MultiBannerAdapter extends BannerAdapter<IBannerItem, BaseHolder<IBannerItem>> {

    public MultiBannerAdapter() {
        super(null); // 自己调用 setDatas 设置数据
    }

    @Override
    public int getItemViewType(int position) {
        return getRealData(position).getType();
    }

    @Override
    public BaseHolder<IBannerItem> onCreateHolder(ViewGroup parent, int viewType) {
        if (viewType == IBannerItem.TYPE_CREATE) {
            return new CreateHolder(parent);
        } else {
            return new DailyHolder(parent); // 默认 DailyHolder
        }
    }

    @Override
    public void onBindView(BaseHolder<IBannerItem> holder, IBannerItem data, int position, int size) {
        holder.setData(data, position);
    }

    public class DailyHolder extends BaseHolder<IBannerItem> {

        private final ImageView dailyImage;

        private final TextView date;

        public ImageEntityNew entity;

        public DailyHolder(ViewGroup parent) {
            super(parent, BannerUtils.getView(parent, R.layout.item_banner_daily));
            dailyImage = getView(R.id.image_daily);
            date = getView(R.id.date);
        }

        @Override
        public void setData(IBannerItem data, int position) {
            if (!(data instanceof DailyBannerItem)) {
                return;
            }

            ImageDbManager.getInstance().queryDailyInToday(entities -> {
                if (!ContextManager.isSurvival(getContext()) || entities.isEmpty()) {
                    return;
                }

                entity = entities.get(0);

                String fitDate = MyTimeUtils.millis2String(entity.createTime, "MMMM dd", Locale.ENGLISH);
                date.setText(fitDate);

                Glide.with(getContext())
                        .load(entity.colorImagePath)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                        .skipMemoryCache(true) // 不走内存缓存
                        .into(dailyImage);
            });
        }
    }

    public class CreateHolder extends BaseHolder<IBannerItem> {

        public CreateHolder(ViewGroup parent) {
            super(parent, BannerUtils.getView(parent, R.layout.item_banner_create));
        }

        @Override
        public void setData(IBannerItem data, int position) {
            if (!(data instanceof CreateBannerItem)) {
                return;
            }
        }
    }
}
