package com.mahay.mchat.im.inf;

import com.mahay.mchat.im.ExecutorFactory;
import com.mahay.mchat.im.MsgTimeoutManager;
import com.mahay.mchat.im.listener.ConnectionStatusListener;
import com.mahay.mchat.im.listener.OnServiceEventListener;
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
     * @param serverUrlList  list of servers socket (combination of IP and port)
     * @param statusListener listener for receiving connect events
     * @param config         user's customized IMService configure
     * @param eventListener  interface by which IMService communicates with app
     */
    void init(Vector<String> serverUrlList, ConnectionStatusListener statusListener,
              ServiceConfig config, OnServiceEventListener eventListener);

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
     * @param msg                  message object to be sent
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

    /**
     * dispatch the message received to application layer
     */
    void dispatchMsg(MessageProtobuf.Msg msg);

    /**
     * return customized or default Heartbeat Message
     */
    MessageProtobuf.Msg getHeartbeatMessage();

    /**
     * switch on the function which use Heartbeat packet to test connectivity
     */
    void addHeartbeatHandler();

    /**
     * return the interval between two resend attempts
     */
    int getResendInterval();

    /**
     * return count of resend attempts before report failure
     */
    int getResendAttemptCount();

    /**
     * return the IMService's MsgTimeoutManager
     */
    MsgTimeoutManager getMsgTimeoutManager();

    /**
     * return the IMService's ExecutorFactory
     */
    ExecutorFactory getExecutorFactory();

    /**
     *
     * return the IMService's OnServiceEventListener
     */
    OnServiceEventListener getOnServiceEventListener();
}
