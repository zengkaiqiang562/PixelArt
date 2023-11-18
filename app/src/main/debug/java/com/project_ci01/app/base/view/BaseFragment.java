package com.project_ci01.app.base.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.gyf.immersionbar.ImmersionBar;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.utils.LogUtils;

import java.lang.ref.WeakReference;

public abstract class BaseFragment extends Fragment {
    protected String TAG = "BaseFragment";

    protected boolean turning = false;

    protected FragmentActivity activity;
    protected BaseActivity baseActivity;
    protected UIHandler<?> uiHandler;

    protected boolean isViewCreated = false;

    /**
     * @return 返回 true 表示可以跳转UI，避免快速重复点击
     */
    protected boolean canTurn() {
        return !turning && (turning = true);
    }

    protected abstract String tag();
    protected abstract View getRoot(LayoutInflater inflater, ViewGroup container);
    protected abstract View stubBar();
    protected abstract void initView(View view, Bundle savedInstanceState);
    protected void handleMessage(@NonNull Message msg) {}

    /** 重写此方法可在 Tab 切换时重新加载数据 */
    public void reload() {}

    public String fmTAG() {
        return tag() + "#" + hashCode();
    }

    public boolean handleBackKey() {
        return false;
    }

    public boolean isViewCreated() {
        return isViewCreated;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        TAG = tag();
        LogUtils.e(TAG, "--> onAttach() context=" + context);
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        }
        if (activity instanceof BaseActivity) {
            baseActivity = (BaseActivity) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e(TAG, "--> onCreate()");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e(TAG, "--> onCreateView()");
        if (activity == null) {
            activity = getActivity();
            if (activity instanceof BaseActivity) {
                baseActivity = (BaseActivity) activity;
            }
        }

        uiHandler = new UIHandler<>(this);
        return getRoot(LayoutInflater.from(activity), container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.e(TAG, "--> onViewCreated()");
        initBar();
        fillStatusbar(stubBar());
        initView(view, savedInstanceState);
        isViewCreated = true;
    }

    protected void initBar() {
        ImmersionBar.with(this)/*.transparentNavigationBar()*/.transparentStatusBar().statusBarDarkFont(true).init();
    }

    private void fillStatusbar(@Nullable View stubBar) {
        if (stubBar != null) {
            int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
            int dp44 = ConvertUtils.dp2px(44);
            if (statusBarHeight < dp44) {
                statusBarHeight = dp44;
            }
            ViewGroup.LayoutParams layoutParams = stubBar.getLayoutParams();
            layoutParams.height = statusBarHeight;
            stubBar.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.e(TAG, "--> onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e(TAG, "--> onResume()");
        turning = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e(TAG, "--> onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.e(TAG, "--> onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.e(TAG, "--> onDestroyView()");
        isViewCreated = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "--> onDestroy()");
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
        }
        activity = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.e(TAG, "--> onDetach()");
    }

    protected static class UIHandler<T extends BaseFragment> extends Handler {

        protected WeakReference<T> wRefFragment;

        public UIHandler(T fragment) {
            super(Looper.getMainLooper());
            wRefFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            T fragment = wRefFragment.get();
            if (fragment == null || !ContextManager.isSurvival(fragment.activity)) {
                return;
            }
            fragment.handleMessage(msg);
        }
    }
}
