package com.project_m1142.app.ping;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public enum NormalPingExecutor {

    INSTANCE;

    public static final long TIMEOUT = PingRunnable.TIMED_OUT_MS;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public NormalPingCommand ping(@NonNull String host, int count, int timeoutMs, NormalPingCallback callback) {
        NormalPingCommand command = new NormalPingCommand(host, count, timeoutMs, callback);
        executor.execute(command);
        return command;
    }
}
