package com.mahay.mchat.im;

public class MsgConstant {
    public static class MsgType {
        // message acknowledgement from server
        public static final int SERVER_RESPONSE = 0x01;
        // message acknowledgement from client
        public static final int CLIENT_RESPONSE = 0x02;

        // heartbeat message from client
        public static final int HEARTBEAT_MESSAGE = 0x03;
        // heartbeat response from server
        // public static final int HEARTBEAT_RESPONSE = 0x04;

        // login request message from client
        public static final int LOGIN_AUTH_MESSAGE = 0x05;
        // login response from server
        public static final int LOGIN_AUTH_RESPONSE = 0x06;

        public static final int MSG_STATUS_REPORT = 0x07;

        // private message (user to user)
        public static final int PRIVATE_MSG = 0x08;
        // group message (user to users)
        public static final int GROUP_MSG = 0x09;
    }

    public static class LoginStatus {
        public static final int ACCEPT = 0x01;
        public static final int REFUSE = 0x02;
    }

    public static class MsgStatus {
        public static final int SEND_MSG_FAILURE = 0x01;
    }

    // private message content type
    public static class PrivateContentType {
        public static final int TEXT = 0x01;
    }

    // group message content type
    public static class GroupContentType {
        public static final int TEXT = 0x00;
    }
}
