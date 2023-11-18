package com.project_ci01.app.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.SPUtils;
import com.project_ci01.app.base.advert.AdResourceActivity;
import com.project_ci01.app.base.advert.AdResourceManager;
import com.project_ci01.app.base.advert.PlaceType;
import com.project_ci01.app.base.advert.Resource;
import com.project_ci01.app.base.bean.event.AdResourceEvent;
import com.project_ci01.app.base.config.AppConfig;
import com.project_ci01.app.base.constants.SPConstants;
import com.project_ci01.app.base.event.EventBusHelper;
import com.project_ci01.app.base.event.IEventListener;
import com.project_ci01.app.base.manage.ConfigManager;
import com.project_ci01.app.base.manage.ContextManager;
import com.project_ci01.app.base.manage.EventTracker;
import com.project_ci01.app.base.user.User;
import com.project_ci01.app.base.user.UserService;
import com.project_ci01.app.base.utils.LogUtils;
import com.project_ci01.app.config.IConfig;
import com.project_ci01.app.databinding.ActivityStartBinding;
import com.project_ci01.app.helper.StartHelper;

public class StartActivity extends AdResourceActivity implements IEventListener {

    private static final int WHAT_READY_TIMEOUT = 5001;

    ActivityStartBinding binding;

    private final StartHelper startHelper = new StartHelper();

    private boolean ready = false;

    private Resource<?> startRes;

    private int startTime;

    private boolean privacyShowing = false;

    @Override
    protected String tag() {
        return "StartActivity";
    }

