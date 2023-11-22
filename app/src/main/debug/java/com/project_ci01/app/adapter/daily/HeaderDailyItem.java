package com.project_ci01.app.adapter.daily;

import com.project_ci01.app.dao.ImageEntityNew;

import java.util.List;

public class HeaderDailyItem implements IDailyItem {

    public final String month;

    public final List<ImageEntityNew> entities;

    public HeaderDailyItem(String month, List<ImageEntityNew> entities) {
        this.month = month;
        this.entities = entities;
    }

    @Override
    public int type() {
        return TYPE_HEADER;
    }
}
