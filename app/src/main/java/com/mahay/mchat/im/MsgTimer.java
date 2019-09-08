package com.mahay.mchat.im;

import com.mahay.mchat.im.inf.IMService;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.Timer;
import java.util.TimerTask;

public class MsgTimer extends Timer {
    private TimerTask task;
    // message to be resent
    private MessageProtobuf.Msg msg;
    private int currentAttemptCount;
    private IMService imService;

    public MsgTimer(IMService imService, MessageProtobuf.Msg msg) {
        this.imService = imService;
        this.msg = msg;
        currentAttemptCount = 0;
        task = new MsgTimerTask();
        schedule(task, imService.getResendInterval(), imService.getResendInterval());
    }

    public void cancel() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        super.cancel();
    }

    private class MsgTimerTask extends TimerTask {
        @Override
        public void run() {
            if (imService.isClosed()) {
                imService.getMsgTimeoutManager().remove(msg.getHead().getMsgId());
            }

            currentAttemptCount++;
            if (currentAttemptCount > imService.getResendAttemptCount()) {
                try {
                    MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
                    headBuilder.setMsgId(msg.getHead().getMsgId());
                    headBuilder.setTimeStamp(System.currentTimeMillis());
                    headBuilder.setMsgType(MsgConstant.MsgType.MSG_STATUS_REPORT);
                    headBuilder.setStatusReport(MsgConstant.MsgStatus.SEND_MSG_FAILURE);

                    MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
                    builder.setHead(headBuilder.build());

                    // report failure to application layer
                    imService.getOnServiceEventListener().OnMsgReceived(builder.build());
                } finally {
                    imService.getMsgTimeoutManager().remove(msg.getHead().getMsgId());
                    // the connection is not stable
                    // start reconnecting
                    imService.reconnect(false);
                }
            } else {
                imService.sendMsg(msg);

                System.out.println("resending message" +  msg);
            }
        }
    }
}
