package com.mahay.mchat.im;

public class MsgConstant {
    public static class MsgType {
        // message acknowledgement from server
        public static final int SERVER_RESPONSE = 0x00;
        // message acknowledgement from client
        public static final int CLIENT_RESPONSE = 0x01;

        // heartbeat message from client
        public static final int HEARTBEAT_MESSAGE = 0x02;
        // heartbeat response from server
        public static final int HEARTBEAT_RESPONSE = 0x03;

        // login request message from client
        public static final int LOGIN_AUTH_MESSAGE = 0x04;
        // login response from server
        public static final int LOGIN_AUTH_RESPONSE = 0x05;
    }

    public static class LoginStatus {
        public static final int ACCEPT = 0x00;
        public static final int REFUSE = 0x01;
    }
}
