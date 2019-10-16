package com.jet.jetconnectiontester;

public interface JetConnectionListner {

    void getErrorMsg(String msg, ConnectionQuality connectionQuality);
    void getCurrentBandWidth(ConnectionQuality connectionQuality);
    void getCurrentBandWidth(ConnectionQuality connectionQuality, double bandWidth);
    void getCurrentBandWidth(ConnectionQuality connectionQuality, double bandWidth, long responseTime);
}
