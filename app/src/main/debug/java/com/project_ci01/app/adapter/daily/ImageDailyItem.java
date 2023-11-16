package com.project_ci01.app.adapter.daily;

import com.project_ci01.app.dao.ImageEntity;

public class ImageDailyItem implements IDailyItem {

    public ImageEntity entity;

    public ImageDailyItem(ImageEntity entity) {
        this.entity = entity;
    }

    @Override
    public int type() {
        return TYPE_IMAGE;
    }
}
