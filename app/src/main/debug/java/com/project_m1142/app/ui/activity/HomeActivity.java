package com.project_m1142.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieDrawable;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
// import com.library.ssr.Core;
// import com.library.ssr.bg.BaseService;
// import com.library.ssr.net.HttpsTest;
import com.project_m1142.app.R;
import com.project_m1142.app.base.bean.gson.UpdateBean;
import com.project_m1142.app.base.event.EventBusHelper;
import com.project_m1142.app.base.manage.EventTracker;
import com.project_m1142.app.base.utils.LogUtils;
import com.project_m1142.app.base.utils.NetUtils;
import com.project_m1142.app.base.view.dialog.DialogHelper;
import com.project_m1142.app.base.view.dialog.SimpleDialogListener;
import com.project_m1142.app.databinding.ActivityHomeBinding;
import com.project_m1142.app.ui.constants.IntentConstants;
import com.project_m1142.app.ui.dialog.DisconnectDialog;
import com.project_m1142.app.ui.dialog.NetErrorDialog;
import com.project_m1142.app.ui.dialog.TestGuideDialog;
import com.project_m1142.app.ui.dialog.UpdateDialog;
import com.project_m1142.app.ui.helper.TabSwitcher;

public class HomeActivity extends SsrUpdateActivity<UpdateDialog> {

    ActivityHomeBinding binding;

    private DisconnectDialog disconnectDialog;
    private TestGuideDialog testGuideDialog;
    private NetErrorDialog netErrorDialog;

    private final NetCheckHandler netCheckHandler = new NetCheckHandler();

    @Override
    protected String tag() {
        return "HomeActivity";
    }

    @Override
    protected void setContentView() {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return binding.stubBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getIntent());

        EventBusHelper.register(this);

        EventTracker.trackHomeShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {

        View.OnClickListener clickWifiListener = v -> {
            // if (isConnecting() || netCheckHandler.netChecking) {
            //     // 正在连接 or 网络检查
            //     ToastUtils.showShort(R.string.toast_connecting);
            //     return;
            // }
            // TabSwitcher.startWiFiActivity(this);
        };
        binding.homeMenuWifiParent.setOnClickListener(clickWifiListener);
        binding.homeLeftButton.setOnClickListener(clickWifiListener);

        View.OnClickListener clickMineListener = v -> {
            // if (isConnecting() || netCheckHandler.netChecking) {
            //     // 正在连接 or 网络检查
            //     ToastUtils.showShort(R.string.toast_connecting);
            //     return;
            // }
            // TabSwitcher.startMineActivity(this);
        };
        binding.homeMenuMineParent.setOnClickListener(clickMineListener);
        binding.homeRightButton.setOnClickListener(clickMineListener);

        binding.homeButtonTest.setOnClickListener(v -> {
            // if (isConnecting() || netCheckHandler.netChecking) {
            //     // 正在连接 or 网络检查
            //     ToastUtils.showShort(R.string.toast_connecting);
            //     return;
            // }
            // startTestActivity();
        });

        binding.homeConnectButton.setOnClickListener(v -> {
            // toggle();
        });
    }

