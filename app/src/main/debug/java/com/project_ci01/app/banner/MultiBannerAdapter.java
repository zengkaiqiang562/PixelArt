package com.project_ci01.app.banner;

import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.project_m1142.app.R;
import com.project_m1142.app.base.view.recyclerview.BaseHolder;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.util.BannerUtils;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

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

        public DailyHolder(ViewGroup parent) {
            super(parent, BannerUtils.getView(parent, R.layout.item_banner_daily));
            dailyImage = getView(R.id.image_daily);
        }

        @Override
        public void setData(IBannerItem data, int position) {
            if (!(data instanceof DailyBannerItem)) {
                return;
            }

//            RoundedCornersTransformation transformation =
//                    new RoundedCornersTransformation(ConvertUtils.dp2px(8), 0, RoundedCornersTransformation.CornerType.TOP);
            RequestOptions options = new RequestOptions()
                    .format(DecodeFormat.PREFER_RGB_565)
//                    .error(R.drawable.icon_live_item_default)
//                    .placeholder(R.drawable.icon_live_item_default)
                    .transform(new CenterCrop()/*, transformation*/);

            Glide.with(getContext())
                    .asDrawable()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(options)
                    .load(((DailyBannerItem) data).iconRes)
                    .into(dailyImage);
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