    @Override
    protected void setContentView() {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected View stubBar() {
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ready = false;
        init(getIntent());
        // TODO 第一版不接广告
//        AdResourceManager.INSTANCE.pullAds();
        EventBusHelper.register(this);

        EventTracker.trackStartShow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        int maxLoadDuration = ConfigManager.INSTANCE.getMaxLaunchTime();
        startHelper.setDuration(maxLoadDuration * 1000L);
        startHelper.setReadyProgressListener(new StartHelper.ReadyProgressListener() {
            @Override
            public void onChanged(int progress) {
                float fProgress = progress * 1.0f / 100;
            }

            @Override
            public void onCompleted() {
                // 进度完成，总是会尝试去展示下 start
                showAdInStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        boolean agreed = SPUtils.getInstance().getBoolean(SPConstants.SP_ACCEPT_PROTOCOL, false);
        LogUtils.e(TAG, "--> agreed=" + agreed + ",  privacyShowing=" + privacyShowing);
        if (!agreed) {
            startPrivacyActivity();
        } else {
            startHelper.startProgress();
            startTime = 0;
            sendReadyTimeout(AppConfig.MIN_LAUNCH_TIME);
        }
    }

    @Override
    public void onBackPressed() {
        /* forbidden back button */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e(TAG, "--> onActivityResult()  requestCode=" + requestCode + "  resultCode=" + resultCode + "  data=" + data);

        if (requestCode == IConfig.REQUEST_PRIVACY_ACTIVITY) {
            privacyShowing = false;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

//        if (requestCode == IntentConstants.REQUEST_CODE_PRIVACY) { // 同意隐私后跳转
//
//        }
    }


    /*=================================*/

    private void startPrivacyActivity() {
        if (checkTurnFlag() && !privacyShowing) {
            Intent intent = new Intent(this, PrivacyActivity.class);
            startActivityForResult(intent, IConfig.REQUEST_PRIVACY_ACTIVITY);
            privacyShowing = true;
        }
    }

    private void turn2Next(long delay) { // delay: ms

        ContextManager.INSTANCE.printActivityHistoryStack();

        uiHandler.postDelayed(() -> {

            LogUtils.e(TAG, "--> turn2Next() postDelayed  isResumed=" + isResumed);
            if (!isResumed) { // 不在前台不跳转页面
                return;
            }

            if (!ContextManager.INSTANCE.contains(PrivacyActivity.class)
                    && !ContextManager.INSTANCE.contains(PixelActivity.class)
                    && !ContextManager.INSTANCE.contains(CompleteActivity.class)
            )
//            if (true) // TODO
            { // 冷启动 or 热启动时任务栈中无首页 or 首页在栈顶则跳首页
                LogUtils.e(TAG, "--> turn2Next() turn to MainActivity");
                startActivity(new Intent(this, MainActivity.class));
            }

            ConfigManager.INSTANCE.upgrade();

            // post 中结束页面，避免当任务栈为空时，切换页面会显示桌面
            uiHandler.post(this::finish);

        }, delay);

    }

    @Override
    public void onEvent(AdResourceEvent event) {
        switch (event.getType()) {
            case TYPE_AD_READY:
                // 没显示过广告，收到 start 广告准备好的回调，且启动动画播放了 3s 以上，则停止动画，显示广告
                if (!ready && isResumed && startTime >= AppConfig.MIN_LAUNCH_TIME) {
                    // 收到 start 广告准备好的回调，且启动动画播放了 3s 以上，则停止动画，显示广告
                    LogUtils.e(TAG, "--> TYPE_AD_READY fast launch ...");
                    removeReadyTimeout();
                    startHelper.switchFast();
                }
                break;
            case TYPE_AD_DISMISS:
            case TYPE_AD_UNSHOW:
                if (startRes == event.getData()) {
                    turn2Next(300);
                }
                startRes = null; // reset
                break;
            case TYPE_AD_SHOW:
                if (startRes == event.getData()) {
//                    EventTracker.trackIntAdInStartShow();
                }
                break;
            case TYPE_AD_UNPULL:
                // Loading 期间拉取失败时，重复进行拉取
                if (event.getData() != null) {
                    PlaceType placeType = PlaceType.convert(event.getData().getPlaceBean().getPlace());
                    if (placeType != null) {
                        Message message = uiHandler.obtainMessage();
                        message.what = placeType.ordinal();
                        message.obj = placeType;
                        uiHandler.sendMessageDelayed(message, 5 * 1000L); // 5s 后重新拉
                    }
                }
                break;
        }
        super.onEvent(event);
    }

    @Override
    public void onStop() {
        super.onStop();
        startHelper.stopProgress();
        removeReadyTimeout();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ready = false;
        EventBusHelper.unregister(this);
    }

    private void showAdInStart() {

        // 迭代 1.0.5：自然用户：只有connect，原生广告，banner, loading写死3s；
        if (UserService.getService().getUser() == User.ORGANIC) {
            turn2Next(0);
            return;
        }

        startRes = AdResourceManager.INSTANCE.findMostWeightResource(PlaceType.START);
        boolean unshow = true;
        if (startRes != null) {
            unshow = !startRes.show(this, null);
        }

        if (unshow) { // 如果广告显示失败，设置跳转标记，并跳下一页（如果显示成功，则在广告关闭的回调中跳下一页）
            LogUtils.d("无可展示的<" + PlaceType.START.type + ">广告，跳转到首页");
            turn2Next(0);
        }
    }


    /*=============================== Handler ==================================*/

    @Override
    public void handleMessage(@NonNull Message msg) {
        if (msg.what == WHAT_READY_TIMEOUT) {
            LogUtils.e(TAG, "--> WHAT_READY_TIMEOUT delay=" + msg.arg1);
            checkReady(msg.arg1);
            return;
        }

        if (msg.obj instanceof PlaceType) {
            PlaceType placeType = (PlaceType) msg.obj;
            LogUtils.e(TAG, "--> handleMessage pullAdInPlace  placeType=" + placeType);
            AdResourceManager.INSTANCE.pullAdInPlace(placeType);
        }
    }

    private void sendReadyTimeout(int delay) {
        LogUtils.e(TAG, "--> sendReadyTimeout()  delay=" + delay);

        if (uiHandler.hasMessages(WHAT_READY_TIMEOUT)) {
            uiHandler.removeMessages(WHAT_READY_TIMEOUT);
        }

        Message message = uiHandler.obtainMessage();
        message.what = WHAT_READY_TIMEOUT;
        message.arg1 = delay;
        uiHandler.sendMessageDelayed(message, delay * 1000L);
    }

    private void removeReadyTimeout() {
        if (uiHandler.hasMessages(WHAT_READY_TIMEOUT)) {
            uiHandler.removeMessages(WHAT_READY_TIMEOUT);
        }
    }

    private void checkReady(int delay) {

        startTime += delay;

        int maxLaunchTime = ConfigManager.INSTANCE.getMaxLaunchTime();

        ready = AdResourceManager.INSTANCE.isReady();

        LogUtils.e(TAG, "--> checkReady()  maxLaunchTime=" + maxLaunchTime + " startTime=" + startTime + "  ready=" + ready);

        LogUtils.d("启动页的动画时长已经历<" + startTime + ">秒");

//        if (ready) { // 广告已准备好
            if (true) { // TODO 第一版不接广告
            startHelper.switchFast();
            return;
        }

        // 没拉到继续等待
        delay = maxLaunchTime - startTime;
        if (delay > 0) {
            sendReadyTimeout(delay);
            return;
        }

        LogUtils.d("启动页的动画已达到最大时长");
    }

}
