package com.project_ci01.app.base.user;

import androidx.annotation.Nullable;

public class UserService {
    private volatile static UserService service;

    private User user;

    private UserService() {}

    public static UserService getService() {
        if (service == null) {
            synchronized (UserService.class) {
                if (service == null) {
                    service = new UserService();
                }
            }
        }
        return service;
    }

    public User getUser() {
//        if (user == null) {
//            int type = SPUtils.getInstance().getInt(SPConstants.SP_USER_TYPE, 0);
//            user = User.convert(type);
//        }
//        LogUtils.e("UserService", "--> user=" + user);
//        return user;
        return User.ORGANIC; // TODO 第一版不接广告，固定自然用户
    }

    public void parseUser(@Nullable String referrerUrl) {

//        if (!TextUtils.isEmpty(referrerUrl) && referrerUrl.contains("organic")) { // 自然用户
//            user = User.ORGANIC;
//            SPUtils.getInstance().put(SPConstants.SP_USER_TYPE, user.getType());
//            EventTracker.trackNormalUser();
//            return;
//        }
//
//        // adjust_reftag=cdwk90IkyJvfN&utm_source=MTG&utm_campaign={uuid}&utm_content={affiliate_id}&utm_term={creative_name}
//        if (!TextUtils.isEmpty(referrerUrl) &&
//                (StringUtils.containsIgnoreCase(referrerUrl, "facebook") || StringUtils.containsIgnoreCase(referrerUrl, "google") || StringUtils.containsIgnoreCase(referrerUrl, "tiktok")
//                || StringUtils.containsIgnoreCase(referrerUrl, "meta") || StringUtils.containsIgnoreCase(referrerUrl, "bigo") || StringUtils.containsIgnoreCase(referrerUrl, "kwai")
//                        || StringUtils.containsIgnoreCase(referrerUrl, "mtg") || StringUtils.containsIgnoreCase(referrerUrl, "applovin"))) { // 买量用户
//            user = User.OTHER;
//            SPUtils.getInstance().put(SPConstants.SP_USER_TYPE, user.getType());
//            EventTracker.trackOtherUser();
//            return;
//        }
//
//        // 执行到这里说明：归类失败（无法判断是自然用户，还是买量用户，此时通过后台开关来判断是划为自然用户还是买量用户）
//        user = ConfigManager.isOrganicUser() ? User.ORGANIC : User.OTHER;
//        SPUtils.getInstance().put(SPConstants.SP_USER_TYPE, user.getType());
//        EventTracker.trackParseUserFailed();
    }
}
