package com.mahay.mchat.im;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class TCPIMServiceInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // inbound handlers
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
        pipeline.addLast(new ProtobufDecoder(MessageProtobuf.Msg.getDefaultInstance()));
        pipeline.addLast(new TCPMsgHandler());

        // outbound handlers
        pipeline.addLast(new LengthFieldPrepender(4));
        pipeline.addLast(new ProtobufEncoder());
    }
}
