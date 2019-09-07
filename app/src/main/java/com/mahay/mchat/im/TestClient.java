package com.mahay.mchat.im;

import com.alibaba.fastjson.JSONObject;
import com.mahay.mchat.im.inf.IMService;
import com.mahay.mchat.im.inf.OnServiceEventListener;
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

        Thread.sleep(5000);

        MessageProtobuf.Head.Builder headBuilder = MessageProtobuf.Head.newBuilder();
        headBuilder.setMsgId(UUID.randomUUID().toString());
        headBuilder.setMsgType(MsgConstant.MsgType.LOGIN_AUTH_MESSAGE);
        headBuilder.setFromId("12345");
        headBuilder.setTimeStamp(System.currentTimeMillis());
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("token", "token_12345");
        headBuilder.setExtend(jsonObj.toString());

        MessageProtobuf.Msg.Builder builder = MessageProtobuf.Msg.newBuilder();
        builder.setHead(headBuilder.build());
        service.sendMsg(builder.build());
    }
}
