package com.mahay.mchat.im.netty;

import com.mahay.mchat.im.TCPIMService;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

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
                    System.out.println("reader idle");
                    imService.reconnect(false);
                    break;
                case WRITER_IDLE:
                    // writer idle is triggered
                    // send a Heartbeat to test connectivity
                    System.out.println("writer idle");
                    imService.getExecutorFactory().executeWorkerTask(new SendHeartbeatTask(ctx));
                    break;
            }
        }
    }

    private class SendHeartbeatTask implements Runnable {
        private ChannelHandlerContext ctx;

        public SendHeartbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            MessageProtobuf.Msg heartbeat = imService.getHeartbeatMessage();
            if (heartbeat != null && ctx.channel().isActive()) {
                imService.sendMsg(heartbeat);
            }
        }
    }
}
