package com.project_ci01.app.adapter.mine;

import com.project_ci01.app.dao.ImageEntity;

public class ImageMineItem implements IMineItem {

    public final ImageEntity entity;

    public ImageMineItem(ImageEntity entity) {
        this.entity = entity;
    }

    @Override
    public int type() {
        return TYPE_IMAGE;
    }
}
