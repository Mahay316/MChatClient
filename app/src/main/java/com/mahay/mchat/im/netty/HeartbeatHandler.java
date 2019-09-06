package com.mahay.mchat.im.netty;

import com.mahay.mchat.im.TCPIMService;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter {
    private TCPIMService imService;

    public HeartbeatHandler(TCPIMService imService) {
        this.imService = imService;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            switch (state) {
                case READER_IDLE:
                    // reader idle is triggered
                    // meaning that the connection is somehow failed
                    // then make an attempt to reconnect
                    imService.reconnect(false);
                    break;
                case WRITER_IDLE:
                    // writer idle is triggered
                    // send a Heartbeat to test connectivity
                    // TODO: use thread pool to send a Heartbeat package
                    break;
            }
        }
    }

    private class SendHeartbeatTask implements Runnable {
        @Override
        public void run() {
            
        }
    }
}