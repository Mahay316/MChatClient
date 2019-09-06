package com.mahay.mchat.im;

import com.mahay.mchat.im.netty.HeartbeatHandler;
import com.mahay.mchat.im.netty.TCPIMServiceInitializer;
import com.mahay.mchat.im.netty.TCPMsgHandler;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class TCPIMService implements IMService {
    private static volatile TCPIMService instance;

    private boolean isClosed;

    private Vector<String> serverUrlList;
    private ConnectionStatusListener statusListener;
    private ServiceConfig config;
    private OnServiceEventListener eventListener;

    private Bootstrap bootstrap;
    private Channel channel;

    private int connectTimeout = ServiceConstant.DEFAULT_CONNECT_TIMEOUT;
    private int reconnectInterval = ServiceConstant.DEFAULT_RECONNECT_INTERVAL;
    private int reconnectAttemptCount = ServiceConstant.DEFAULT_RECONNECT_ATTEMPT_COUNT;
    private int resendInterval = ServiceConstant.DEFAULT_RESEND_INTERVAL;
    private int resendAttemptCount = ServiceConstant.DEFAULT_RESEND_ATTEMPT_COUNT;
    private int heartbeatInterval = ServiceConstant.DEFAULT_FOREGROUND_HEARTBEAT_INTERVAL;
    private int foregroundHeartbeatInterval = ServiceConstant.DEFAULT_FOREGROUND_HEARTBEAT_INTERVAL;
    private int backgroundHeartbeatInterval = ServiceConstant.DEFAULT_BACKGROUND_HEARTBEAT_INTERVAL;

    public static TCPIMService getInstance() {
        if (instance == null) {
            synchronized (TCPIMService.class) {
                if (instance == null) {
                    instance = new TCPIMService();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Vector<String> serverUrlList, ConnectionStatusListener statusListener,
                     ServiceConfig config, OnServiceEventListener eventListener) {
        this.serverUrlList = serverUrlList;
        this.statusListener = statusListener;
        this.config = config;
        this.eventListener = eventListener;
    }

    @Override
    public void connect() {
        reconnect(true);
    }

    @Override
    public void reconnect(boolean first) {

    }

    @Override
    public void sendMsg(MessageProtobuf.Msg msg) {

    }

    @Override
    public void sendMsg(MessageProtobuf.Msg msg, boolean isJoinTimeoutManager) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void dispatchMsg(MessageProtobuf.Msg msg) {
        eventListener.OnMsgReceived(msg);
    }

    /**
     * switch on the function which use Heartbeat packet to test connectivity
     */
    public void addHeartbeatHandler() {
        if (channel == null || !channel.isActive() || channel.pipeline() == null) {
            return;
        }

        ChannelPipeline pipeline = channel.pipeline();
        // re-attach the IdleStateHandler
        if (pipeline.get(IdleStateHandler.class.getSimpleName()) != null) {
            pipeline.remove(IdleStateHandler.class.getSimpleName());
        }
        // losing Heartbeat packet in succession for 3 times representing the connection is failed
        pipeline.addFirst(IdleStateHandler.class.getSimpleName(),
                new IdleStateHandler(getHeartbeatInterval() * 3, getHeartbeatInterval(), 0, TimeUnit.MILLISECONDS));

        // re-attach the HeartbeatHandler
        if (pipeline.get(HeartbeatHandler.class.getSimpleName()) != null) {
            pipeline.remove(HeartbeatHandler.class.getSimpleName());
        }
        if (pipeline.get(TCPMsgHandler.class.getSimpleName()) != null) {
            pipeline.addBefore(TCPMsgHandler.class.getSimpleName(), HeartbeatHandler.class.getSimpleName(),
                    new HeartbeatHandler(this));
        }
    }

    private void initBootstrap() {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new TCPIMServiceInitializer(this));
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout());
    }

    public int getConnectTimeout() {
        if (config != null && config.getConnectTimeout() > 0) {
            return config.getConnectTimeout();
        }
        return connectTimeout;
    }

    public int getReconnectInterval() {
        if (config != null && config.getReconnectInterval() >= 0) {
            return config.getReconnectInterval();
        }
        return reconnectInterval;
    }

    public int getReconnectAttemptCount() {
        if (config != null && config.getReconnectAttemptCount() > 0) {
            return config.getReconnectAttemptCount();
        }
        return reconnectAttemptCount;
    }

    public int getResendInterval() {
        if (config != null && config.getResendInterval() >= 0) {
            return config.getResendInterval();
        }
        return resendInterval;
    }

    public int getResendAttemptCount() {
        if (config != null && config.getResendAttemptCount() > 0) {
            return config.getResendAttemptCount();
        }
        return resendAttemptCount;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public int getForegroundHeartbeatInterval() {
        if (config != null && config.getForegroundHeartbeatInterval() > 0) {
            return config.getForegroundHeartbeatInterval();
        }
        return foregroundHeartbeatInterval;
    }

    public int getBackgroundHeartbeatInterval() {
        if (config != null && config.getBackgroundHeartbeatInterval() > 0) {
            return config.getBackgroundHeartbeatInterval();
        }
        return backgroundHeartbeatInterval;
    }
}
