package com.project_m1142.app.ui.dialog;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;
import com.project_m1142.app.R;
import com.project_m1142.app.base.bean.gson.UpdateBean;
import com.project_m1142.app.base.constants.SPConstants;
import com.project_m1142.app.base.manage.ContextManager;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class UpdateDialog extends BaseDialog {

    UpdateBean updateBean;

    @Override
    protected String tag() {
        return "UpdateDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_update;
    }

    @Override
    protected void init() {
        TextView tvNexttime = findViewById(R.id.update_cancel_button);
        TextView tvUpdate = findViewById(R.id.update_confirm_button);

        tvNexttime.setOnClickListener(v -> {
            dismiss();
        });

        tvUpdate.setOnClickListener(v -> {
            if (updateBean != null && activity != null) {
                String pkgName = TextUtils.isEmpty(updateBean.getPackage()) ? activity.getPackageName() : updateBean.getPackage();
                ContextManager.turn2Playstore(activity, pkgName);
            }
        });

        if (updateBean == null) {
            return;
        }

        ConstraintLayout.LayoutParams updateLayoutParams = (ConstraintLayout.LayoutParams) tvUpdate.getLayoutParams();
        if (updateBean.isForce()) {
            tvNexttime.setVisibility(View.GONE);
            updateLayoutParams.width = ConvertUtils.dp2px(286);
        } else {
            tvNexttime.setVisibility(View.VISIBLE);
            updateLayoutParams.width = ConvertUtils.dp2px(130);
        }
    }

    public void setUpgradeInfo(UpdateBean updateBean) {
        this.updateBean = updateBean;
    }

    @Override
    protected boolean allowOutCancel() {
        return false;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        LogUtils.e(TAG, "--> onDismiss()  upgradeBean=" + updateBean);

        if (updateBean != null && updateBean.isForce()) {
            ContextManager.INSTANCE.finishAll();
        }

        if (updateBean != null && !updateBean.isForce()) {
            SPUtils.getInstance().put(SPConstants.SP_UPGRADE_TIME, System.currentTimeMillis());
        }
    }
}
