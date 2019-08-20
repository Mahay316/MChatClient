package com.mahay.mchat.im;

import java.util.Vector;

public interface IMService {
    /**
     *
     */
    void init(Vector<String> serverUrlList, ConnectionStatusListener listener);

    void connect();

    void reconnect();

    void sendMsg();

    void close();

    boolean isClosed();
}
