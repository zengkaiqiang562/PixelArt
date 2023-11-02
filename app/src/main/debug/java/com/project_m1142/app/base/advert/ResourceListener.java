package com.project_m1142.app.base.advert;

public interface ResourceListener {
    void onPrePull(Resource<?> res);

    void onPullFailed(Resource<?> res, int code, String msg);

    void onPullSuccess(Resource<?> res);

    void onShow(Resource<?> res); // 显示中

    void onDismiss(Resource<?> res); // 显示完并消失

    void onUnshow(Resource<?> res, int code, String msg); // 显示失败

    void onExpired(Resource<?> res); // 广告超过有效期

    void onClick(Resource<?> res); // 访问（点击）广告

    void onEarnedReward(Resource<?> res); // 从 激励广告 or 插页式激励广告中 获取到了奖励
}
