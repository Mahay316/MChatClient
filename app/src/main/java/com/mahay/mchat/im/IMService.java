package com.mahay.mchat.im;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.Vector;

/**
 * Interface of IM service
 * Any class that provides IM (Instant Messaging) service should implement this
 */
public interface IMService {
    /**
     * initialize the IMService
     *
     * @param serverUrlList list of servers socket (combination of IP and port)
     * @param listener listener for receiving connect events
     */
    void init(Vector<String> serverUrlList, ConnectionStatusListener listener, ServiceConfig config);

    /**
     * connect to the server
     * connecting is considered as a special (first) reconnecting
     */
    void connect();

    /**
     * reconnect to the server
     * connecting is considered as a special (first) reconnecting
     *
     * @param first whether it's connecting or reconnecting
     */
    void reconnect(boolean first);

    /**
     * write message into channel
     *
     * @param msg message object to be sent
     */
    void sendMsg(MessageProtobuf.Msg msg);

    /**
     * write message into channel
     *
     * @param msg message object to be sent
     * @param isJoinTimeoutManager whether to put msg into timeout managing
     */
    void sendMsg(MessageProtobuf.Msg msg, boolean isJoinTimeoutManager);

    /**
     * close the IM service (release Thread pool, close channel and so on)
     */
    void close();

    /**
     * get the state of IM service
     *
     * @return whether the IM service is closed
     */
    boolean isClosed();
}
