package com.project_ci01.app.base.utils;

import android.util.Log;

import com.google.gson.Gson;

public class JsonUtils {
    private static final String TAG = "JsonTools";

    /**
     * jsonData Convert TO instance of type
     */
    public static <T> T fromJson(String jsonData, Class<T> type) {
        try {
            return new Gson().fromJson(jsonData, type);
        } catch (Exception err) {
            Log.e(TAG, "--> fromJson(String) Exception: " + err.getMessage());
            return null;
        }
    }

//    /**
//     * inputStream Convert TO instance of type
//     */
//    public static <T> T fromJson(InputStream inputStream, Class<T> type) {
//        try {
//            InputStreamReader reader = new InputStreamReader(inputStream);
//            return new Gson().fromJson(reader, type);
//        } catch (Exception err) {
//            Log.e(TAG, "--> fromJson(InputStream) Exception: " + err.getMessage());
//            return null;
//        }
//    }


    /**
     * bean Convert TO json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }
}
