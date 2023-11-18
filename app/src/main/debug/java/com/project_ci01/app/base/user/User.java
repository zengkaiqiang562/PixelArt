package com.project_ci01.app.base.user;

import com.project_ci01.app.base.manage.ConfigManager;

public enum User {
    OTHER(1), // 买量用户
    ORGANIC(2); // 自然用户

    private final int type;

    User(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static User convert(int type) {
        for (User user : values()) {
            if (user.type == type) {
                return user;
            }
        }
        return ConfigManager.isOrganicUser() ? ORGANIC : OTHER;
    }
}
