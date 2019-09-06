package com.mahay.mchat.im;

public class MsgConstant {
    public static class MsgType {
        public static final int SERVER_RESPONSE = 0x00;
        public static final int CLIENT_RESPONSE = 0x01;
        public static final int HEARTBEAT_RESPONSE = 0x02;
        public static final int LOGIN_AUTH_RESPONSE = 0x03;
    }

    public static class LoginStatus {
        public static final int ACCEPT = 0x00;
        public static final int REFUSE = 0x01;
    }
}
