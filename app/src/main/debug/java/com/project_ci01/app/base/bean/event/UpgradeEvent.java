package com.project_ci01.app.base.bean.event;

import androidx.annotation.Nullable;

import com.project_ci01.app.base.bean.gson.UpdateBean;
import com.project_ci01.app.base.event.EventType;

public class UpgradeEvent extends BaseEvent<UpdateBean> {

    public UpgradeEvent(@Nullable UpdateBean data) {
        super(data, EventType.TYPE_UPGRADE);
    }
}
