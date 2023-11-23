package com.project_ci01.app.base.manage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;


import androidx.annotation.NonNull;

import com.project_ci01.app.base.view.BaseActivity;
import com.project_ci01.app.base.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ContextManager {

    INSTANCE;

    private final List<WeakReference<Activity>> activityHistoryStack = new ArrayList<>();

    // 根据网址跳转第三方浏览器
    public static void turn2BrowserWithHost(@NonNull BaseActivity activity, @NonNull String host) {
        Intent intent = new Intent();
        // 设置意图动作为打开浏览器
        intent.setAction(Intent.ACTION_VIEW);
        // 声明一个Uri
        Uri uri = Uri.parse(host);
        intent.setData(uri);
        // 不要用 resolveActivity ，可能会跳转不过去
        if (/*intent.resolveActivity(activity.getPackageManager()) != null && */activity.canTurn()) {
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 跳转到电子邮箱，给 email 发邮箱
     */
    public static void turn2Email(@NonNull BaseActivity activity, @NonNull String email) {
        Uri uri = Uri.parse("mailto:" + email);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        // 设置对方邮件地址
        intent.putExtra(Intent.EXTRA_EMAIL, email);
//        // 设置标题内容
//        intent.putExtra(Intent.EXTRA_SUBJECT, title);
//        // 设置邮件文本内容
//        intent.putExtra(Intent.EXTRA_TEXT, content);
        // 不要用 resolveActivity ，可能会跳转不过去
        if (/*intent.resolveActivity(activity.getPackageManager()) != null && */activity.canTurn()) {
            try {
                activity.startActivity(Intent.createChooser(intent, "Select Email"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 跳转到应用商店中的 pkgName 对应的 app 详情页
     */
    public static void turn2Playstore(@NonNull BaseActivity activity, @NonNull String pkgName) {
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 不要用 resolveActivity ，可能会跳转不过去
        if (/*intent.resolveActivity(activity.getPackageManager()) != null && */activity.canTurn()) {
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 分享网页
     */
    public static void turn2ShareApp(@NonNull BaseActivity activity, @NonNull String pkgName) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
//        if (stringCheck(className) && stringCheck(packageName)) {
//            ComponentName componentName = new ComponentName(packageName, className);
//            intent.setComponent(componentName);
//        } else if (stringCheck(packageName)) {
//            intent.setPackage(packageName);
//        }


//            intent.putExtra(Intent.EXTRA_TEXT, content);
        Uri uri = Uri.parse("market://details?id=" + pkgName);
        intent.putExtra(Intent.EXTRA_TEXT, uri.toString());



//        if (null != title && !TextUtils.isEmpty(title)) {
//            intent.putExtra(Intent.EXTRA_TITLE, title);
//        }
//        if (null != subject && !TextUtils.isEmpty(subject)) {
//            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        }
//        intent.putExtra(Intent.EXTRA_TITLE, title);
        // 不要用 resolveActivity ，可能会跳转不过去
        if (/*intent.resolveActivity(activity.getPackageManager()) != null && */activity.canTurn()) {
            try {
                Intent chooserIntent = Intent.createChooser(intent, "Share to: ");
                activity.startActivity(chooserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return context 无效返回 false，有效返回 true
     */
    public static boolean isSurvival(Context context) {
        if (context == null) {
            return false;
        }
        return !(context instanceof Activity) || isActivitySurvival((Activity) context);
    }

    /**
     * @return activity 无效返回 false，有效返回 true
     */
    private static boolean isActivitySurvival(Activity activity) {
        if (activity == null) {
            return false;
        }

        return !activity.isDestroyed() && !activity.isFinishing();
    }

    public synchronized void addActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (contains(activity)) {
            removeActivity(activity);

        }
        activityHistoryStack.add(new WeakReference<>(activity));
    }

    public synchronized void removeActivity(Activity activity) {
        if (activityHistoryStack.size() <= 0) {
            return;
        }

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity tmpActivity = wRefActivity.get();
            if (tmpActivity == null || tmpActivity == activity) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
            }
        }
    }

    public synchronized Activity peekActivity(Class<? extends Activity> clazz) {
        if (activityHistoryStack.size() <= 0) {
            return null;
        }

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }
            if (activity.getClass() == clazz) {
                return activity;
            }
        }
        return null;
    }

    public synchronized Activity topActivity() {
        if (activityHistoryStack.size() <= 0) {
            return null;
        }
        return activityHistoryStack.get(activityHistoryStack.size() - 1).get();
    }

//    public synchronized Class<?> topActivityClass() {
//        return topActivityClass();
//    }

    public synchronized Class<?> topActivityClass(Class<? extends Activity>... excluClss) { // 排除 excluCls 的条件下，前台 Activity 是哪个
        if (activityHistoryStack.size() <= 0) {
            return null;
        }

        List<Class<? extends Activity>> excluClsList = Arrays.asList(excluClss);

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                continue;
            }
//            if (activity.getClass() == excluCls) {
            if (excluClsList.contains(activity.getClass())) {
                continue;
            }
            return activity.getClass();
        }
        return null;
    }

    public synchronized void finishActivity(Class<? extends Activity> clazz) {
        finishActivity(clazz, false);
    }

    /**
     * 结束指定类名的Activity
     */
    public synchronized void finishActivity(Class<? extends Activity> clazz, boolean _super) {
        if (activityHistoryStack.size() <= 0) {
            return;
        }

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }
            Class<? extends Activity> cls = activity.getClass();
            if (cls == clazz) {
                activity.finish();
//                activity.overridePendingTransition(R.anim.right_in,
//                        R.anim.right_out);

                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }

            if (_super) {
                Class<?> clsSuper = cls.getSuperclass();
                while (clsSuper != null) {
                    if (clsSuper == clazz) {
                        activity.finish();
//                        activity.overridePendingTransition(R.anim.right_in,
//                                R.anim.right_out);

                        wRefActivity.clear();
                        activityHistoryStack.remove(i);
                        break;
                    }
                    clsSuper = clsSuper.getSuperclass();
                }
            }
        }
    }

    /**
     * 除参数 Class 类型 匹配的 Activity 实例外，{@link #activityHistoryStack} 集中的其他 Activity 实例都销毁掉
     * 如果{@link #activityHistoryStack} 集中 存在多个 与 参数 Class 类型 匹配的 Activity 实例，则仅保留最先添加进来的
     *
     */
    @SafeVarargs
    public final synchronized void finishAllExclu(@NonNull Class<? extends Activity>... excluClss) {
        if (activityHistoryStack.size() <= 0) {
            return;
        }

        List<Class<? extends Activity>> excluClsList = Arrays.asList(excluClss);

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }
//            if (activity.getClass() != excluCls) {
            if (!excluClsList.contains(activity.getClass())) {
                activity.finish();

                wRefActivity.clear();
                activityHistoryStack.remove(i);
            }
        }

        if (activityHistoryStack.size() <= 1) {
            return;
        }

        //再来一次for循环，仅保留最先的一个Activity实例
        for (int i = activityHistoryStack.size() - 1; i >= 1; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }

            activity.finish();
            wRefActivity.clear();
            activityHistoryStack.remove(i);
        }
    }

    public synchronized void finishAll() {
        if (activityHistoryStack.size() <= 0) {
            return;
        }

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }
            activity.finish();
            wRefActivity.clear();
            activityHistoryStack.remove(i);
        }
        activityHistoryStack.clear();
    }

    public synchronized boolean contains(Activity activity) {
        if (activity == null || activityHistoryStack.size() <= 0) {
            return false;
        }

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity tmpActivity = wRefActivity.get();
            if (tmpActivity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }
            if (tmpActivity == activity) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean contains(Class<? extends Activity> clazz) {
        if (activityHistoryStack.size() <= 0) {
            return false;
        }

        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                activityHistoryStack.remove(i);
                continue;
            }
            if (activity.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    public synchronized void printActivityHistoryStack() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int i = activityHistoryStack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = activityHistoryStack.get(i);
            Activity activity = wRefActivity.get();
            sb.append(activity.getClass().getSimpleName());
            sb.append(i != 0 ? ", " : " }");
        }

        LogUtils.e("ActivityHistoryStack", sb.toString());
    }
}
