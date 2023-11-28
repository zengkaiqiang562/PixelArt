package com.project_ci01.app.base.view;

import com.project_ci01.app.base.manage.ContextManager;

import java.lang.ref.WeakReference;

public abstract class ActivityPresenter<T extends BaseActivity, Data> extends RefreshPresenter<Data> {

    protected WeakReference<T> wRefContext;

    protected ActivityPresenter(T activity) {
        wRefContext = new WeakReference<>(activity);
    }

    /**
     * @return 返回有效的Context，maybe null
     */
    protected T getContext() {
        if (wRefContext == null) {
            return null;
        }
        T activity = wRefContext.get();
        return ContextManager.isSurvival(activity) ? activity : null;
    }

    /**
     * @return true 在生命周期内，false 不在生命周期内
     */
    protected boolean checkContext() {
        if (wRefContext == null) {
            return false;
        }
        T activity = wRefContext.get();
        return ContextManager.isSurvival(activity);
    }


    public void release() {
        if (wRefContext != null) {
            wRefContext.clear();
            wRefContext = null;
        }
    }
}
