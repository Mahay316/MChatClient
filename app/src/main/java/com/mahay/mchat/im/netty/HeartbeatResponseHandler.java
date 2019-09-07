package com.mahay.mchat.im.netty;

import com.mahay.mchat.im.MsgConstant;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HeartbeatResponseHandler extends ChannelInboundHandlerAdapter {
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
        if (msgType == MsgConstant.MsgType.HEARTBEAT_RESPONSE) {
            // TODO: complete the action when receiving a heartbeat response
        } else {
            // if the message is not a Heartbeat Response,
            // then deliver the message to the next handler
            ctx.fireChannelRead(msg);
        }
    }
}
