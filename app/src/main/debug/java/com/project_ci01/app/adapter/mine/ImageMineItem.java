package com.project_ci01.app.adapter.mine;

import com.project_ci01.app.dao.ImageEntityNew;

public class ImageMineItem implements IMineItem {

    public final ImageEntityNew entity;

    public ImageMineItem(ImageEntityNew entity) {
        this.entity = entity;
    }

    @Override
    public int type() {
        return TYPE_IMAGE;
    }
}
