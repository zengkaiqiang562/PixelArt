package com.project_ci01.app.base.net;

import androidx.annotation.NonNull;

import com.project_ci01.app.base.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class MyInterceptor implements Interceptor {
    private static final String TAG = "MyInterceptor";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request request = chain.request();

        boolean safe = Boolean.parseBoolean(request.header("security"));
        LogUtils.e(TAG, "--> intercept()  security=" + safe);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        BufferedSink buffer = Okio.buffer(Okio.sink(bao));

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            requestBody.writeTo(buffer);
            buffer.flush();
        }

        String originRequest = bao.toString();

        LogUtils.e(TAG, "HTTP Request url=" + request.url() + "  body=" + originRequest);

        byte[] requestBytes;
//        if (security) {
//            requestBytes = SafeNative.nEncrypt(originRequest);
//        } else {
            requestBytes = originRequest.getBytes(StandardCharsets.UTF_8);
//        }

        RequestBody newBody = FormBody.create(MediaType.parse("application/json"), requestBytes);

        Request newRequest = request.newBuilder()
                .addHeader("Content-Type", "application/json")
                .post(newBody)
                .build();

        Response response = chain.proceed(newRequest);

        ResponseBody responseBody = response.body();
        Response.Builder responseBuilder = response.newBuilder().request(newRequest);
        if (responseBody != null) {
            byte[] responseBytes = responseBody.bytes();

//            Slog.e(TAG, "--> SecureInterceptor  responseBytes.size=" + responseBytes.length);

            byte[] dstResponseBytes;
//            if (security) {
//                dstResponseBytes = SafeNative.nDecrypt(responseBytes);
//            } else {
                dstResponseBytes = responseBytes;
//            }

            if (dstResponseBytes == null) {
                LogUtils.e(TAG, "HTTP Response url=" + newRequest.url() + "  dstResponseBytes == null !!!");
                return response;
            }

//            Slog.e(TAG, "--> SecureInterceptor  dstResponseBytes.len=" + dstResponseBytes.length); // java: dstResponseBytes.len=10954

            String strResponseBody = new String(dstResponseBytes, StandardCharsets.UTF_8);
            LogUtils.e(TAG, "HTTP Response url=" + newRequest.url() + "  body=" + strResponseBody);

            Source newSource = Okio.source(new ByteArrayInputStream(dstResponseBytes));

            String contentType = response.header("Content-Type");
            long contentLength = dstResponseBytes.length;
            responseBuilder.body(new RealResponseBody(contentType, contentLength, Okio.buffer(newSource)));
        }

        return responseBuilder.build();
    }
}