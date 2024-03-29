package com.mahay.mchat.im.listener;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

/**
 * This is the interface by which IMService layer communicates with application layer
 * It's supposed to be implemented by application layer code
 */
public interface OnServiceEventListener {
    void OnMsgReceived(MessageProtobuf.Msg msg);
}
