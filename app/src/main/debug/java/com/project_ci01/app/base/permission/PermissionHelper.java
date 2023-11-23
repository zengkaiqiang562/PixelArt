package com.project_ci01.app.base.permission;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.base.utils.LogUtils;

import java.util.List;

public class PermissionHelper {

    private static final String TAG = "PermissionHelper";

    public static void checkOrApplyStoragePermission(Activity activity, @NonNull PermissionCallback callback) {
        if (checkStoragePermission(activity)) {
            callback.onCompleted(true);
            return;
        }

        applyStoragePermission(activity, granted -> {
            callback.onCompleted(granted);
        });
    }

    public static  boolean checkStoragePermission(Context context) {
        boolean rwGranted = XXPermissions.isGranted(context, Permission.Group.STORAGE);
        LogUtils.e(TAG, "--> checkStoragePermission()  rwGranted=" + rwGranted);
        return rwGranted;
    }

    public static  boolean checkStorage13Permission(Context context) {
        boolean rwGranted = XXPermissions.isGranted(context, Permission.READ_MEDIA_AUDIO, Permission.READ_MEDIA_IMAGES, Permission.READ_MEDIA_VIDEO);
        LogUtils.e(TAG, "--> checkStorage13Permission()  rwGranted=" + rwGranted);
        return rwGranted;
    }

    public static boolean checkNotificationPermission(Context context) {
        boolean notiGranted = XXPermissions.isGranted(context, Permission.NOTIFICATION_SERVICE);
        LogUtils.e(TAG, "--> checkNotificationPermission()  notiGranted=" + notiGranted);
        return notiGranted;
    }

    public static boolean checkCameraPermission(Context context) {
        boolean granted = XXPermissions.isGranted(context, Permission.CAMERA);
        LogUtils.e(TAG, "--> checkCameraPermission()  granted=" + granted);
        return granted;
    }

    public static boolean checkLocationPermission(Context context) {
        boolean granted = XXPermissions.isGranted(context, Permission.NEARBY_WIFI_DEVICES);
        LogUtils.e(TAG, "--> checkLocationPermission()  granted=" + granted);
        return granted;
    }

    public static void applyStoragePermission(Activity activity, @NonNull PermissionCallback callback) {

        XXPermissions.with(activity)
                // 申请多个权限
                .permission(Permission.Group.STORAGE) // Manifest.permission.READ_EXTERNAL_STORAGE & Manifest.permission.WRITE_EXTERNAL_STORAGE
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        LogUtils.e(TAG, "--> applyStoragePermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        LogUtils.e(TAG, "--> applyStoragePermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }

    public static void applyStorage13Permission(Activity activity, @NonNull PermissionCallback callback) {

        XXPermissions.with(activity)
                // 申请多个权限
                .permission(Permission.READ_MEDIA_AUDIO)
                .permission(Permission.READ_MEDIA_VIDEO)
                .permission(Permission.READ_MEDIA_IMAGES)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        LogUtils.e(TAG, "--> applyStorage13Permission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        LogUtils.e(TAG, "--> applyStorage13Permission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }

    public static void applyCameraPermission(Activity activity, @NonNull PermissionCallback callback) {

        XXPermissions.with(activity)
                // 申请多个权限
                .permission(Permission.CAMERA) // Manifest.permission.READ_EXTERNAL_STORAGE & Manifest.permission.WRITE_EXTERNAL_STORAGE
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        LogUtils.e(TAG, "--> applyCameraPermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        LogUtils.e(TAG, "--> applyCameraPermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }

    public static void applyNotificationPermission(Activity activity, @NonNull PermissionCallback callback) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).setSkipStartLoading(true);
        }
        XXPermissions.with(activity)
                .permission(Permission.NOTIFICATION_SERVICE)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        LogUtils.e(TAG, "--> applyNotificationPermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        LogUtils.e(TAG, "--> applyNotificationPermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }

    public static void applyLocationPermission(Activity activity, boolean doNever, @NonNull PermissionCallback callback) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).setSkipStartLoading(true);
        }
        XXPermissions.with(activity)
                .permission(Permission.NEARBY_WIFI_DEVICES)
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        LogUtils.e(TAG, "--> applyLocationPermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        LogUtils.e(TAG, "--> applyLocationPermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never && doNever) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }
}
