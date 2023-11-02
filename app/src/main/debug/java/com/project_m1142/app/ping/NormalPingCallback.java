package com.project_m1142.app.ping;

public interface NormalPingCallback {
    void onCompleted(NormalPingCommand pingTask, String host, long ping, int index);
    void onFailed(NormalPingCommand pingTask, String host, Exception e, int index);
}
