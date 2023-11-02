package com.project_m1142.app.base.bean.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.base.advert.Resource;
import com.project_m1142.app.base.event.EventType;


public class AdResourceEvent extends BaseEvent<Resource<?>> {

    public AdResourceEvent(@Nullable Resource<?> res, @NonNull EventType type) {
        super(res, type);
    }
}
