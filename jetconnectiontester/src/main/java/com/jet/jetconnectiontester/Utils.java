package com.jet.jetconnectiontester;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class Utils {

    public static ConnectionType checkNetWorkType(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info != null) {

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {

                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                if (wifiInfo != null) {
                    return ConnectionType.TYPE_WIFI;
                } else {
                    return ConnectionType.TYPE_NO_NETWORK;
                }

            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {

                return ConnectionType.TYPE_MOBILE;
            }

        } else {

            return ConnectionType.TYPE_NO_NETWORK;
        }

        return ConnectionType.TYPE_NO_NETWORK;
    }
}
