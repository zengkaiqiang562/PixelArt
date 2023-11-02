package com.project_m1142.app.wifi.ext;

public enum NetworkState {
    IDLE("Idle"),
    START_CONNECT("Start Connection…"), // <!--开始连接...-->
    SCANNING("Scanning…"),
    AUTHENTICATING("Authentication…"), // <!--身份验证中...-->
    OBTAINING_IPADDR("Get address information…"), // <!--获取地址信息...-->
    CONNECTED("Connected"), // <!--已连接-->
    SUSPENDED("Connection interruption"), // <!--连接中断-->
    DISCONNECTING("On disconnect…"), // <!--断开中...-->
    DISCONNECTED("Disconnect"), // <!--已断开-->
    FAILED("Connection Failed"), // <!--连接失败-->
    BLOCKED("Invalid WiFi"), // <!--wifi无效-->
    VERIFYING_POOR_LINK("Bad Signal"), // <!--信号差-->
    CAPTIVE_PORTAL_CHECK("Forced Portal Login"), // <!--强制登陆门户-->
    SUPPLICANT_ERROR_AUTHENTICATING("PassWord Error"), // <!--密码错误-->
    SUPPLICANT_ERROR_OTHER("Authentication Error"); // <!--身份验证出现问题-->

    public final String desc;

    NetworkState(String desc) {
        this.desc = desc;
    }
}
