package com.project_ci01.app.banner.guide;

import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.recyclerview.BaseHolder;
import com.youth.banner.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideBannerAdapter extends BannerAdapter<GuideBannerItem, GuideBannerAdapter.GuideHolder> {

    public static List<GuideBannerItem> newItems() {
        List<GuideBannerItem> items = new ArrayList<>();
        items.add(new GuideBannerItem("lottie/pixel_guide_scale.json", "Zoom in until you can see the numbers"));
        items.add(new GuideBannerItem("lottie/pixel_guide_color.json", "Choose a color, find the correct number, and tap to fill"));
        items.add(new GuideBannerItem("lottie/pixel_guide_swipe.json", "Double-tap to switch color, long press to fill quickly"));
        return items;
    }

    public GuideBannerAdapter(List<GuideBannerItem> datas) {
        super(datas);
    }

    @Override
    public GuideHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new GuideHolder(parent);
    }

    @Override
    public void onBindView(GuideHolder holder, GuideBannerItem data, int position, int size) {
        holder.setData(data, position);
    }

    public class GuideHolder extends BaseHolder<GuideBannerItem> {

        private final LottieAnimationView animView;
        private final TextView tvTip;

        public GuideHolder(ViewGroup parent) {
            super(parent, R.layout.item_banner_pixel_guide);
            animView = getView(R.id.anim_guide);
            tvTip = getView(R.id.tip_guide);
        }

        @Override
        public void setData(GuideBannerItem data, int position) {
            if (data == null) {
                return;
            }
            if (animView.isAnimating()) {
                animView.cancelAnimation();
            }
            animView.setAnimation(data.animFilePath);
            animView.setRepeatCount(LottieDrawable.INFINITE);
            animView.playAnimation();

            tvTip.setText(data.tip);
        }
    }
}
