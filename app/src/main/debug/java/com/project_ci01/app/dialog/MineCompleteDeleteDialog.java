package com.project_ci01.app.dialog;

import com.project_ci01.app.dao.ImageEntityNew;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.dialog.BaseDialog;

public class MineCompleteDeleteDialog extends BaseDialog {

    private ImageEntityNew entity;

    @Override
    protected String tag() {
        return "MineCompleteDeleteDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_mine_complete_delete;
    }

    @Override
    protected void init() {
        findViewById(R.id.delete).setOnClickListener(v -> {
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
