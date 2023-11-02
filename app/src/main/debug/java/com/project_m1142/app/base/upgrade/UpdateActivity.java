package com.project_m1142.app.base.upgrade;

import android.os.Message;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.project_m1142.app.base.config.AppConfig;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.advert.NativeDisplayActivity;
import com.project_m1142.app.base.bean.event.UpgradeEvent;
import com.project_m1142.app.base.bean.gson.UpdateBean;
import com.project_m1142.app.base.event.IEventListener;
import com.project_m1142.app.base.manage.ConfigManager;
import com.project_m1142.app.base.view.dialog.BaseDialog;
import com.project_m1142.app.base.view.dialog.DialogHelper;
import com.project_m1142.app.ui.dialog.LimitDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class UpdateActivity<T extends BaseDialog> extends NativeDisplayActivity implements IEventListener {

    private static final int WHAT_APP_UPDATE = 8001;

    private boolean futureUpdate = false;
    private UpdateBean updateBeanInFuture;

    protected T updateDialog;

    private LimitDialog limitDialog;

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkLimit() && futureUpdate && updateBeanInFuture != null) {
            sendUpgrade(updateBeanInFuture);
            futureUpdate = false;
            updateBeanInFuture = null;
        }
    }

    private void sendUpgrade(UpdateBean updateBean) {
        if (uiHandler.hasMessages(WHAT_APP_UPDATE)) {
            uiHandler.removeMessages(WHAT_APP_UPDATE);
        }
        if (!updateBean.isForce() && ConfigManager.INSTANCE.hasPromptUpgradeToday()) { // 非强制更新一天只弹一次
            return;
        }
        Message message = uiHandler.obtainMessage();
        message.what = WHAT_APP_UPDATE;
        message.obj = updateBean;
        uiHandler.sendMessageDelayed(message, 200);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(UpgradeEvent event) {
        LogUtils.e(TAG, "--> onEvent UpgradeEvent  event=" + event);
        if (!checkLimit()) {
            sendUpgrade(event.getData());
        }
    }

    private void handUpdate(UpdateBean updateBean) {
        if (isResumed) {
            showUpdateDialog(updateBean);
        } else {
            futureUpdate = true;
            updateBeanInFuture = updateBean;
        }
    }

    @CallSuper
    @Override
    protected void handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_APP_UPDATE && msg.obj instanceof UpdateBean) {
            handUpdate((UpdateBean) msg.obj);
        }
    }

    private boolean checkLimit() {
        if (!AppConfig.VPN_LIMIT) { // just for debug
            return false;
        }
        boolean limit = ConfigManager.INSTANCE.checkLimit();
        LogUtils.e(TAG, "--> checkLimit()  limit=" + limit);
        if (limit) {
            showLimitDialog();
        }
        return limit;
    }

    private void showLimitDialog() {
        limitDialog = DialogHelper.showDialog(this, limitDialog, LimitDialog.class, null);
    }

    protected abstract void showUpdateDialog(UpdateBean updateBean);
}
