package com.project_ci01.app.base.view;

import com.project_ci01.app.base.manage.ContextManager;

import java.lang.ref.WeakReference;

public abstract class FragmentPresenter<T extends BaseFragment, Data> extends RefreshPresenter<Data> {

    protected WeakReference<T> wRefFragment;

    protected FragmentPresenter(T fragment) {
        wRefFragment = new WeakReference<>(fragment);
    }

    /**
     * @return 返回有效的Context，maybe null
     */
    protected BaseActivity getContext() {
        if (wRefFragment == null) {
            return null;
        }
        T fragment = wRefFragment.get();
        if (fragment == null) {
            return null;
        }
        return ContextManager.isSurvival(fragment.baseActivity) ? fragment.baseActivity : null;
    }

    protected T getFragment() {
        if (wRefFragment == null) {
            return null;
        }
        T fragment = wRefFragment.get();
        if (fragment == null) {
            return null;
        }
        return ContextManager.isSurvival(fragment.baseActivity) ? fragment : null;
    }

    /**
     * @return true 在生命周期内，false 不在生命周期内
     */
    protected boolean checkContext() {
        if (wRefFragment == null) {
            return false;
        }
        T fragment = wRefFragment.get();
        if (fragment == null) {
            return false;
        }
        return ContextManager.isSurvival(fragment.baseActivity);
    }


    public void release() {
        if (wRefFragment != null) {
            wRefFragment.clear();
            wRefFragment = null;
        }
    }
}
