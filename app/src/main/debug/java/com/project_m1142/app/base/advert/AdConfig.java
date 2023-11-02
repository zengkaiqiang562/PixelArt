package com.project_m1142.app.base.advert;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project_m1142.app.base.bean.gson.PlaceBean;
import com.project_m1142.app.base.bean.gson.ConfigBean;
import com.project_m1142.app.base.bean.gson.UnitBean;
import com.project_m1142.app.base.manage.ConfigManager;
import com.project_m1142.app.base.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class AdConfig {

    private static final String TAG = "AdConfig";

    /**
     * @return 返回所有的广告位
     */
    public static List<PlaceBean> getAllPlaceBeans() {
        ConfigBean configBean = ConfigManager.INSTANCE.getConfigBean();
        return configBean == null ? null : configBean.getPlaceBeans();
    }

//    /**
//     * @return 返回指定广告位上的广告类型
//     */
//    public static UnitType getUnitType(@NonNull PlaceType placeType) {
//
//        List<PlaceBean> placeBeans = getAllPlaceBeans();
//
//        if (placeBeans == null || placeBeans.size() <= 0) {
//            return null;
//        }
//
//        for (PlaceBean placeBean : placeBeans) {
//            if (!placeType.type.equals(placeBean.getPlace()) || placeBean.getUnitBean() == null) {
//                continue;
//            }
//            return UnitType.convert(placeBean.getUnitBean().getType());
//        }
//
//        return null;
//    }

    /**
     * @return 从指定广告位中获取指定类型的广告单元集合
     */
    @NonNull
    public static List<UnitBean> getSortUnitBeans(@NonNull PlaceType placeType, @NonNull UnitType unitType) {
        List<UnitBean> result = new ArrayList<>();
        PlaceBean placeBean = getPlaceBean(placeType);

        if (placeBean == null) {
            return result;
        }

        List<UnitBean> sortUnitBeans = placeBean.getSortUnitBeans();

        if (sortUnitBeans == null || sortUnitBeans.isEmpty()) {
            return result;
        }

        for (UnitBean unitBean : sortUnitBeans) {
            if (unitType.type.equals(unitBean.getType())) {
                result.add(unitBean);
            }
        }

        return result;
    }

    /**
     * @return 从指定广告位中获取指定类型的广告单元集合中权重最高的那个
     */
    @Nullable
    public static UnitBean getMostWeightUnitBean(@NonNull PlaceType placeType, @NonNull UnitType unitType) {
        List<UnitBean> sortUnitBeans = getSortUnitBeans(placeType, unitType);
        if (sortUnitBeans.isEmpty()) {
            return null;
        }
        return sortUnitBeans.get(0);
    }

    /**
     * @return 返回指定广告位上不兼容的广告单元类型
     */
    public static List<UnitType> getUncompatUnitTypes(@NonNull PlaceType placeType) {
        List<UnitType> result = new ArrayList<>();
        switch (placeType) {
            case START:
                result.add(UnitType.NAV);
                result.add(UnitType.BAN);
                break;
            case Connect:
            case INFO:
                result.add(UnitType.START);
                result.add(UnitType.NAV);
                result.add(UnitType.BAN);
                break;
            case HOME:
            case REPORT:
            case BANNER:
                result.add(UnitType.START);
                result.add(UnitType.INT);
                break;
        }
        return result;
    }


    /**
     * @return 返回与指定广告位相互兼容的广告位集合（包括指定广告位本身）
     * 返回集合中首元素为指定广告位本身
     */
    public static List<PlaceType> getCompatPlaceTypes(@NonNull PlaceType placeType) {
        List<PlaceType> result = new ArrayList<>();
        switch (placeType) {
            case START:
                result.add(PlaceType.START);
                result.add(PlaceType.Connect);
                result.add(PlaceType.INFO);
                break;
            case Connect:
                result.add(PlaceType.Connect);
                result.add(PlaceType.START);
                result.add(PlaceType.INFO);
                break;
            case INFO:
                result.add(PlaceType.INFO);
                result.add(PlaceType.Connect);
                result.add(PlaceType.START);
                break;
            case HOME:
                result.add(PlaceType.HOME);
                result.add(PlaceType.REPORT);
                result.add(PlaceType.BANNER);
                break;
            case REPORT:
                result.add(PlaceType.REPORT);
                result.add(PlaceType.HOME);
                result.add(PlaceType.BANNER);
                break;
            case BANNER:
                result.add(PlaceType.BANNER);
                result.add(PlaceType.HOME);
                result.add(PlaceType.REPORT);
                break;
        }
        return result;
    }

    /**
     * @return 返回指定广告位实体类
     */
    public static PlaceBean getPlaceBean(@NonNull PlaceType placeType) {

        List<PlaceBean> placeBeans = getAllPlaceBeans();

        if (placeBeans == null || placeBeans.size() <= 0) {
            return null;
        }

        for (PlaceBean placeBean : placeBeans) {
            if (!placeType.type.equals(placeBean.getPlace())) {
                continue;
            }

            if (!placeBean.isEnable()) {
                LogUtils.d("<" + placeBean.getPlace() + ">广告关闭");
                continue;
            }

            return placeBean;
        }
        return null;
    }

    /**
     * @return 指定广告位是否已打开（默认关闭）
     */
    public static boolean isPlaceOpen(@NonNull PlaceType placeType) {

        List<PlaceBean> placeBeans = getAllPlaceBeans();

        if (placeBeans == null || placeBeans.size() <= 0) {
            return false;
        }

        for (PlaceBean placeBean : placeBeans) {
            if (!placeType.type.equals(placeBean.getPlace())) {
                continue;
            }
            return placeBean.isEnable();
        }

        return false;
    }

//    /**
//     * @return 首次启动时是否显示 start 位广告 （默认启动）
//     */
//    public static boolean isEnableSplashBillOnBirth() {
//        ProfileEntity profileEntity = ProfileService.getInstance().getProfile();
//        return profileEntity == null || profileEntity.isEnableSplashBillOnBirth();
//    }

//    /**
//     * @return 是否展示功能额外广告 （默认不展示）
//     */
//    public static boolean isFuncExtraEnable() {
//        ConfigBean configBean = ConfigManager.INSTANCE.getConfigBean();
//        return configBean != null && configBean.isFuncExtraEnable();
//    }
//
//    /**
//     * @return 是否展示首页额外广告 （默认不展示）
//     */
//    public static boolean isHomeExtraEnable() {
//        ConfigBean configBean = ConfigManager.INSTANCE.getConfigBean();
//        return configBean != null && configBean.isHomeExtraEnable();
//    }
//
//    /**
//     * @return 是否展示Tab额外广告 （默认不展示）
//     */
//    public static boolean isTabExtraEnable() {
//        ConfigBean configBean = ConfigManager.INSTANCE.getConfigBean();
//        return configBean != null && configBean.isTabExtraEnable();
//    }

    public static String getAdChannel(String adapter) {
        String channel = ""; // 广告源渠道

        if (TextUtils.isEmpty(adapter)) {
            return channel;
        }

        if (adapter.contains("AdMobAdapter")) {
            channel = "admob";
        } else if (adapter.contains("adcolony")) {
            channel = "adcolony";
        } else if (adapter.contains("chartboost")) {
            channel = "chartboost";
        } else if (adapter.contains("inmobi")) {
            channel = "inmobi";
        } else if (adapter.contains("ironsource")) {
            channel = "ironsource";
        } else if (adapter.contains("pangle")) {
            channel = "pangle";
        } else if (adapter.contains("unity")) {
            channel = "unity";
        } else if (adapter.contains("vungle")) {
            channel = "vungle";
        } else if (adapter.contains(".mtg")) {
            channel = "mintegral";
        }

        return channel;
    }

//    /**
//     * @return 报告页原生广告是否在第一位 （默认 false，不在）
//     */
//    public static boolean isNavFavor() {
//        ProfileEntity profileEntity = ProfileService.getInstance().getProfile();
//        return profileEntity != null && profileEntity.isNavFavor();
//    }

//    /**
//     * @return 当日内，原生广告的访问（点击）次数上限（默认无上限）
//     */
//    public static int getNavMaxClickTimes() {
//        ProfileEntity profileEntity = ProfileService.getInstance().getProfile();
//
//        int navMaxClickTimes = profileEntity == null ? Integer.MAX_VALUE : profileEntity.getNavMaxClickTimes();
//
//        Slog.e(TAG, "--> getNavMaxClickTimes()  navMaxClickTimes=" + navMaxClickTimes);
//        return navMaxClickTimes;
//    }
}
