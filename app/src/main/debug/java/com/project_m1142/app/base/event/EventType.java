package com.project_m1142.app.base.event;

public enum EventType {
    TYPE_AD_READY,
    TYPE_AD_PULLED,
    TYPE_AD_UNPULL, // 拉取失败
    TYPE_AD_SHOW,
    TYPE_AD_UNSHOW,
    TYPE_AD_DISMISS,
    TYPE_AD_REWARD, // 获取到奖励
    TYPE_UPDATE_CONFIG,
    TYPE_UPGRADE,

    /*======================*/
    TYPE_CONNECT_STATUS,
    TYPE_CONNECTED_TIMER,
    TYPE_SERVER_LIST
}
