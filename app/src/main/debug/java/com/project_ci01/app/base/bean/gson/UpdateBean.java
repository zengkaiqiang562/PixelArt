package com.project_ci01.app.base.bean.gson;

import com.google.gson.annotations.SerializedName;

public class UpdateBean {
    @SerializedName("force")
    private boolean force; // 是否强制更新

    @SerializedName("version")
    private int version; // 版本号

    @SerializedName("package")
    private String _package; // 更新包名

    @SerializedName("title")
    private String title; // 更新标题

    @SerializedName("message")
    private String message; // 弹窗信息


    public boolean isForce() {
        return force;
    }

    public int getVersion() {
        return version;
    }

    public String getPackage() {
        return _package;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "UpdateBean{" +
                "force=" + force +
                ", version=" + version +
                ", package='" + _package + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
