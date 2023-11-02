package com.project_m1142.app.base.view.dialog;

public interface DialogListener <T extends BaseDialog> {
    void onShowBefore(T dialog);
    void onResume(BaseDialog dialog);
    void onConfirm();
    void onCancel();
    void onBack(); // Dialog 中的返回键
    void onDismiss();
}
