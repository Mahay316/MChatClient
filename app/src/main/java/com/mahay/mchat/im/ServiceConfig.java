package com.mahay.mchat.im;

public interface ServiceConfig {
    int getConnectTimeout();

    int getReconnectInterval();

    int getReconnectAttemptCount();

    int getResendInterval();

    int getResendAttemptCount();

    int getForegroundHeartbeatInterval();

    int getBackgroundHeartbeatInterval();
}
