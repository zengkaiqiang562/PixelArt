package com.project_m1142.app.base.advert;

import com.project_m1142.app.base.utils.LogUtils;

import java.util.Arrays;

public enum UnitType {
    START("start|open", Provider.MAX),
    INT("int", Provider.MAX),
    NAV("nav", Provider.MAX),
    BAN("ban", Provider.MAX);

    public final String type;

    public final Provider provider;

    UnitType(String type, Provider provider) {
        this.type = type;
        this.provider = provider;
    }

    public static UnitType convert(String type) {
        for (UnitType unitType : values()) {
//            if (unitType.type.equals(type)) {
//                return unitType;
//            }
            String[] split = unitType.type.split("\\|");
            LogUtils.e("UnitType", "--> convert compatTypes=" + Arrays.toString(split) + "  dstType=" + type);
            for (String compatType : split) {
                if (compatType.equals(type)) {
                    return unitType;
                }
            }
        }
        return null;
    }
}
