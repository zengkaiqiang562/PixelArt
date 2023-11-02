package com.project_m1142.app.base.event;

import com.project_m1142.app.base.bean.event.BaseEvent;

import org.greenrobot.eventbus.EventBus;

public class EventBusHelper {
    public static void register(IEventListener listener) {
        if (!EventBus.getDefault().isRegistered(listener)) {
            EventBus.getDefault().register(listener);
        }
    }

    public static void unregister(IEventListener listener) {
        if (EventBus.getDefault().isRegistered(listener)) {
            EventBus.getDefault().unregister(listener);
        }
    }

    public static <T> void post(BaseEvent<T> eventBean, boolean sticky) {
        if (sticky) {
            EventBus.getDefault().postSticky(eventBean);
        } else {
            EventBus.getDefault().post(eventBean);
        }
    }

    public static void removeStickyEvent(Class<? extends BaseEvent<?>> eventClass) {
        EventBus.getDefault().removeStickyEvent(eventClass);
    }
}
