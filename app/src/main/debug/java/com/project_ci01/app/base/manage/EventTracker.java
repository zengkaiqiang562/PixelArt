package com.project_ci01.app.base.manage;

//import com.adjust.sdk.Adjust;
//import com.adjust.sdk.AdjustEvent;
import com.blankj.utilcode.util.SPUtils;
import com.project_ci01.app.base.utils.LogUtils;

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
//        trackEvent("ny8ahu", null, true);
    }

    /**
     * 事件: A启动页展示（去重）
     * 事件名: jmqnzl
     * 是否去重: 是
     */
    public static void trackStartShow() {
//        trackEvent("jmqnzl", null, true);
    }


    /**
     * 事件: A首页展示（去重）
     * 事件名: o4x0xj
     * 是否去重: 是
     */
    public static void trackHomeShow() {
//        trackEvent("o4x0xj", null, true);
    }




    /**
     * 事件: 开始请求广告后，回传数据
     * 事件名: gvfxq2
     * 广告单元参数 One-Clickadone、广告位置参数 One-Clickadlocal
     */
    public static void traceAdPreload(String unitId, String place) {
//        LogUtils.e(TAG, "--> traceAdPreload()  unitId=" + unitId + "  place=" + place);
//        Map<String, String> map = new HashMap<>();
//        map.put("One-Clickadone", unitId);
//        map.put("One-Clickadlocal", place);
//        trackEvent("gvfxq2", map, false);
    }

    /**
     * 事件: 广告请求成功后，回传数据
     * 事件名: 6n7qgm
     * 广告单元参数 One-Clickadone、广告位置参数 One-Clickadlocal、收入渠道参数 One-Clickincome
     */
    public static void traceAdPullSuccess(String unitId, String place, String channel) {
//        LogUtils.e(TAG, "--> traceAdPullSuccess()  unitId=" + unitId + "  place=" + place + "  channel=" + channel);
//        Map<String, String> map = new HashMap<>();
//        map.put("One-Clickadone", unitId);
//        map.put("One-Clickadlocal", place);
//        map.put("One-Clickincome", channel);
//        trackEvent("6n7qgm", map, false);
    }

    /**
     * 事件: 广告点击后，回传数据
     * 事件名: 7gy4zl
     * 广告单元参数 One-Clickadone、广告位置参数 One-Clickadlocal、收入渠道参数 One-Clickincome
     */
    public static void traceAdClick(String unitId, String place, String channel) {
//        LogUtils.e(TAG, "--> traceAdClick()  unitId=" + unitId + "  place=" + place + "  channel=" + channel);
//        Map<String, String> map = new HashMap<>();
//        map.put("One-Clickadone", unitId);
//        map.put("One-Clickadlocal", place);
//        map.put("One-Clickincome", channel);
//        trackEvent("7gy4zl", map, false);
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
//        if (unique && SPUtils.getInstance().getBoolean(event)) {
//            LogUtils.e(TAG, "trackEvent() -->  unique event " + event + "  has been REPORT !!!");
//            return; // 去重事件已经上报过，不再上报
//        }
//
//        AdjustEvent adjustEvent = new AdjustEvent(event);
//        if (unique) {
//            adjustEvent.setOrderId(event);
//        }
//        if (params != null && !params.isEmpty()) {
//            for (Map.Entry<String, String> entry : params.entrySet()) {
//                adjustEvent.addCallbackParameter(entry.getKey(), entry.getValue());
//            }
//        }
//        Adjust.trackEvent(adjustEvent);
//
//        if (unique) {
//            SPUtils.getInstance().put(event, true);
//        }
    }
}
