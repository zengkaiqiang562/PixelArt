package com.project_ci01.app.dialog;

import com.blankj.utilcode.util.ConvertUtils;
import com.project_ci01.app.R;
import com.project_ci01.app.banner.guide.GuideBannerAdapter;
import com.project_ci01.app.base.view.dialog.BaseDialog;
import com.youth.banner.Banner;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.indicator.DrawableIndicator;

public class PixelGuideDialog extends BaseDialog {

    @Override
    protected String tag() {
        return "PixelGuideDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pixel_guide;
    }

    @Override
    protected void init() {

        findViewById(R.id.skip).setOnClickListener(v -> {
            dismiss();
        });

        Banner banner = findViewById(R.id.banner);

        GuideBannerAdapter bannerAdapter = new GuideBannerAdapter(GuideBannerAdapter.newItems());
        banner.setAdapter(bannerAdapter);
        banner.setIntercept(false);
        banner.setIndicator(new DrawableIndicator(activity, R.drawable.pixel_guide_indicator_unsel, R.drawable.pixel_guide_indicator_sel));
        banner.setIndicatorNormalWidth(ConvertUtils.dp2px(8));
        banner.setIndicatorSelectedWidth(ConvertUtils.dp2px(8));
        banner.setIndicatorSpace(ConvertUtils.dp2px(8));
        IndicatorConfig.Margins margins = new IndicatorConfig.Margins();
        margins.bottomMargin = ConvertUtils.dp2px(76);
        banner.setIndicatorMargins(margins);
    }

}
