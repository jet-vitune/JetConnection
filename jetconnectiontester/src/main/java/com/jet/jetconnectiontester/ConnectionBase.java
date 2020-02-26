package com.jet.jetconnectiontester;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ConnectionBase {

    private static final String TAG = "ConnectionBase";

    private static ConnectionBase connectionBase;

    protected ConnectionClassManager mConnectionClassManager;
    protected DeviceBandwidthSampler mDeviceBandwidthSampler;
    protected ConnectionChangedListener mListener;
    protected ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;
    protected String mURL = "https://d2c018txhbneoo.cloudfront.net/ViTune/test.jpg";
    protected int mTries = 0;
    protected long downloadImageStartTime;
    protected long downloadImageresponseTime;
    protected Context context;
    protected JetConnectionListner jetConnectionListner;
    protected ConnectionChangeListner connectionChangeListner;
    protected NetworkChangeReceiver networkChangeReceiver;
    protected DownloadImage downloadImage;

    public static ConnectionBase getInstance() {

        if (connectionBase == null) {
            connectionBase = new ConnectionBase();
        }

        return connectionBase;
    }


    public void connectionClassManager(Context context, JetConnectionListner jetConnectionListner, ConnectionChangeListner connectionChangeListner) {

        this.context = context;
        this.jetConnectionListner = jetConnectionListner;
        this.connectionChangeListner = connectionChangeListner;

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        mConnectionClassManager.reset();

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Current BandWidth Quality: " + mConnectionClassManager.getCurrentBandwidthQuality().toString());
        }

        mListener = new ConnectionChangedListener();
    }

    public void connectionClassManager(Context context, String url, JetConnectionListner jetConnectionListner, ConnectionChangeListner connectionChangeListner) {

        this.context = context;
        this.mURL = url;
        this.jetConnectionListner = jetConnectionListner;
        this.connectionChangeListner = connectionChangeListner;

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        mConnectionClassManager.reset();

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "Current BandWidth Quality: " + mConnectionClassManager.getCurrentBandwidthQuality().toString());
        }

        mListener = new ConnectionChangedListener();
    }

    public void connectionClassManager(Context context, ConnectionChangeListner connectionChangeListner) {

        this.context = context;
        this.connectionChangeListner = connectionChangeListner;
    }

    private class ConnectionChangedListener implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "onBandwidthStateChange: " + mConnectionClass.toString());
            }
        }

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState, double bandWidth) {
            mConnectionClass = bandwidthState;

            downloadImageresponseTime = System.currentTimeMillis() - downloadImageStartTime;

            jetConnectionListner.getCurrentBandWidth(bandwidthState, bandWidth);
            jetConnectionListner.getCurrentBandWidth(bandwidthState, bandWidth, downloadImageresponseTime);

        }
    }

    public void startSpeedTest() {

        if (downloadImage != null) {
            downloadImage.cancel(true);
            downloadImage = null;
        }
        downloadImage = new DownloadImage(mURL);
        downloadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void stopSpeedTest(){
        if (downloadImage != null) {
            downloadImage.cancel(true);
            downloadImage = null;
        }
    }

    private class DownloadImage extends AsyncTask<Void, Void, Void> {

        Exception exception;
        String  url;

        DownloadImage(String imageUrl){
            url = imageUrl;
        }


        @Override
        protected void onPreExecute() {
            mDeviceBandwidthSampler.startSampling();

            if (BuildConfig.DEBUG) {
                Log.e(TAG, " startSampling");
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String imageURL = url;
            try {
                // Open a stream to download the image from our URL.
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[1024];

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }
            } catch (Exception e) {

                exception = e;
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Error while downloading image: " + e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mDeviceBandwidthSampler.stopSampling();
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                mTries++;

                if (downloadImage != null) {
                    downloadImage.cancel(true);
                    downloadImage = null;
                }
                downloadImage = new DownloadImage(mURL);
                downloadImage.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } else if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries >= 10) {

                jetConnectionListner.getCurrentBandWidth(ConnectionQuality.UNKNOWN, 0, 0);
                if (exception != null) {
                    jetConnectionListner.getErrorMsg(exception.toString(), ConnectionQuality.UNKNOWN);
                }
            }

            if (!mDeviceBandwidthSampler.isSampling()) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "onPostExecute: mDeviceBandwidthSampler.isSampling is false");
                }
            }
        }
    }

    public static boolean isNetworkAvailable(Context context) {

        @SuppressLint("WrongConstant")
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isConnected();
        }

        return false;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (isVpnEnabled())
            isConnected = false;
        if (getProxySettingDetails())
            isConnected = false;

        return isConnected;
    }

    private static boolean isVpnEnabled() {
        if (!BuildConfig.DEBUG) {
            List<String> networkList = new ArrayList<>();
            try {
                for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (networkInterface.isUp())
                        networkList.add(networkInterface.getName());
                }
            } catch (Exception ex) {

                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "" + ex.toString());
                }
            }
            if (networkList.contains("tun0")) {
                return true;
            } else if (networkList.contains("ppp")) {
                return true;
            }
        }
        return false;
    }

    public static boolean getProxySettingDetails() {
        String proxyAddress = "";
        String portValue = "";
        boolean proxySettingEnable = false;
        try {
            proxyAddress = System.getProperty("http.proxyHost");
            portValue = System.getProperty("http.proxyPort");
            if (proxyAddress == null || portValue == null || portValue.equals("0")) {

                proxySettingEnable = false;
            } else if (proxyAddress != null && !proxyAddress.isEmpty() && proxyAddress.length() > 10) {
                proxySettingEnable = true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, e.toString());
            }
        }
        return proxySettingEnable;
    }


    BroadcastReceiver netConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equalsIgnoreCase("internet_connection")) {
                boolean isNetConnected = intent.getBooleanExtra("isNetConnected", false);
                ConnectionType connectionType = Utils.checkNetWorkType(context);
                connectionChangeListner.connectionChangeType(isNetConnected, connectionType);
            }
        }
    };
}
