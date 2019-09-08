package com.mahay.mchat.im;

import com.mahay.mchat.im.inf.IMService;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.StringUtil;

public class MsgTimeoutManager {
    private IMService imService;
    // the amount of messages being managed
    private int msgCount;
    private ConcurrentHashMap<String, MsgTimer> msgTimeoutMap;

    public MsgTimeoutManager(IMService imService) {
        this.imService = imService;
        msgTimeoutMap = new ConcurrentHashMap<>();
        msgCount = 0;
    }

    public void add(MessageProtobuf.Msg msg) {
        if (msg == null || StringUtil.isNullOrEmpty(msg.getHead().getMsgId())) {
            return;
        }

        int msgType = msg.getHead().getMsgType();
        // if the message is heartbeat message or client response message, do not manage
        if (msgType == MsgConstant.MsgType.HEARTBEAT_MESSAGE || msgType == MsgConstant.MsgType.CLIENT_RESPONSE) {
            return;
        }

        String msgId = msg.getHead().getMsgId();
        if (!msgTimeoutMap.containsKey(msgId)) {
            MsgTimer timer = new MsgTimer(imService, msg);
            msgTimeoutMap.put(msgId, timer);
            msgCount++;

            System.out.println("add a message to TimeoutManager " + msg);
        }
    }

    public void remove(String msgId) {
        if (StringUtil.isNullOrEmpty(msgId)) {
            return;
        }

        MsgTimer timer = msgTimeoutMap.remove(msgId);
        if (timer != null) {
            timer.cancel();

            System.out.println("remove a message from Timeout Manager " + msgId);
        }
    }

    public int getMsgCount() {
        return msgCount;
    }
}
