package com.project_ci01.app.adapter.daily;

import com.project_ci01.app.dao.ImageEntity;

import java.util.List;

public class HeaderDailyItem implements IDailyItem {

    public final String month;

    public final List<ImageEntity> entities;

    public HeaderDailyItem(String month, List<ImageEntity> entities) {
        this.month = month;
        this.entities = entities;
    }

    @Override
    public int type() {
        return TYPE_HEADER;
    }
}
