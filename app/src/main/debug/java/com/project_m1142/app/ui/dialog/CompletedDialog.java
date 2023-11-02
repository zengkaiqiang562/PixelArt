package com.project_m1142.app.ui.dialog;

import android.animation.Animator;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.project_m1142.app.R;
import com.project_m1142.app.base.view.dialog.BaseDialog;

public class CompletedDialog extends BaseDialog {

    private LottieAnimationView animComplete;

    @Override
    protected String tag() {
        return "CompletedDialog";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_complete;
    }

    @Override
    protected void init() {
        animComplete = findViewById(R.id.complete_anim);
        animComplete.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                dismiss();
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        animComplete.postDelayed(() -> {
            startAnim();
        }, 100);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAnim();
    }

    private void startAnim() {
        if (animComplete == null) {
            return;
        }
        if (animComplete.isAnimating()) {
            animComplete.pauseAnimation();
            animComplete.cancelAnimation();
        }
        animComplete.playAnimation();
    }

    private void stopAnim() {
        if (animComplete == null) {
            return;
        }
        if (animComplete.isAnimating()) {
            animComplete.pauseAnimation();
            animComplete.cancelAnimation();
        }
    }
}
