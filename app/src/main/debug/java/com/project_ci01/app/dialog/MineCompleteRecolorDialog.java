package com.project_ci01.app.dialog;

import com.project_ci01.app.dao.ImageEntity;
import com.project_ci01.app.R;
import com.project_ci01.app.base.view.dialog.BaseDialog;

public class MineCompleteRecolorDialog extends BaseDialog {

    private ImageEntity entity;

    @Override
    protected String tag() {
        return "MineCompleteRecolorDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_mine_complete_recolor;
    }

    @Override
    protected void init() {
        findViewById(R.id.recolor).setOnClickListener(v -> {
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

    public void setImageEntity(ImageEntity entity) {
        this.entity = entity;
    }

    public ImageEntity getImageEntity() {
        return entity;
    }
}
