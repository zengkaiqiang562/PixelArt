package com.project_m1142.app.base.manage;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.blankj.utilcode.util.SPUtils;
import com.project_m1142.app.base.utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class EventTracker {
    private static final String TAG = "EventTracker";

    /**
     * 事件: A隐私页展示（去重）
     * 事件名: ny8ahu
     * 是否去重: 是
     */
    public static void trackPrivacyShow() {
        trackEvent("ny8ahu", null, true);
    }

    /**
     * 事件: A启动页展示（去重）
     * 事件名: jmqnzl
     * 是否去重: 是
     */
    public static void trackStartShow() {
        trackEvent("jmqnzl", null, true);
    }


    /**
     * 事件: A首页展示（去重）
     * 事件名: o4x0xj
     * 是否去重: 是
     */
    public static void trackHomeShow() {
        trackEvent("o4x0xj", null, true);
    }

    /**
     * 事件: A连接按钮点击（去重）
     * 事件名: tokrls
     * 是否去重: 是
     */
    public static void trackConnectClick() {
        trackEvent("tokrls", null, true);
    }

    /**
     * 事件: A报告页展示成功（去重）  失败、成功、结果报告页展示成功后，回传数据
     * 事件名: j8i5di
     * 是否去重: 是
     */
    public static void trackReportShow() {
        trackEvent("j8i5di", null, true);
    }

    /**
     * 事件: A成功报告页（去重）
     * 事件名: pxk9dv
     * 是否去重: 是
     */
    public static void trackConnectedReportShow() {
        trackEvent("pxk9dv", null, true);
    }

    /**
     * 事件: A失败报告页（去重）
     * 事件名: ib2umj
     * 是否去重: 是
     */
    public static void trackUnconnectReportShow() {
        trackEvent("ib2umj", null, true);
    }

    /**
     * 事件: B更多页面（去重）
     * 事件名: 74q5uj
     * 是否去重: 是
     */
    public static void trackMoreShow() {
        trackEvent("74q5uj", null, true);
    }

    /**
     * 事件: Bwifi页面（去重）
     * 事件名: w7d57e
     * 是否去重: 是
     */
    public static void trackWifiShow() {
        trackEvent("w7d57e", null, true);
    }

    /**
     * 事件: Bwifi页面弹窗（去重）   WiFi页面，点击要连接的wifi出现弹窗
     * 事件名: br7d44
     * 是否去重: 是
     */
    public static void trackWifiDialogShow() {
        trackEvent("br7d44", null, true);
    }

    /**
     * 事件: B等待动画（去重）
     * 事件名: vwk12y
     * 是否去重: 是
     */
    public static void trackWifiConnectingShow() {
        trackEvent("vwk12y", null, true);
    }

    /**
     * 事件: B测试页面（去重）
     * 事件名: guufg6
     * 是否去重: 是
     */
    public static void trackTestShow() {
        trackEvent("guufg6", null, true);
    }

    /**
     * 事件: B测试动画（去重）
     * 事件名: 4r4g5p
     * 是否去重: 是
     */
    public static void trackTestExecShow() {
        trackEvent("4r4g5p", null, true);
    }

    /**
     * 事件: B测试结果页面（去重）
     * 事件名: nws8py
     * 是否去重: 是
     */
    public static void trackTestResultShow() {
        trackEvent("nws8py", null, true);
    }





    /**
     * 事件: 开始请求广告后，回传数据
     * 事件名: gvfxq2
     * 广告单元参数 One-Clickadone、广告位置参数 One-Clickadlocal
     */
    public static void traceAdPreload(String unitId, String place) {
        LogUtils.e(TAG, "--> traceAdPreload()  unitId=" + unitId + "  place=" + place);
        Map<String, String> map = new HashMap<>();
        map.put("One-Clickadone", unitId);
        map.put("One-Clickadlocal", place);
        trackEvent("gvfxq2", map, false);
    }

    /**
     * 事件: 广告请求成功后，回传数据
     * 事件名: 6n7qgm
     * 广告单元参数 One-Clickadone、广告位置参数 One-Clickadlocal、收入渠道参数 One-Clickincome
     */
    public static void traceAdPullSuccess(String unitId, String place, String channel) {
        LogUtils.e(TAG, "--> traceAdPullSuccess()  unitId=" + unitId + "  place=" + place + "  channel=" + channel);
        Map<String, String> map = new HashMap<>();
        map.put("One-Clickadone", unitId);
        map.put("One-Clickadlocal", place);
        map.put("One-Clickincome", channel);
        trackEvent("6n7qgm", map, false);
    }

    /**
     * 事件: 广告点击后，回传数据
     * 事件名: 7gy4zl
     * 广告单元参数 One-Clickadone、广告位置参数 One-Clickadlocal、收入渠道参数 One-Clickincome
     */
    public static void traceAdClick(String unitId, String place, String channel) {
        LogUtils.e(TAG, "--> traceAdClick()  unitId=" + unitId + "  place=" + place + "  channel=" + channel);
        Map<String, String> map = new HashMap<>();
        map.put("One-Clickadone", unitId);
        map.put("One-Clickadlocal", place);
        map.put("One-Clickincome", channel);
        trackEvent("7gy4zl", map, false);
    }

    //==========================================================================================//


    /**
     * 事件: 买量用户
     * 事件名: m4o8zp
     * 是否去重: 是
     */
    public static void trackOtherUser() {
//        trackEvent("m4o8zp", null, true);
    }

    /**
     * 事件: 自然用户
     * 事件名: 32x9pt
     * 是否去重: 是
     */
    public static void trackNormalUser() {
//        trackEvent("32x9pt", null, true);
    }

    /**
     * 事件: 归类失败
     * 事件名: 6pt7rj
     * 是否去重: 是
     */
    public static void trackParseUserFailed() {
//        trackEvent("6pt7rj", null, true);
    }

    //==========================================================================================//


    private static void trackEvent(String event, Map<String, String> params, boolean unique) {
        if (unique && SPUtils.getInstance().getBoolean(event)) {
            LogUtils.e(TAG, "trackEvent() -->  unique event " + event + "  has been REPORT !!!");
            return; // 去重事件已经上报过，不再上报
        }

        AdjustEvent adjustEvent = new AdjustEvent(event);
        if (unique) {
            adjustEvent.setOrderId(event);
        }
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                adjustEvent.addCallbackParameter(entry.getKey(), entry.getValue());
            }
        }
        Adjust.trackEvent(adjustEvent);

        if (unique) {
            SPUtils.getInstance().put(event, true);
        }
    }
}
