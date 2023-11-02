package com.project_m1142.app.ui.helper;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.utils.LogUtils;

public class StartHelper {
    private static final String TAG = "StartHelper";

    private static final int WHAT_PROGRESS_UPDATE = 10001;
    private static final long UPDATE_STEP = 50L; // 100ms: 每隔 100ms 更新一次 seekbar，更新值为 step
    private static final long FAST_TIME = 500L; // 500ms: 0.5s 内快速走完进度

    private static final int MAX_PROGRESS = 100;

//    private final SeekBar seekBar;

    private long duration; // 总时长 ms

    private float step; // progress/100ms :  每100ms的进度值

    private float fProgress;

    private int intProgress;

    private final ProgressHandler handler;

    private ReadyProgressListener listener;

    public StartHelper() {
//        LayoutInflater.from(context).inflate(R.layout.view_loading_progress, this, true);
//        seekBar = findViewById(R.id.seekbar);
        handler = new ProgressHandler();

//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (listener != null) {
//                    listener.onChanged(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

    public void setReadyProgressListener(ReadyProgressListener listener) {
        this.listener = listener;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void startProgress() {
        fProgress = 0; // reset
//        if (seekBar != null) {
//            seekBar.setProgress(0); // reset
//        }
        intProgress = 0;
        if (listener != null) {
            listener.onChanged(intProgress);
        }
        calcNormal();
        handler.sendProgressUpdate(true);
    }

    public void stopProgress() {
        handler.removeProgressUpdate();
    }

    public void switchFast() {
        calcFast();
    }

    private void calcNormal() {
        if (duration <= 0) {
            Log.e(TAG, "--> calcNormal() failed !!! because of invalid duration=" + duration);
            return;
        }
        int remain = MAX_PROGRESS - intProgress;
        // 100ms step => duration/100ms * step = remain
        step = UPDATE_STEP * remain * 1.0f / duration;
        LogUtils.e(TAG, "--> calcNormal()  step=" + step);
    }

    private void calcFast() {
        int remainProgress = MAX_PROGRESS - intProgress;
        // 100ms step => fastTime/100ms * step = remainProgress
        step = UPDATE_STEP * remainProgress * 1.0f / FAST_TIME;
        LogUtils.e(TAG, "--> calcFast()  step=" + step);
    }

    private void update() {
//        if (seekBar == null) {
//            return;
//        }

//        int maxProgress = seekBar.getMax();

        if (fProgress < MAX_PROGRESS) {
            fProgress += step;
        }

        if (fProgress > MAX_PROGRESS) {
            fProgress = MAX_PROGRESS;
        }

//        Slog.e(TAG, "--> update()  fProgress=" + fProgress);

//        seekBar.setProgress((int) fProgress);
        intProgress = (int) fProgress;
        if (listener != null) {
            listener.onChanged(intProgress);
        }

        if (fProgress < MAX_PROGRESS) {
            handler.sendProgressUpdate(false);
        } else {
            handler.removeProgressUpdate();
            if (listener != null) {
                listener.onCompleted();
            }
        }
    }

    private class ProgressHandler extends Handler {

        public ProgressHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == WHAT_PROGRESS_UPDATE) {
                update();
            }
        }

        private void sendProgressUpdate(boolean fromUser) {
            if (hasMessages(WHAT_PROGRESS_UPDATE)) {
                removeMessages(WHAT_PROGRESS_UPDATE);
            }
            sendEmptyMessageDelayed(WHAT_PROGRESS_UPDATE, fromUser ? 0 : UPDATE_STEP);
        }

        private void removeProgressUpdate() {
            if (hasMessages(WHAT_PROGRESS_UPDATE)) {
                removeMessages(WHAT_PROGRESS_UPDATE);
            }
        }
    }

    public interface ReadyProgressListener {
        void onChanged(int progress);
        void onCompleted();
    }
}
