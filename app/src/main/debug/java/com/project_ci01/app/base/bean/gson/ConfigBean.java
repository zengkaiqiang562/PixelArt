package com.project_ci01.app.base.bean.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigBean {

//    /**
//     * 获取配置时间间隔(分钟)
//     */
//    @SerializedName("healthConfigTime")
//    private int configTime;
//
//    /**
//     * 启动页时长上限（秒）
//     */
//    @SerializedName("healthLoadingTime")
//    private int launchTime;
//
//    /**
//     * 功能额外广告开关。
//     */
//    @SerializedName("healthExtraEnable")
//    private boolean enableFuncExtra;
//
//    /**
//     * 首页额外广告开关。
//     */
//    @SerializedName("healthExtrafirstpage")
//    private boolean enableHomeExtra;
//
//    /**
//     * Tap栏额外广告开关。
//     */
//    @SerializedName("healthExtratap")
//    private boolean enableTabExtra;
//
//    /**
//     * 归类失败是否为自然用户（新增）
//     * 打开则为自然用户，关闭为买量用户，默认打开
//     */
//    @SerializedName("organicUser")
//    private Boolean organicUser;

    @SerializedName("adConfigs")
    private List<PlaceBean> placeBeans;

    @SerializedName("update")
    private UpdateBean updateBean;

    public List<PlaceBean> getPlaceBeans() {
        return placeBeans;
    }

    public UpdateBean getUpdateBean() {
        return updateBean;
    }

//    public int getConfigTime() {
//        return configTime;
//    }
//
//    public int getLaunchTime() {
//        return launchTime;
//    }
//
//    public boolean isFuncExtraEnable() {
//        return enableFuncExtra;
//    }
//
//    public boolean isHomeExtraEnable() {
//        return enableHomeExtra;
//    }
//
//    public boolean isTabExtraEnable() {
//        return enableTabExtra;
//    }
//
//    public boolean isOrganicUser() {
//        return organicUser == null || organicUser; // 默认打开
//    }


    @Override
    public String toString() {
        return "ConfigBean{" +
                "placeBeans=" + placeBeans +
                ", updateBean=" + updateBean +
                '}';
    }
}
