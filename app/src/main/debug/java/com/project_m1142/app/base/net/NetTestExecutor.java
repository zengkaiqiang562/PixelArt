package com.project_m1142.app.base.net;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public enum NetTestExecutor {

    INSTANCE;

    private static final String TAG = "NetTestExecutor";

    private final OkHttpClient okHttpClient;
//    private final Handler handler = new Handler(Looper.getMainLooper());

    NetTestExecutor() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS);

//        builder.addInterceptor(new MyInterceptor());

        okHttpClient = builder.build();

//        retrofit = new Retrofit.Builder()
//                .baseUrl(AppConfig.URL_OFFICIAL)
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
    }

    public void test() {
        Request request = new Request.Builder()
                .url("https://mirrors.edge.kernel.org/pub/linux/kernel/v6.x/linux-6.1.15.tar.gz") // test url
                .cacheControl(new CacheControl.Builder().noStore().build()) // 不保存数据
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 下载失败
                LogUtils.e(TAG, "--> onFailure()  e=" + e);
//                e.printStackTrace();
//                listener.onDownloadFailed();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
//                FileOutputStream fos = null;
                // 储存下载文件的目录
                //String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
////                    File file = new File(savePath, getNameFromUrl(url));
//                    File file = new File(savePath);
//
//                    if (file.exists()){
//                        file.delete();
//                    }
//                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
//                        fos.write(buf, 0, len);
                        sum += len;
//                        LogUtils.e(TAG, "--> onResponse()  sum=" + sum + "  total=" + total);
//                        int progress = (int) (sum * 1.0f / total * 100);
//                        // 下载中
//                        listener.onDownloading(progress);
                    }
//                    fos.flush();
//                    // 下载完成
//                    listener.onDownloadSuccess(filePath);
                } catch (Exception e) {
                    LogUtils.e(TAG, "--> onResponse()  e=" + e);
//                    listener.onDownloadFailed();
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        LogUtils.e(TAG, "--> onResponse()  e=" + e);
                    }
//                    try {
//                        if (fos != null)
//                            fos.close();
//                    } catch (IOException e) {
//                    }
                }
            }
        });
    }

    public void cancel() {
        okHttpClient.dispatcher().cancelAll();
    }

}
