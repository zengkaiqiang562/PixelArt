package com.project_m1142.app.base.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static void writeObject(Context context, Object obj, String filePath) {

        File cacheDir = context.getCacheDir();

        String absFilePath = cacheDir + File.separator + filePath;

        LogUtils.e(TAG, "--> writeObject()  absFilePath=" + absFilePath);

        File file = new File(absFilePath);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(absFilePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            Log.e(TAG, "---> writeObject() failed  absFilePath : " + absFilePath  + " e : " + e);
        }
    }

    public static Object readObject(Context context, String filePath) {

        File cacheDir = context.getCacheDir();

        String absFilePath = cacheDir + File.separator + filePath;

        LogUtils.e(TAG, "--> readObject()  absFilePath=" + absFilePath);

        File file = new File(absFilePath);
        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(absFilePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj =  ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            Log.e(TAG, "---> readObject() failed  absFilePath : " + absFilePath  + " e : " + e);
            return null;
        }
    }
}
