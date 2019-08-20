package com.mahay.mchat.im;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.Vector;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TCPIMService implements IMService {
    private static volatile TCPIMService instance;

    private boolean isClosed;

    private Vector<String> serverUrlList;
    private ConnectionStatusListener listener;
    private ServiceConfig config;

    private Bootstrap bootstrap;

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
    public void init(Vector<String> serverUrlList, ConnectionStatusListener listener, ServiceConfig config) {
        this.serverUrlList = serverUrlList;
        this.listener = listener;
        this.config = config;
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

    private void initBootstrap() {
        EventLoopGroup loopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new TCPIMServiceInitializer());
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
