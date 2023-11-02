package com.project_m1142.app.ping;

import android.net.Network;
import android.net.NetworkCapabilities;

import androidx.annotation.NonNull;

import com.project_m1142.app.base.manage.LifecyclerManager;
import com.project_m1142.app.base.utils.NetUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NormalPingCommand extends BasePingCommand {
    private final String host;
    private final int count;
    private final int timeoutMs;
    private final NormalPingCallback callback;

    public NormalPingCommand(@NonNull final String host, int count, int timeoutMs, NormalPingCallback callback) {
        super(false, InetAddress.class);
        this.host = host;
        this.timeoutMs = timeoutMs;
        this.count = count;
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            final InetAddress dest;
            if (inetCls == InetAddress.class) {
                dest = InetAddress.getByName(host);
            } else {
                dest = NetUtils.getInetAddress(host, inetCls);
            }

            PingRunnable core = new PingRunnable(dest,
                    new PingRunnable.PingListener() {
                        @Override
                        public void onPing(long timeMs, final int index) {
                            if (!cancel && callback != null) {
                                callback.onCompleted(NormalPingCommand.this, host, timeMs, index);
                            }
                        }

                        @Override
                        public void onPingException(final Exception e, final int index) {
                            if (!cancel && callback != null) {
                                callback.onFailed(NormalPingCommand.this, host, e, index);
                            }
                        }
                    });

            core.setCount(count); // url 地址 ping 的次数
            core.setTimeoutMs(timeoutMs); //  url 地址每次 ping 时的超时时间为 5s

            if (wifi) {
                final Network network = NetUtils.getNetwork(LifecyclerManager.INSTANCE.getApplication(), NetworkCapabilities.TRANSPORT_WIFI);
                if (network == null) {
                    throw new UnknownHostException("Failed to find a WiFi Network");
                }
                core.setNetwork(network);
            }
            pingRunnable = core;
            core.run();

        } catch (UnknownHostException e) {
            if (!cancel && callback != null) {
                callback.onFailed(NormalPingCommand.this, host, e, -1);
            }
        }
    }
}
