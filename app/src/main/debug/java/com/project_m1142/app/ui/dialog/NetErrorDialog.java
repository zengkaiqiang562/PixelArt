package com.project_m1142.app.ui.dialog;

import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class NetErrorDialog extends BaseDialog {

    @Override
    protected String tag() {
        return "NetErrorDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_net_error;
    }

    @Override
    protected void init() {
        findViewById(R.id.neterr_try_again_button).setOnClickListener(v -> {
            confirm = true;
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });

        findViewById(R.id.neterr_cancel_button).setOnClickListener(v -> {
            cancel = true;
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });
    }
}
