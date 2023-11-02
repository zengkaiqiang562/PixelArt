package com.project_m1142.app.ui.dialog;

import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class DisconnectDialog extends BaseDialog {

    @Override
    protected String tag() {
        return "DisconnectDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_disconnect;
    }

    @Override
    protected void init() {
        findViewById(R.id.disconnect_confirm_button).setOnClickListener(v -> {
            confirm = true;
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });

        findViewById(R.id.disconnect_cancel_button).setOnClickListener(v -> {
            cancel = true;
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });
    }
}
