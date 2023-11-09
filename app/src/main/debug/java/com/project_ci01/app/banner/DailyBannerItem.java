package com.project_ci01.app.banner;

import androidx.annotation.DrawableRes;

public class DailyBannerItem implements IBannerItem {

    public final @DrawableRes int iconRes;

    public DailyBannerItem(int iconRes) {
        this.iconRes = iconRes;
    }

    @Override
    public int getType() {
        return TYPE_DAILY;
    }
}