    @Override
    public void onBackPressed() {
        /* forbidden back button */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);
        netCheckHandler.removeNetCheck();
    }


    // @Override
    // protected void toggle() {
    //     if (state.getCanStop()) {
    //         showDisconnectDialog();
    //     } else {
    //         if (NetworkUtils.isConnected()) { // 有网络时，直接开始连接
    //             connect.launch(null);
    //         } else { // 无网络时，弹窗提示
    //             showNetErrorDialog();
    //         }
    //     }
    // }

    // @Override
    // protected void changeState(BaseService.State state, @Nullable String msg, Boolean animate, boolean fromListener) {
    //     BaseService.State lastState = this.state;
    //     Log.e(TAG, "--> changeState()  lastState=" + lastState + "  state=" + state
    //             + ", msg=" + msg + ", animate=" + animate + ", fromListener=" + fromListener);

    //     if (state == BaseService.State.Connected) {
    //         if (fromListener) {
    //             // 连接成功，开始测试是否能上网
    //             testConnection(state);

    //             // 更新状态（只是连接到 VPN 节点了，但该节点是否能上网还需要通过 HttpTester 进行测试）
    //             tester.getStatus().observe(this, status -> {
    //                 Log.e(TAG, "--> HttpTester test status=" + status);

    //                 status.retrieve(text -> {
    //                     Log.e(TAG, "--> HttpTester test status.text=" + text);
    //                     return null;
    //                 }, errMsg -> {
    //                     Log.e(TAG, "--> HttpTester test status.errMsg=" + errMsg);
    //                     return null;
    //                 });

    //                 if (status instanceof HttpsTest.Status.Idle) { // 测试准备完成

    //                 } else if (status instanceof HttpsTest.Status.Testing) { // 测试中

    //                 } else if (status instanceof HttpsTest.Status.Error) { // 测试失败
    //                     stopVpn(); // 测试失败断开连接
    //                     startUnconnectReportActivity(); // 连接失败
    //                 } else { // HttpsTest.Status.Success 测试成功
    //                     showConnectedView();
    //                     if (lastState == BaseService.State.Connecting) {
    //                         startConnectedReportActivity(); // 连接成功
    //                     }
    //                 }
    //             });
    //         } else { // 页面回到前台时的状态恢复，不再测试
    //             showConnectedView();
    //         }
    //     } else {
    //         tester.getStatus().removeObservers(this);
    //         if (state != BaseService.State.Idle) {
    //             tester.invalidate();
    //         }
    //         // 更新状态
    //         switch (state) {
    //             case Connecting:
    //                 showConnectingView();
    //                 break;
    //             case Stopping:
    //                 break;
    //             default: // Idle / Stopped
    //                 showUnconnectView();
    //                 break;
    //         }
    //     }

    //     this.state = state;
    // }

    private void showUnconnectView() {
        binding.homeConnectButton.setVisibility(View.VISIBLE);
        binding.homeConnectButton.setImageResource(R.drawable.home_unconnect_icon);

        binding.homeLeftButton.setVisibility(View.INVISIBLE);
        binding.homeRightButton.setVisibility(View.INVISIBLE);

        binding.homeConnectingAnim.setVisibility(View.INVISIBLE);
        binding.homeConnectingAnim.cancelAnimation();
    }

    private void showConnectingView() {
        binding.homeConnectButton.setVisibility(View.INVISIBLE);

        binding.homeLeftButton.setVisibility(View.INVISIBLE);
        binding.homeRightButton.setVisibility(View.INVISIBLE);

        binding.homeConnectingAnim.setVisibility(View.VISIBLE);
        if (!binding.homeConnectingAnim.isAnimating()) {
            binding.homeConnectingAnim.setRepeatCount(LottieDrawable.INFINITE);
            binding.homeConnectingAnim.playAnimation();
        }

        EventTracker.trackConnectClick();
    }

    private void showConnectedView() {
        binding.homeConnectButton.setVisibility(View.VISIBLE);
        binding.homeConnectButton.setImageResource(R.drawable.home_connected_icon);

        binding.homeLeftButton.setVisibility(View.VISIBLE);
        binding.homeRightButton.setVisibility(View.VISIBLE);

        binding.homeConnectingAnim.setVisibility(View.INVISIBLE);
        binding.homeConnectingAnim.cancelAnimation();
    }

    private void startConnectedReportActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, ConnectedReportActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_CONNECTED_REPORT);
        }
    }

    private void startUnconnectReportActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, UnconnectReportActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_UNCONNECT_REPORT);
        }
    }

    private void startTestActivity() {
        if (checkTurnFlag()) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivityForResult(intent, IntentConstants.REQUEST_CODE_TEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(TAG, "--> onActivityResult()  requestCode=" + requestCode + "  resultCode=" + resultCode + "  data=" + data);

        if (resultCode != RESULT_OK) {
            return;
        }

        // if (requestCode == IntentConstants.REQUEST_CODE_UNCONNECT_REPORT) {
        //     if (data != null && data.getBooleanExtra(IntentConstants.EXTRA_RECONNECT_FLAG, false)) {
        //         startVpn();
        //     }
        // } else if (requestCode == IntentConstants.REQUEST_CODE_CONNECTED_REPORT) {
        //     if (state == BaseService.State.Connected) {
        //         showTestGuideDialog();
        //     }
        // }
    }


    @Override
    protected void showUpdateDialog(UpdateBean updateBean) {
        updateDialog = DialogHelper.showDialog(this, updateDialog, UpdateDialog.class, new SimpleDialogListener<UpdateDialog>() {
            @Override
            public void onShowBefore(UpdateDialog dialog) {
                dialog.setUpgradeInfo(updateBean);
            }
        });
    }

    private void showDisconnectDialog() {
        // disconnectDialog = DialogHelper.showDialog(this, disconnectDialog, DisconnectDialog.class, new SimpleDialogListener<>() {
        //     @Override
        //     public void onConfirm() {
        //         startConnectedReportActivity(); // 断开连接
        //         Core.INSTANCE.stopService();
        //     }
        // });
    }

    private void showTestGuideDialog() {
        testGuideDialog = DialogHelper.showDialog(this, testGuideDialog, TestGuideDialog.class, new SimpleDialogListener<>() {
            @Override
            public void onConfirm() {
                startTestActivity();
            }
        });
    }

    private void showNetErrorDialog() {
        netErrorDialog = DialogHelper.showDialog(this, netErrorDialog, NetErrorDialog.class, new SimpleDialogListener<>() {
            @Override
            public void onConfirm() {
                netCheckHandler.sendNetCheck();
            }
        });
    }

    /*=============== Net Error Handler =============*/

    private class NetCheckHandler extends Handler {

        static final int WHAT_NET_CHECK = 2000;

        boolean netChecking = false;

        NetCheckHandler() {
            super(Looper.getMainLooper());
        }

        void sendNetCheck() {
            netChecking = true;
            if (hasMessages(WHAT_NET_CHECK)) {
                removeMessages(WHAT_NET_CHECK);
            }
            sendEmptyMessageDelayed(WHAT_NET_CHECK, 2 * 1000L); // 延迟 2s 后再检查下网络
            showConnectingView();
        }

        void removeNetCheck() {
            if (hasMessages(WHAT_NET_CHECK)) {
                removeMessages(WHAT_NET_CHECK);
            }
            showUnconnectView();
            netChecking = false;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            // if (msg.what == WHAT_NET_CHECK) {
            //     if (isResumed && NetworkUtils.isConnected()) { // 在前台，有网络，开始连接
            //         startVpn();
            //     } else if (isResumed) { // 在前台，无网络，继续弹窗
            //         showNetErrorDialog();
            //         showUnconnectView();
            //     } else { // 在后台，不管有无网络，恢复到未连接状态，不做其他操作
            //         showUnconnectView();
            //     }
            //     netChecking = false;
            // }
        }
    }
}
