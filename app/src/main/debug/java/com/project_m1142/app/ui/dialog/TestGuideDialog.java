package com.project_m1142.app.ui.dialog;

import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class TestGuideDialog extends BaseDialog {

    @Override
    protected String tag() {
        return "TestGuideDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_test_guide;
    }

    @Override
    protected void init() {
        findViewById(R.id.connected_test_button).setOnClickListener(v -> {
            confirm = true;
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });

        findViewById(R.id.connected_later_button).setOnClickListener(v -> {
            cancel = true;
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });
    }
}
