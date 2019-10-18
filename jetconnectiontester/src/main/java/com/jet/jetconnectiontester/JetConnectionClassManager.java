package com.jet.jetconnectiontester;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JetConnectionClassManager {

    private static final String TAG = "JetConnectionManager";


    public JetConnectionClassManager(Context context, JetConnectionListner jetConnectionListner,
                                     ConnectionChangeListner connectionChangeListner){

        ConnectionBase.getInstance().connectionClassManager(context, jetConnectionListner, connectionChangeListner);

    }

    public JetConnectionClassManager(Context context,
                                     String url,
                                     JetConnectionListner jetConnectionListner,
                                     ConnectionChangeListner connectionChangeListner){

      ConnectionBase.getInstance().connectionClassManager(context,
              url,
              jetConnectionListner,
              connectionChangeListner);
    }

    public JetConnectionClassManager(Context context,  ConnectionChangeListner connectionChangeListner){

       ConnectionBase.getInstance().connectionClassManager(context,connectionChangeListner);
    }

    public void removeListner(){

        removeConnectionChangeListned();
        ConnectionBase.getInstance().mConnectionClassManager.remove(ConnectionBase.getInstance().mListener);
    }

    public void registerListner(){

        try {

            registerConnectionChangeListner();
            ConnectionBase.getInstance().mConnectionClassManager.reset();
            ConnectionBase.getInstance().mConnectionClassManager.register(ConnectionBase.getInstance().mListener);
            if (ConnectionBase.getInstance().isConnected(ConnectionBase.getInstance().context)) {

                ConnectionBase.getInstance().mTries = 0;
                ConnectionBase.getInstance().downloadImageresponseTime = 0;
                ConnectionBase.getInstance().downloadImageStartTime = System.currentTimeMillis();
                ConnectionBase.getInstance().mConnectionClass = ConnectionQuality.UNKNOWN;

                ConnectionBase.getInstance().startSpeedTest();
            } else {
                ConnectionBase.getInstance().jetConnectionListner.getCurrentBandWidth(ConnectionQuality.INTERNET_NOT_AVAILABLE);
            }
        }catch (Exception e){
            if(BuildConfig.DEBUG) {
                Log.e(TAG, e.toString());
            }
        }
    }


    public void registerConnectionChangeListner(){

        ConnectionBase.getInstance().networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        ConnectionBase.getInstance().context.registerReceiver(ConnectionBase.getInstance().networkChangeReceiver, intentFilter);

        LocalBroadcastManager.getInstance(ConnectionBase.getInstance().context).registerReceiver(ConnectionBase.getInstance().netConnectionReceiver, new IntentFilter("internet_connection"));
    }

    public void removeConnectionChangeListned(){

        if(ConnectionBase.getInstance().networkChangeReceiver != null){
            ConnectionBase.getInstance().context.unregisterReceiver(ConnectionBase.getInstance().networkChangeReceiver);
            ConnectionBase.getInstance().networkChangeReceiver = null;
        }

        if (ConnectionBase.getInstance().netConnectionReceiver != null) {
            LocalBroadcastManager.getInstance(ConnectionBase.getInstance().context).unregisterReceiver(ConnectionBase.getInstance().netConnectionReceiver);
        }
    }

}
