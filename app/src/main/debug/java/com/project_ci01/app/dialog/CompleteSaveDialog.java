package com.project_ci01.app.dialog;

import com.project_ci01.app.R;
import com.project_ci01.app.base.view.dialog.BaseDialog;
import com.project_ci01.app.dao.ImageEntityNew;

public class CompleteSaveDialog extends BaseDialog {

    private ImageEntityNew entity;

    @Override
    protected String tag() {
        return "CompleteSaveDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_complete_save;
    }

    @Override
    protected void init() {
        findViewById(R.id.save_pic).setOnClickListener(v -> {
            confirm = true;
            if (listener != null) {
                listener.onConfirm();
            }
            dismiss();
        });

        findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });
    }

    public void setImageEntity(ImageEntityNew entity) {
        this.entity = entity;
    }

    public ImageEntityNew getImageEntity() {
        return entity;
    }
}
