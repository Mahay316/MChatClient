package com.mahay.mchat.im;

/**
 *
 */
public interface ConnectionStatusListener {
    /**
     * called after connecting to server successfully
     */
    void onConnected();

    /**
     * called when trying to connect to server
     */
    void onConnecting();

    /**
     * called after
     */
    void onConnectFailed();
}
