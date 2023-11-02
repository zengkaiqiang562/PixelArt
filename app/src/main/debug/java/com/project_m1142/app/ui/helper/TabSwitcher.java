package com.project_m1142.app.ui.helper;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.view.BaseActivity;
import com.project_m1142.app.ui.activity.HomeActivity;
import com.project_m1142.app.ui.activity.MineActivity;
import com.project_m1142.app.ui.activity.WiFiActivity;

public class TabSwitcher {
    public static void startHomeActivity(@NonNull BaseActivity srcActivity) {
        if (srcActivity.checkTurnFlag()) {
            Intent intent = new Intent(srcActivity, HomeActivity.class);
            srcActivity.startActivity(intent);
            srcActivity.finish();
        }
    }

    public static void startWiFiActivity(@NonNull BaseActivity srcActivity) {
        if (srcActivity.checkTurnFlag()) {
            Intent intent = new Intent(srcActivity, WiFiActivity.class);
            srcActivity.startActivity(intent);
            if (!(srcActivity instanceof HomeActivity)) {
                srcActivity.finish();
            }
        }
    }

    public static void startMineActivity(@NonNull BaseActivity srcActivity) {
        if (srcActivity.checkTurnFlag()) {
            Intent intent = new Intent(srcActivity, MineActivity.class);
            srcActivity.startActivity(intent);
            if (!(srcActivity instanceof HomeActivity)) {
                srcActivity.finish();
            }
        }
    }
}
