package com.mahay.mchat.im;

import com.mahay.mchat.im.protobuf.MessageProtobuf;

import java.util.UUID;
import java.util.Vector;

public class TestClient  {
    public static void main(String[] args) throws Exception {
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

        Thread.sleep(10000);
        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MsgConstant.MsgType.HEARTBEAT_RESPONSE);
        headBuilder.setFromId("12345");
        headBuilder.setToId("12346");
        headBuilder.setTimeStamp(System.currentTimeMillis());
        builder.setHead(headBuilder.build());
        builder.setBody("connection test");
        service.sendMsg(builder.build());
    }
}
