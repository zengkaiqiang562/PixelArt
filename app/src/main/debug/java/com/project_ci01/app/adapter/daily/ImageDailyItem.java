package com.project_ci01.app.adapter.daily;

import com.project_ci01.app.dao.ImageEntityNew;

public class ImageDailyItem implements IDailyItem {

    public ImageEntityNew entity;

    public ImageDailyItem(ImageEntityNew entity) {
        this.entity = entity;
    }

    @Override
    public int type() {
        return TYPE_IMAGE;
    }
}
