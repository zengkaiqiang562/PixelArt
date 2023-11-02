package com.project_m1142.app.base.view.holder;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.view.BaseActivity;

import java.lang.ref.WeakReference;

public abstract class BaseViewHolder implements ActivityLifecycleObserver {
    protected String TAG = "BaseViewHolder";

    protected BaseActivity baseActivity;
    protected ViewGroup parentView;
    protected ViewGroup contentView;

    protected boolean turning = false;

    protected UIHandler<?> uiHandler;

    protected BaseViewHolder(Context context, ViewGroup parentView, LifecycleObservable<ActivityLifecycleObserver> lifecycleObservable) {
        this(context, parentView, lifecycleObservable, true);
    }

    protected BaseViewHolder(Context context, ViewGroup parentView, LifecycleObservable<ActivityLifecycleObserver> lifecycleObservable, boolean attach) {
        if (!(context instanceof BaseActivity)) {
            throw new RuntimeException("ViewHolder must created in BaseActivity context!!!");
        }
        baseActivity = (BaseActivity)context;
        this.parentView = parentView;
        contentView = (ViewGroup) LayoutInflater.from(context).inflate(getLayoutId(), this.parentView, false);
        lifecycleObservable.addLifecycleObserver(this);
        init();

        if (attach) {
            addToParent();
        }
    }

    protected abstract String tag();

    protected abstract int getLayoutId();

    public abstract void init();

    /**
     * 由子类重写，用于处理activity
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    /**
     * @return 返回 true 表示可以跳转UI，避免快速重复点击
     */
    public boolean checkTurnFlag() {
        return !turning && (turning = true);
    }

    protected void handleMessage(@NonNull Message msg) {}

    public final boolean isResumed() {
        return ContextManager.isSurvival(baseActivity) && baseActivity.isResumed;
    }

    protected final <T extends View> T findViewById(int res) {
        return contentView.findViewById(res);
    }

    public final View getContentView() {
        return contentView;
    }

    public final void addToParent() {
        if (parentView != null && contentView != null) {
            parentView.addView(contentView);
        }
    }

    public final void removeAndAddParent() {
        if (parentView != null && contentView != null) {
            parentView.removeAllViews();
            parentView.addView(contentView);
        }
    }

    public void removeFromParent() {
        ViewParent parent = contentView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(contentView);
        }
    }

    /**
     * 由子类重写，用于销毁时释放资源
     */
    public void release() {
        if (hasAttachedParent()) {
            removeFromParent();
        }
    }

    public final boolean hasAttachedParent() {
        return contentView != null && parentView != null && contentView.getParent() != null;
    }

//    public final boolean isSurvival() {
//        return ContextManager.isSurvival(baseActivity) && hasAttachedParent();
//    }

    protected void setLayoutTransition() {
        LayoutTransition transition = new LayoutTransition();
    }

    /*--------------------------------- LifecycleObserver Method  start -------------------------------------------*/

    @Override
    public void onCreate() {

    }

    @Override
    public void onStart() {
    }

    @Override
    public void onRestart() {
    }

    @Override
    public void onResume() {
        turning = false;
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    /*--------------------------------- LifecycleObserver Method  end -------------------------------------------*/

    protected static class UIHandler<T extends BaseViewHolder> extends Handler {

        protected WeakReference<T> wRefHolder;

        public UIHandler(T activity) {
            super(Looper.getMainLooper());
            wRefHolder = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            T holder = wRefHolder.get();
            if (!ContextManager.isSurvival(holder.baseActivity)) {
                return;
            }
            holder.handleMessage(msg);
        }
    }
}
