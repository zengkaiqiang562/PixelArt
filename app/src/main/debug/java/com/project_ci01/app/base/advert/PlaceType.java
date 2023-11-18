package com.project_ci01.app.base.advert;

import androidx.annotation.Nullable;

public enum PlaceType {
    START("healthstart"),
    Connect("healthconnect"),
    INFO("healthknowledge"),
    HOME("healthhome"),
    REPORT("healthreport"),
    BANNER("healthbanner");

    public final String type;

    PlaceType(String type) {
        this.type = type;
    }

    @Nullable
    public static PlaceType convert(String type) {
        for (PlaceType placeType : values()) {
            if (placeType.type.equals(type)) {
                return placeType;
            }
        }
        return null;
    }
}
