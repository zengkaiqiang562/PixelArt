package com.project_ci01.app.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import com.blankj.utilcode.constant.MemoryConstants;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
    /**
     * Attempts to get an IPv6 address
     * @param host
     * @return
     * @throws UnknownHostException
     */
    public static InetAddress getInetAddress(final String host, Class<? extends InetAddress> inetClass) throws UnknownHostException {
        final InetAddress[] inetAddresses = InetAddress.getAllByName(host);
        InetAddress dest = null;
        for (final InetAddress inetAddress : inetAddresses) {
            if (inetClass.equals(inetAddress.getClass())) {
                return inetAddress;
            }
        }
        throw new UnknownHostException("Could not find IP address of type " + inetClass.getSimpleName());
    }

    public static Network getNetwork(final Context context, final int transport) {
        final ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        for (Network network : connManager.getAllNetworks()) {
            NetworkCapabilities networkCapabilities = connManager.getNetworkCapabilities(network);
            if (networkCapabilities != null &&
                    networkCapabilities.hasTransport(transport) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                return network;
            }
        }
        return null;
    }

    public static String byte2FitMemorySize(final long byteSize) {
        int precision = 1;
        if (byteSize < 0) {
            throw new IllegalArgumentException("byteSize shouldn't be less than zero!");
        } else if (byteSize < MemoryConstants.KB) {
            return String.format("%." + precision + "fB", (double) byteSize);
        } else if (byteSize < MemoryConstants.MB) {
            return String.format("%." + precision + "fKB", (double) byteSize / MemoryConstants.KB);
        } else if (byteSize < MemoryConstants.GB) {
            return String.format("%." + precision + "fMB", (double) byteSize / MemoryConstants.MB);
        } else {
            return String.format("%." + precision + "fGB", (double) byteSize / MemoryConstants.GB);
        }
    }
}
