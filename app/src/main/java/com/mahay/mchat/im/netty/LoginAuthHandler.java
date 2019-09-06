package com.mahay.mchat.im.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mahay.mchat.im.MsgConstant;
import com.mahay.mchat.im.TCPIMService;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Structure of Login Response Message
 * MsgType: MsgConstant.MsgType.LOGIN_AUTH_RESPONSE
 * Extend: {"status", MsgConstant.LoginStatus.STATUS}
 */
public class LoginAuthHandler extends ChannelInboundHandlerAdapter {
    private TCPIMService imService;

    public LoginAuthHandler(TCPIMService imService) {
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
        if (msgType == MsgConstant.MsgType.LOGIN_AUTH_RESPONSE) {

            JSONObject jsonObj = JSON.parseObject(message.getHead().getExtend());
            int status = jsonObj.getIntValue("status");

            if (status == MsgConstant.LoginStatus.ACCEPT) {
                // send a heartbeat packet immediately after successfully connecting
                MessageProtobuf.Msg heartbeat = imService.getHeartbeatMessage();
                if(heartbeat != null) {
                    imService.sendMsg(heartbeat);
                }

                // switch on HeartbeatHandler to manager the connectivity test
                imService.addHeartbeatHandler();
            } else {
                // login authentication failed, reconnect to server
                imService.reconnect(false);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
