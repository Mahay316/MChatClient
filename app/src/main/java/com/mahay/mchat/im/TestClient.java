package com.mahay.mchat.im;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.Vector;

public class TestClient  {
    public static void main(String[] args) {
        Vector<String> serverUrlList = new Vector<>();
        serverUrlList.add("127.0.0.1 8899");
        IMService service = IMServiceFactory.getIMService();
        service.init(serverUrlList, null, null, new OnServiceEventListener() {
            @Override
            public void OnMsgReceived(MessageProtobuf.Msg msg) {
                System.out.println(msg.toString());
            }
        });
        service.connect();
    }
}
