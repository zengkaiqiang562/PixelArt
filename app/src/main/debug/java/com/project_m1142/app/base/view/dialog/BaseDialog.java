package com.project_m1142.app.base.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;

import com.gyf.immersionbar.ImmersionBar;
import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.BaseActivity;

import java.lang.ref.WeakReference;

public abstract class BaseDialog extends DialogFragment {
    protected String TAG = "BaseDialog";
    protected Context context;
    protected BaseActivity activity;
    protected ViewGroup rootView;
    protected ViewGroup parentView;
    protected ViewGroup contentView;

    protected DialogListener<?> listener;

    protected UIHandler<?> uiHandler;

    protected boolean confirm = false;
    protected boolean cancel = false;

    protected void init() {}
    protected void setOnKeyListener(Dialog dialog) {}
    protected abstract String tag();
    protected void handleMessage(@NonNull Message msg) {}

    protected @StyleRes int animStyle() {
        return R.style.DialogAnimationStyle;
    }

    /**
     * Dialog的内容布局
     */
    protected abstract @LayoutRes int getLayoutId();

    /**
     * Dialog主题样式
     */
    protected @StyleRes int getDialogStyle() {
        return R.style.DialogBackgroundStyle;
    }

    /**
     * 点击 Dialog 以外区域能否隐藏 Dialog
     */
    protected boolean allowOutCancel() {
        return true;
    }

    public void reset() {
        cancel = false;
        confirm = false;
    }

    public void setListener(DialogListener<? extends BaseDialog> listener) {
        this.listener = listener;
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

    public String fmTAG() {
        return tag() + "#" + hashCode();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }

        Dialog dialog = new Dialog(context, getDialogStyle());

        rootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_root, null);
        View.inflate(context, getLayoutId(), rootView);
        parentView = (ViewGroup) rootView.getChildAt(0);
        contentView = (ViewGroup) parentView.getChildAt(0);

        rootView.setOnClickListener(v -> {
            if (allowOutCancel()) {
                dismiss();
            }
        });

        parentView.setOnClickListener(v -> {
            if (allowOutCancel()) {
                dismiss();
            }
        });

        contentView.setOnClickListener(v -> {
            /* do nothing*/
        });

        dialog.setContentView(rootView);
        dialog.setCancelable(allowOutCancel());
        dialog.setCanceledOnTouchOutside(allowOutCancel());
        setOnKeyListener(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(animStyle());
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
//        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        uiHandler = new UIHandler<>(this);
        init();

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImmersionBar.with(this).transparentNavigationBar().transparentStatusBar().statusBarDarkFont(false).init();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            listener.onResume(this);
        }
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        LogUtils.e(TAG, "--> onDismiss()");
        if (listener != null && !confirm && !cancel) {
            listener.onDismiss();
        }
    }

    protected static class UIHandler<T extends BaseDialog> extends Handler {

        protected WeakReference<T> wRefDialog;

        public UIHandler(T dialog) {
            super(Looper.getMainLooper());
            wRefDialog = new WeakReference<>(dialog);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            T dialog = wRefDialog.get();
            if (dialog == null || !ContextManager.isSurvival(dialog.activity)) {
                return;
            }
            dialog.handleMessage(msg);
        }
    }
}
