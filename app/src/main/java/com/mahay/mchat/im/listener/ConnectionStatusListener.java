package com.mahay.mchat.im.listener;

/**
 * The listener interface for receiving connection events
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
     * called after failing to connect to server
     */
    void onConnectFailed();
}
