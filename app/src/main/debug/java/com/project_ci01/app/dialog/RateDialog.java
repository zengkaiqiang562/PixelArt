package com.project_ci01.app.dialog;

import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.SPUtils;
import com.project_ci01.app.R;
import com.project_ci01.app.base.config.AppConfig;
import com.project_ci01.app.base.constants.SPConstants;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.view.dialog.BaseDialog;

public class RateDialog extends BaseDialog implements View.OnClickListener {

    private static final int RATE_NUM = 5;

    private static final int[] sRateIds = {
            R.id.rate_star_1,
            R.id.rate_star_2,
            R.id.rate_star_3,
            R.id.rate_star_4,
            R.id.rate_star_5
    };

    private int count;


    private final ImageView[] ivRates = new ImageView[RATE_NUM];

    @Override
    protected String tag() {
        return "RateDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_rate;
    }

    @Override
    protected void init() {

//        findViewById(R.id.rate_confirm_button).setOnClickListener(v -> {
//            confirm = true;
//            if (listener != null) {
//                listener.onConfirm();
//            }
//            dismiss();
//        });

        findViewById(R.id.rate_cancel_button).setOnClickListener(v -> {
            cancel = true;
            if (listener != null) {
                listener.onCancel();
            }
            dismiss();
        });

        for (int i = 0; i < sRateIds.length; i++) {
            ivRates[i] = findViewById(sRateIds[i]);
            ivRates[i].setOnClickListener(this);

            if (i == sRateIds.length - 1) {
                ivRates[i].setAnimation(AnimationUtils.loadAnimation(activity, R.anim.scale_conn_btn));
            }
        }
    }

    public void changeRate(int ratingCount) {
        for (int i = 0; i < ivRates.length; i++) {
            ivRates[i].setSelected(i < ratingCount);
        }

        if (ratingCount >= 4.0f) {
            ContextManager.turn2Playstore(activity, activity.getPackageName());
        } else {
            ContextManager.turn2Email(activity, AppConfig.FEEDBACK_EMAIL);
        }
        SPUtils.getInstance().put(SPConstants.SP_HAS_BEEN_RATED, true);
        rootView.postDelayed(this::dismissAllowingStateLoss, 500);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rate_star_1) {
            count = 1;
        } else if (id == R.id.rate_star_2) {
            count = 2;
        } else if (id == R.id.rate_star_3) {
            count = 3;
        } else if (id == R.id.rate_star_4) {
            count = 4;
        } else if (id == R.id.rate_star_5) {
            count = 5;
        }

        changeRate(count);
    }
}
