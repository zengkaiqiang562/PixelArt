package com.project_m1142.app.base.common;

public interface CommonCallback<T> {
    void onFailed(int code, String msg);
    void onSuccess(T bean);
}
