package com.project_ci01.app.dao;

public enum Category {
    UNKNOWN("unknown"),
    LOVE("Love"),
    CARTOON("Cartoon"),
    FOOD("Food"),
    DAILY("Daily");

    public final String catName;

    Category(String catName) {
        this.catName = catName;
    }

    public static Category convert(int type) {
        for (Category fromType : values()) {
            if (fromType.ordinal() == type) {
                return fromType;
            }
        }
        return UNKNOWN;
    }

    public static Category convert(String catName) {
        for (Category fromType : values()) {
            if (fromType.catName.equals(catName)) {
                return fromType;
            }
        }
        return UNKNOWN;
    }
}
