package com.project_ci01.app.base.common;

public interface CommonCallback<T> {
    void onFailed(int code, String msg);
    void onSuccess(T bean);
}
