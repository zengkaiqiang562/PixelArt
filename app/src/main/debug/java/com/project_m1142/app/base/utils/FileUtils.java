package com.project_m1142.app.base.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static boolean saveBitmap(Bitmap bitmap, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "---> saveBitmap() failed  filePath=" + filePath  + " e=" + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param delIfExist  true 文件存在时删除，并重新保存； false 文件存在时不处理
     */
    public static void writeObject(Object obj, String filePath, boolean delIfExist) {

        LogUtils.e(TAG, "--> writeObject()  filePath=" + filePath + "   delIfExist=" + delIfExist);

        File file = new File(filePath);
        if (file.exists()) {
            if (!delIfExist) {
                return;
            }
            file.delete();
        }

        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
        } catch (IOException e) {
            Log.e(TAG, "---> writeObject() failed  filePath=" + filePath  + " e=" + e);
            e.printStackTrace();
        }
    }

    public static void writeObject(Object obj, String filePath) {

        writeObject(obj, filePath, true); // 存在时删除
    }

    public static Object readObject(String filePath) {

        LogUtils.e(TAG, "--> readObject()  filePath=" + filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj =  ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            Log.e(TAG, "---> readObject() failed  filePath=" + filePath  + " e=" + e);
            e.printStackTrace();
            return null;
        }
    }
}
