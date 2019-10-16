package com.example.jet_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.jet.jetconnectiontester.ConnectionChangeListner;
import com.jet.jetconnectiontester.ConnectionQuality;
import com.jet.jetconnectiontester.ConnectionType;
import com.jet.jetconnectiontester.JetConnectionClassManager;
import com.jet.jetconnectiontester.JetConnectionListner;
import com.jet.jetconnectiontester.NetworkChangeReceiver;

public class MainActivity extends AppCompatActivity implements JetConnectionListner, ConnectionChangeListner {

    private static final String TAG = "MainActivity";

    JetConnectionClassManager jetConnectionClassManager;

    TextView tv_net_status;
    TextView tv_response_time;
    TextView tv_bandwidth;
    TextView tv_network_type;
    TextView tv_network_connected_status;

    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: " + System.currentTimeMillis());


        tv_net_status = findViewById(R.id.tv_net_status);
        tv_bandwidth = findViewById(R.id.tv_bandwidth);
        tv_response_time = findViewById(R.id.tv_response_time);
        tv_network_connected_status = findViewById(R.id.tv_network_connected_status);
        tv_network_type = findViewById(R.id.tv_network_type);

        jetConnectionClassManager = new JetConnectionClassManager(this, this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        startTime = System.currentTimeMillis();

        Log.e(TAG, "onResume: " + startTime);
        jetConnectionClassManager.registerListner();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        jetConnectionClassManager.removeListner();
    }

    @Override
    public void getErrorMsg(String msg, ConnectionQuality connectionQuality) {

        Log.e(TAG," Connection Quality: "+ connectionQuality);
        Log.e(TAG, "Error msg: "+msg);

        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
    }

    @Override
    public void getCurrentBandWidth(ConnectionQuality connectionQuality) {


    }


    @Override
    public void getCurrentBandWidth(ConnectionQuality connectionQuality, final double bandWidth) {


    }

    @Override
    public void getCurrentBandWidth(ConnectionQuality connectionQuality, final double bandWidth, final long responseTime) {


        switch (connectionQuality) {

            case POOR:
                Log.e(TAG, "Connection Quality POOR: " + System.currentTimeMillis());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_net_status.setText("Connection Quality: POOR");
                        tv_response_time.setText("Response Time: " + responseTime+" ms");
                        tv_bandwidth.setText(" BandWidht: " +  bandWidth+" KBps");
                    }
                });


                break;

            case MODERATE:
                Log.e(TAG, "MODERATE: " + System.currentTimeMillis());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tv_net_status.setText("Connection Quality: MODERATE");
                        tv_response_time.setText("Response Time: " + responseTime+" ms");
                        tv_bandwidth.setText(" BandWidht: " + bandWidth+" KBps");
                    }
                });


                break;

            case GOOD:
                Log.e(TAG, "GOOD: " + System.currentTimeMillis());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_net_status.setText("Connection Quality: GOOD");
                        tv_response_time.setText("Response Time: " + responseTime+" ms");
                        tv_bandwidth.setText(" BandWidht: " +  bandWidth+" KBps");
                    }
                });

                break;

            case EXCELLENT:
                Log.e(TAG, "EXCELLENT: " + System.currentTimeMillis());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_net_status.setText("Connection Quality: EXCELLENT");
                        tv_response_time.setText("Response Time: " + responseTime+" ms");
                        tv_bandwidth.setText(" BandWidht: " +  bandWidth+" KBps");
                    }
                });

                break;

            case UNKNOWN:
                Log.e(TAG, "UNKNOWN: " + System.currentTimeMillis());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_net_status.setText("Connection Quality: UNKNOWN");
                        tv_response_time.setText("Response Time: " + responseTime+" ms");
                        tv_bandwidth.setText(" BandWidht: " +  bandWidth+" KBps");
                    }
                });

                break;

            case INTERNET_NOT_AVAILABLE:
                Log.e(TAG, "INTERNET_NOT_AVAILABLE");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_net_status.setText("INTERNET_NOT_AVAILABLE ");
                        tv_response_time.setText("Response time: " + responseTime+" ms");
                        tv_bandwidth.setText("");
                    }
                });

                break;
        }
    }

    @Override
    public void connectionChangeType(boolean isConnected, ConnectionType connectionType) {

        if(isConnected){
            tv_network_connected_status.setText("Network status: Connected");
        }else {
            tv_network_connected_status.setText("Network status: Disconnected");
        }

        switch (connectionType){

            case TYPE_MOBILE:

                Log.e(TAG, "Network Type: TYPE_MOBILE Connected: "+isConnected);
                tv_network_type.setText("NetWork Type: Mobile");
                break;

            case TYPE_WIFI:

                Log.e(TAG, "Network Type: TYPE_WIFI: "+isConnected);
                tv_network_type.setText("NetWork Type: WiFi");
                break;

            case TYPE_NO_NETWORK:

                Log.e(TAG, "Network Type: TYPE_NO_NETWORK: "+isConnected);
                tv_network_type.setText("NetWork Type: No NetWork");

                break;
        }
    }
}
