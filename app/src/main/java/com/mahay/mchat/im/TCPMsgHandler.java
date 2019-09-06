package com.mahay.mchat.im;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TCPMsgHandler extends ChannelInboundHandlerAdapter {
    private TCPIMService imService;

    public TCPMsgHandler(TCPIMService imService) {
        this.imService = imService;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        MessageProtobuf.Msg message = null;
        if (msg instanceof MessageProtobuf.Msg) {
            message = (MessageProtobuf.Msg) msg;
        }
        if (message == null || message.getHead() == null) {
            return;
        }

        int msgType = message.getHead().getMsgType();
        if (msgType == MsgConstant.MsgType.SERVER_RESPONSE) {
            // remove the message successfully received by server
            // from message timeout manager

        } else {
            // immediately send back a response message

        }

        // dispatch the message to application layer
        imService.dispatchMsg(message);
    }
}
