package com.project_ci01.app.banner;

import androidx.annotation.DrawableRes;

public class DailyBannerItem implements IBannerItem {

    @Override
    public int getType() {
        return TYPE_DAILY;
    }
}
