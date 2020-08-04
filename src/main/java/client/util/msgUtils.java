package client.util;


import client.model.ChatSession;
import client.model.ChatType;
import client.model.Message;
import client.model.MessageType;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class msgUtils {
    private static final Charset charset = Charset.forName("UTF-8");


    public static ByteBuffer MessageToByteBuffer(Message msg){
        return  charset.encode(
                msg.toString()
        );
    };

    public  static Message getchatMsg(String from, ChatSession to, String msg){
       MessageType type;

       if(to.getType().equals(ChatType.Group)){
            type= MessageType.GroupMsg;
       }else{
            type= MessageType.SingleMsg;
       }
       return
            Message
                .builder()
                .from(from)
                .to(to.getName())
                .msg(msg)
                .time(LocalDateTime.now())
                .type(type)
                .build();
    }

    public  static ByteBuffer getloginMsg(String from){
        return  charset.encode(
                Message
                        .builder()
                        .from(from)
                        .to("server")
                        .time(LocalDateTime.now())
                        .type(MessageType.Login)
                        .msg("").build().toString()
        );
    }


    public static ByteBuffer getHertBeatMsg(String from){
        return  charset.encode(
                Message
                        .builder()
                        .iconUrl("")
                        .from(from)
                        .to("server")
                        .type(MessageType.HeartBeat)
                        .build().toString()
        );
    }
}
