package com.project_ci01.app.base.utils;

import android.util.Log;

import com.project_ci01.app.base.config.AppConfig;

public class LogUtils {
    private static final boolean DEBUG = AppConfig.ENABLE_LOG;

    public static void d(String tag, String message) {
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void d(String message) {
        if (DEBUG) {
            Log.d("CI01", message);
        }
    }

    public static void e(String message) {
        if (DEBUG) {
            Log.e("CI01", message);
        }
    }
}
