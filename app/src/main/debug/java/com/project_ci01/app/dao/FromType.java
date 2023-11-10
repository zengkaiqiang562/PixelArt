package com.project_ci01.app.dao;

public enum FromType {
    UNKNOWN("unknown"),
    LOCAL("local"),
    NETWORK("network"),
    CREATE("create");

    public final String typeName;

    FromType(String typeName) {
        this.typeName = typeName;
    }

    public static FromType convert(int type) {
        for (FromType fromType : values()) {
            if (fromType.ordinal() == type) {
                return fromType;
            }
        }
        return UNKNOWN;
    }

    public static FromType convert(String typeName) {
        for (FromType fromType : values()) {
            if (fromType.typeName.equals(typeName)) {
                return fromType;
            }
        }
        return UNKNOWN;
    }
}
