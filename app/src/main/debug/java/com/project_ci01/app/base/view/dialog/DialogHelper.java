package com.project_ci01.app.base.view.dialog;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.project_ci01.app.base.manage.ContextManager;

public class DialogHelper {

    private static final String TAG = "DialogHelper";

    public static <T extends BaseDialog> T showDialog(FragmentActivity activity, T dialog, @NonNull Class<T> dialogClass, @Nullable DialogListener<T> listener) {
        if (!ContextManager.isSurvival(activity)) {
            return null;
        }

        if (dialog == null) {
            try {
                dialog = dialogClass.newInstance();

                if (listener != null) {
                    dialog.setListener(listener);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "--> showDialog  newInstance Exception=" + e);
                return null;
            }
        }

        if (listener != null) {
            listener.onShowBefore(dialog);
        }

        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            if (!dialog.isAdded() && fragmentManager.findFragmentByTag(dialog.fmTAG()) == null) {
                dialog.showNow(fragmentManager, dialog.fmTAG());
                dialog.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "--> showDialog  showNow Exception=" + e);
            return null;
        }

        return dialog;
    }
}
