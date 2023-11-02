package com.project_m1142.app.base.bean.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.base.event.EventType;

public abstract class BaseEvent<T> {
    protected T data;

    protected EventType type;

    protected BaseEvent(@Nullable T data, @NonNull EventType type) {
        this.data = data;
        this.type = type;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @NonNull
    public EventType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{" +
                "data=" + data +
                ", type=" + type +
                '}';
    }
}
