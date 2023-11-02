package com.project_m1142.app.ui.history;

import com.project_m1142.app.dao.TestHistoryEntity;

public class HistoryItem implements IItem{

    public final TestHistoryEntity entity;

    public HistoryItem(TestHistoryEntity entity) {
        this.entity = entity;
    }

    @Override
    public int getType() {
        return TYPE_HISTORY;
    }
}
