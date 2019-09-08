package com.mahay.mchat.im;

/**
 * Constant of IM service's default configuration
 */
public class ServiceConstant {
    // connect attempt timeout
    public static final int DEFAULT_CONNECT_TIMEOUT = 3 * 1000;
    // interval between two reconnect attempts
    public static final int DEFAULT_RECONNECT_INTERVAL = 1000;
    // times try to reconnect before reporting the failure
    public static final int DEFAULT_RECONNECT_ATTEMPT_COUNT = 5;
    // interval between two message resend attempts
    public static final int DEFAULT_RESEND_INTERVAL = 5 * 1000;
    // times try to resend a message before reporting the failure
    public static final int DEFAULT_RESEND_ATTEMPT_COUNT = 5;
    // interval between two heartbeat packets when app's in the foreground
    public static final int DEFAULT_FOREGROUND_HEARTBEAT_INTERVAL = 3 * 1000;
    // interval between two heartbeat packets when app's in the background
    public static final int DEFAULT_BACKGROUND_HEARTBEAT_INTERVAL = 30 * 1000;

    // connect state code
    public static final int CONNECT_STATE_FAILURE = -1;
    public static final int CONNECT_STATE_SUCCESS = 1;

}
