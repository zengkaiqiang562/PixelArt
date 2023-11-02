package com.project_m1142.app.base.bean.event;

import androidx.annotation.Nullable;

import com.project_m1142.app.base.bean.gson.ConfigBean;
import com.project_m1142.app.base.event.EventType;


public class ConfigEvent extends BaseEvent<ConfigBean> {

    public ConfigEvent(@Nullable ConfigBean data) {
        super(data, EventType.TYPE_UPDATE_CONFIG);
    }
}
