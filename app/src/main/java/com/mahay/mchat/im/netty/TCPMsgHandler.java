package com.mahay.mchat.im.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mahay.mchat.im.MsgConstant;
import com.mahay.mchat.im.TCPIMService;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.UUID;

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
            // remove the msg from MsgTimeoutManager because it's been received successfully
            JSONObject jsonObj = JSON.parseObject(message.getHead().getExtend());
            imService.getMsgTimeoutManager().remove(jsonObj.getString("msgId"));

            System.out.println("server has received " + message);
        } else {
            // immediately send back a response message
            MessageProtobuf.Msg responeMsg = buildClientResponseMsg(message.getHead().getMsgId());
            if (responeMsg != null) {
                imService.sendMsg(responeMsg);
            }

            // dispatch the message to application layer
            imService.dispatchMsg(message);
        }
    }

    private MessageProtobuf.Msg buildClientResponseMsg(String msgId) {
        // build head of the message
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MsgConstant.MsgType.CLIENT_RESPONSE);
        headBuilder.setTimeStamp(System.currentTimeMillis());
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("msgId", msgId);
        headBuilder.setExtend(jsonObj.toString());

        // build message
        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        builder.setHead(headBuilder.build());

        return builder.build();
    }
}
