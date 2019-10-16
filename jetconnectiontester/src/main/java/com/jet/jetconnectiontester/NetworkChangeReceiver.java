package com.jet.jetconnectiontester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        try {

            String action = intent.getAction();

            if(BuildConfig.DEBUG){
                Log.e(TAG, "NetworkChangeReceiver: "+action);
            }

            if(action != null && action.equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")){

                if (isConnected(context)) {

                    /* sending message to Main Activity*/
                    try {
                        Intent netConnectionintent = new Intent("internet_connection");
                        netConnectionintent.setAction("internet_connection");
                        netConnectionintent.putExtra("isNetConnected", true);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(netConnectionintent);

                    } catch (Exception e) {

                        if(BuildConfig.DEBUG){
                            Log.e(TAG, e.toString());
                        }
                    }

                } else {

                    Intent netConnectionintent = new Intent("internet_connection");
                    netConnectionintent.setAction("internet_connection");
                    netConnectionintent.putExtra("isNetConnected", false);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(netConnectionintent);

                }

            }


        } catch (Exception e) {

            if(BuildConfig.DEBUG){
                Log.e(TAG, e.toString());
            }
        }
    }

    public boolean isConnected(Context context) {
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();

        } catch (Exception e) {

            return false;
        }
    }

}
