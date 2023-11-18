package com.project_ci01.app.base.net;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.bean.gson.LocationBean;
import com.project_ci01.app.base.config.AppConfig;
import com.project_ci01.app.base.manage.LocationManager;
import com.project_ci01.app.base.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public enum NetExecutor {

    INSTANCE;

    private static final String TAG = "NetExecutor";

    private final Retrofit retrofit;
    private final OkHttpClient okHttpClient;
    private final Handler handler = new Handler(Looper.getMainLooper());

    NetExecutor() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                //禁止重定向操作，避免出现 too many follow-up requests: 21 的异常信息
                .followRedirects(false)
                .followSslRedirects(false);

        builder.addInterceptor(new MyInterceptor());

        okHttpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.URL_OFFICIAL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void resetConnPools() {
        try {
            okHttpClient.connectionPool().evictAll();
            LogUtils.e(TAG, "--> resetConnPools()");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "--> resetConnPools() Exception=" + e);
        }
    }

    public void requestLocalInfo(@NonNull LocationManager.RequestCallback callback) {
        NetInterface netInterface = retrofit.create(NetInterface.class);
        request1stLocal(netInterface, callback);
    }

    private void request1stLocal(NetInterface serivce, LocationManager.RequestCallback callback) {
        Call<LocationBean> call = serivce.request1stLocal();
        LogUtils.e(TAG, "--> request1stLocal()");
        call.enqueue(new Callback<LocationBean>() {
            @Override
            public void onResponse(@NonNull Call<LocationBean> call, @NonNull Response<LocationBean> response) {
                LocationBean locationBean = response.body();
                if (locationBean != null) {
                    LogUtils.e(TAG, "--> request1stLocal() onSuccess");
                    callback.onSuccess(locationBean);
                } else {
                    request2ndLocal(serivce, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationBean> call, @NonNull Throwable t) {
                request2ndLocal(serivce, callback);
            }
        });
    }

    private void request2ndLocal(NetInterface serivce, LocationManager.RequestCallback callback) {
        Call<LocationBean> call = serivce.request2ndLocal();
        LogUtils.e(TAG, "--> request2ndLocal()");
        call.enqueue(new Callback<LocationBean>() {
            @Override
            public void onResponse(@NonNull Call<LocationBean> call, @NonNull Response<LocationBean> response) {
                LocationBean locationBean = response.body();
                if (locationBean != null) {
                    LogUtils.e(TAG, "--> request2ndLocal() onSuccess");
                    callback.onSuccess(locationBean);
                } else {
                    request3rdLocal(serivce, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationBean> call, @NonNull Throwable t) {
                request3rdLocal(serivce, callback);
            }
        });
    }

    private void request3rdLocal(NetInterface serivce, LocationManager.RequestCallback callback) {
        Call<LocationBean> call = serivce.request3rdLocal();
        LogUtils.e(TAG, "--> request3rdLocal()");
        call.enqueue(new Callback<LocationBean>() {
            @Override
            public void onResponse(@NonNull Call<LocationBean> call, @NonNull Response<LocationBean> response) {
                LocationBean locationBean = response.body();
                if (locationBean != null) {
                    LogUtils.e(TAG, "--> request3rdLocal() onSuccess");
                    callback.onSuccess(locationBean);
                } else {
                    request4thLocal(serivce, callback);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationBean> call, @NonNull Throwable t) {
                request4thLocal(serivce, callback);
            }
        });
    }

    private void request4thLocal(NetInterface serivce, LocationManager.RequestCallback callback) {
        Call<LocationBean> call = serivce.request4thLocal();
        LogUtils.e(TAG, "--> request4thLocal()");
        call.enqueue(new Callback<LocationBean>() {
            @Override
            public void onResponse(@NonNull Call<LocationBean> call, @NonNull Response<LocationBean> response) {
                LocationBean locationBean = response.body();
                LogUtils.e(TAG, "--> request4thLocal() onSuccess");
                if (locationBean != null) {
                    callback.onSuccess(locationBean);
                } else {
                    callback.onFailure(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LocationBean> call, @NonNull Throwable t) {
                LogUtils.e(TAG, "--> request4thLocal() onFailure: " + t);
                callback.onFailure(t);
            }
        });
    }
}
