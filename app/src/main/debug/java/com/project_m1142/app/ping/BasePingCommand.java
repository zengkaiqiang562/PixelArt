package com.project_m1142.app.ping;

import java.net.InetAddress;

public abstract class BasePingCommand implements Runnable {
    protected final boolean wifi;
    protected final Class<? extends InetAddress> inetCls;
    protected PingRunnable pingRunnable;
    protected boolean cancel = false;

    public BasePingCommand(boolean wifi, Class<? extends InetAddress> inetCls) {
        this.wifi = wifi;
        this.inetCls = inetCls;
    }

    public void cancel() {
        if (pingRunnable != null) {
            pingRunnable.setCount(0);
        }
        cancel = true;
    }

    public boolean isCanceled() {
        return cancel;
    }
}
