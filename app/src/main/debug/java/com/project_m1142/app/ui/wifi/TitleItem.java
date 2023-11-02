package com.project_m1142.app.ui.wifi;

import androidx.annotation.StringRes;

public class TitleItem implements IItem {

    public final @StringRes int  titleRes;

    public TitleItem(@StringRes int titleRes) {
        this.titleRes = titleRes;
    }

    @Override
    public int getType() {
        return TYPE_TITLE;
    }
}
