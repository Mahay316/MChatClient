package com.mahay.mchat.im;

import com.mahay.mchat.im.netty.HeartbeatHandler;
import com.mahay.mchat.im.netty.TCPIMServiceInitializer;
import com.mahay.mchat.im.netty.TCPMsgHandler;
import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.UUID;
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
import io.netty.util.internal.StringUtil;

public class TCPIMService implements IMService {
    private static volatile TCPIMService instance;

    // indicate whether the TCPIMService is closed
    private boolean isClosed;
    // indicate whether the TCPIMService is during the process of connecting
    // prevent the connect operation being executed repeatedly
    private boolean isConnecting;

    private Vector<String> serverUrlList;
    private ConnectionStatusListener statusListener;
    private ServiceConfig config;
    private OnServiceEventListener eventListener;

    private Bootstrap bootstrap;
    private Channel channel;
    private ExecutorFactory executorFactory;

    private int connectTimeout = ServiceConstant.DEFAULT_CONNECT_TIMEOUT;
    private int reconnectInterval = ServiceConstant.DEFAULT_RECONNECT_INTERVAL;
    private int reconnectAttemptCount = ServiceConstant.DEFAULT_RECONNECT_ATTEMPT_COUNT;
    private int resendInterval = ServiceConstant.DEFAULT_RESEND_INTERVAL;
    private int resendAttemptCount = ServiceConstant.DEFAULT_RESEND_ATTEMPT_COUNT;
    private int heartbeatInterval = ServiceConstant.DEFAULT_FOREGROUND_HEARTBEAT_INTERVAL;
    private int foregroundHeartbeatInterval = ServiceConstant.DEFAULT_FOREGROUND_HEARTBEAT_INTERVAL;
    private int backgroundHeartbeatInterval = ServiceConstant.DEFAULT_BACKGROUND_HEARTBEAT_INTERVAL;
    private MessageProtobuf.Msg defaultHeartbeatMessage;

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
        // reopen TCPIMService
        isClosed = false;
        executorFactory = new ExecutorFactory();
        constructHeartbeatMessage();
    }

    @Override
    public void connect() {
        reconnect(true);
    }

    @Override
    public void reconnect(boolean first) {
        if (!isClosed && !isConnecting) {
            // only the first caller can start reconnecting
            synchronized (this) {
                if (!isClosed && !isConnecting) {
                    isConnecting = true;

                    closeChannel();
                    executorFactory.executeBossTask(new ReconnectTask(first));
                }
            }
        }
    }

    @Override
    public void sendMsg(MessageProtobuf.Msg msg) {
        sendMsg(msg, false);
    }

    @Override
    public void sendMsg(MessageProtobuf.Msg msg, boolean isJoinTimeoutManager) {
        if (msg == null || msg.getHead() == null) {
            System.err.println("failed to send msg because it's not complete");
            return;
        }

        if (!StringUtil.isNullOrEmpty(msg.getHead().getMsgId()) && isJoinTimeoutManager) {
            joinTimeoutManager();
        }

        if (channel == null) {
            System.err.println("failed to send msg because connection hasn't successfully established yet");
            return;
        }
        channel.writeAndFlush(msg);
    }

    @Override
    public void close() {
        // TODO: do some work before shutting down
        isClosed = true;
    }

    @Override
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void dispatchMsg(MessageProtobuf.Msg msg) {
        if (eventListener != null) {
            eventListener.OnMsgReceived(msg);
        }
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

    public MessageProtobuf.Msg getHeartbeatMessage() {
        if (config != null && config.getHeartbeatMessage() != null) {
            return config.getHeartbeatMessage();
        }

        return defaultHeartbeatMessage;
    }

    public ExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    /**
     * construct default heartbeat message
     */
    private void constructHeartbeatMessage() {
        // build head
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MsgConstant.MsgType.HEARTBEAT_MESSAGE);
        headBuilder.setTimeStamp(System.currentTimeMillis());

        // build message
        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        builder.setHead(headBuilder.build());

        defaultHeartbeatMessage = builder.build();
    }

    private void closeChannel() {
        try {
            if (channel != null) {
                // this is an asynchronous method
                channel.close();
                channel.eventLoop().shutdownGracefully();
            }
        } finally {
            channel = null;
        }
    }

    private void joinTimeoutManager() {
        // TODO: create a Timeout Manager
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

    /**
     * Runnable class representing reconnecting tasks
     */
    private class ReconnectTask implements Runnable {
        private boolean isFirst;

        public ReconnectTask(boolean isFirst) {
            this.isFirst = isFirst;
        }

        @Override
        public void run() {
            // TODO: add connect state callback
            while (!isClosed) {
                int status = reconnect();
                if (status == ServiceConstant.CONNECT_STATE_SUCCESS) {
                    break;
                } else {
                    try {
                        Thread.sleep(getReconnectInterval());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * reconnect method, taking care of the initialization and shut-down of Bootstrap
         * the part of code that's responsible for connecting operation locates in method connectServer()
         *
         * @return the result of attempted connection
         */
        private int reconnect() {
            if (!isClosed) {
                try {
                    if (bootstrap != null) {
                        bootstrap.config().group().shutdownGracefully();
                    }
                } finally {
                    bootstrap = null;
                }
                initBootstrap();

                return connectServer();
            }
            return ServiceConstant.CONNECT_STATE_FAILURE;
        }

        private int connectServer() {
            if (serverUrlList == null || serverUrlList.size() == 0) {
                return ServiceConstant.CONNECT_STATE_FAILURE;
            }

            for (int i = 0; (!isClosed && i < serverUrlList.size()); i++) {
                // valid format of server url: ip[space]port, e.g. 172.0.0.1 8860
                String serverUrl = serverUrlList.get(i);
                // if server url is invalid, then try next
                if (StringUtil.isNullOrEmpty(serverUrl)) {
                    continue;
                }

                String[] address = serverUrl.split(" ");
                for (int j = 1; j <= getReconnectAttemptCount(); j++) {
                    if (isClosed) {
                        return ServiceConstant.CONNECT_STATE_FAILURE;
                    }

                    String serverIp = address[0];
                    int serverPort = Integer.parseInt(address[1]);

                    // try to connect to server
                    try {
                        channel = bootstrap.connect(serverIp, serverPort).sync().channel();
                    } catch (InterruptedException e) {
                        System.err.println(j + "th attempted connection to server " + serverUrl + " failed");
                        e.printStackTrace();
                    }

                    // if channel is not null, we consider the connection successful
                    if (channel != null) {
                        return ServiceConstant.CONNECT_STATE_SUCCESS;
                    } else {
                        // sleep for (j * getReconnectInterval()) ms and try again
                        try {
                            Thread.sleep(j * getReconnectInterval());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            return ServiceConstant.CONNECT_STATE_FAILURE;
        }
    }
}
