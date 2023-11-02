package com.project_m1142.app.base.bean.event;

import androidx.annotation.Nullable;

import com.project_m1142.app.base.bean.gson.UpdateBean;
import com.project_m1142.app.base.event.EventType;

public class UpgradeEvent extends BaseEvent<UpdateBean> {

    public UpgradeEvent(@Nullable UpdateBean data) {
        super(data, EventType.TYPE_UPGRADE);
    }
}
