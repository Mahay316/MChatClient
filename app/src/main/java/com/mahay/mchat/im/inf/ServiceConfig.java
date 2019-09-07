package com.mahay.mchat.im.inf;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

public interface ServiceConfig {
    int getConnectTimeout();

    int getReconnectInterval();

    int getReconnectAttemptCount();

    int getResendInterval();

    int getResendAttemptCount();

    int getForegroundHeartbeatInterval();

    int getBackgroundHeartbeatInterval();

    MessageProtobuf.Msg getHeartbeatMessage();
}
