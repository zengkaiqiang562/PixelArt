package com.project_m1142.app.ui.dialog;

import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.project_m1142.app.R;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class LimitDialog extends BaseDialog {

    @Override
    protected String tag() {
        return "LimitDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_limit;
    }

    @Override
    protected void init() {
        findViewById(R.id.limit_leave_button).setOnClickListener(v -> {
            dismiss();
        });
    }

    @Override
    protected boolean allowOutCancel() {
        return false;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ContextManager.INSTANCE.finishAll();
    }
}
