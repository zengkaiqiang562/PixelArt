package com.project_ci01.app.base.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.google.gson.Gson;
import com.project_ci01.app.pixel.PixelList;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

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

    public static void writeObjectByZipJson(Object obj, String filePath) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        byte[] gzCompress = GzipUtils.zip(json.getBytes(StandardCharsets.UTF_8));
        byte[] encodeBase64 = Base64.encodeBase64(gzCompress);
        FileIOUtils.writeFileFromBytesByStream(filePath, encodeBase64);
    }

    public static <T> T readObjectByZipJson(Class<T> clazz, String filePath) {
        LogUtils.e(TAG, "--> readObjectByZipJson()  filePath=" + filePath);
        byte[] bytes = FileIOUtils.readFile2BytesByStream(filePath);
        byte[] decodeBase64 = Base64.decodeBase64(bytes);
        byte[] gzDecompress = GzipUtils.unzip(decodeBase64);
        String json = new String(gzDecompress, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
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
