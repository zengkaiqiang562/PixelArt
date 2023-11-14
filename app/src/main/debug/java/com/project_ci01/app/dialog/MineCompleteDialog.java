package com.project_ci01.app.dialog;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project_ci01.app.dao.ImageEntity;
import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class MineCompleteDialog extends BaseDialog {

    private ImageEntity entity;

    private OnActionListener onActionListener;

    @Override
    protected String tag() {
        return "MineCompleteDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_mine_complete;
    }

    @Override
    protected void init() {

        findViewById(R.id.ll_recolor).setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onRecolor();
            }
            dismiss();
        });

        findViewById(R.id.ll_delete).setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onDelete();
            }
            dismiss();
        });

        findViewById(R.id.ll_share).setOnClickListener(v -> {
            if (onActionListener != null) {
                onActionListener.onShare();
            }
            dismiss();
        });

        if (entity == null) {
            return;
        }

        ImageView completeImage = findViewById(R.id.complete_image);

        Glide.with(context)
                .load(entity.colorImagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不走本地缓存
                .skipMemoryCache(true) // 不走内存缓存
                .into(completeImage);
    }

    public void setImageEntity(ImageEntity entity) {
        this.entity = entity;
    }

    public ImageEntity getImageEntity() {
        return entity;
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this.onActionListener = onActionListener;
    }

    public interface OnActionListener {
        void onRecolor();
        void onDelete();
        void onShare();
    }
}
