package com.project_ci01.app.base.advert;

public enum Status {
    PREPARE, // 准备加载
    PULLING, // 正在加载
    PULL_SUCCESS, // 加载成功
    PULL_FAILED, // 加载失败
    SHOW, // 显示成功（并显示出来了）
    DISMISS, // 显示完进入消失状态
    UNSHOW, // 显示失败（没有显示出来）
    RELEASE, // 已释放资源（销毁）
    EXPIRED // 抓取后在有效期限内（1 小时）
}
