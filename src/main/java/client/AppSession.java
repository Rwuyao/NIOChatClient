package client;

import client.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AppSession {
    private Map<String, ObservableList<Message>> msgMap=new HashMap<>();//存储所有用户的聊天记录
    private Map<String, ChatSession> chatmap=new HashMap<>();
    private ObservableList<ChatSession> chatSession= FXCollections.observableList(new LinkedList<ChatSession>());

    private String currentUsername;
    private User currentUser;
    private volatile ChatSession currenChat;

    public void increaseNotRead(String chatname, String msg, LocalDateTime time){
       ChatSession e= chatmap.get(chatname);
        e.increase();
        e.setLastChatMsg(msg);
        e.setLastChatTime(time);
    }
    public void refreshNotRead(String chatname, String msg, LocalDateTime time){
        ChatSession e= chatmap.get(chatname);
        e.refresh();
        e.setLastChatMsg(msg);
        e.setLastChatTime(time);
    }

    public boolean checkMsgIsFromCurrentChat(String from){
        if(from.equals(currenChat.getName())){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkCurrenChat(ChatSession newChat){
        if(currenChat==null ||!currenChat.equals(newChat)){
           return true;
        }
        else{
            return false;
        }
    }

    public ChatSession getCurrenChat() {
        //延迟加载
        if (currenChat == null) {
            synchronized(AppSession.class){
                if(currenChat == null){
                    currenChat = getFirstChatSession();
                }
            }
        }
        return currenChat;
    }

    public void setCurrenChat(ChatSession currenChat) {
        this.currenChat = currenChat;
    }

    //获取某个用户缓存的MsgList
    public ObservableList<Message> getMsgSession(String usrename){
        if(msgMap.containsKey(usrename)){
            return msgMap.get(usrename) ;
        }
        return null;
    }

    //获取某个用户信息
    public ChatSession getFirstChatSession(){
        if(chatSession.size()<=0){
            return null;
        }
        ChatSession channel  =  chatSession.get(0);
        return  channel;
    }

    //设置当前用户信息
    public void CacheUserInfo(User u){
        currentUsername=u.getUsername();
        currentUser=u;

    }

    //缓存来自对方的message信息
    public void cacheFromMessage(Message msg){
        String fromUser =msg.getFrom();
        List<Message> msgList =msgMap.get(fromUser);
        msgList.add(msg);
    }

    //缓存我们发送的message信息
    public void cacheToMessage(Message msg){
        String toUser =msg.getTo();
        List<Message> msgList =msgMap.get(toUser);
        msgList.add(msg);
    }

    //移除所有缓存
    public void clearSession(){
        msgMap.clear();
        chatmap.clear();
        chatSession.clear();
        currentUsername="";
        currentUser=null;
    }

    //批量缓存用户信息
    public void parseAndinitSession(List<User> userlist){

        //初始化
        chatSession.clear();
        chatmap.clear();
        msgMap.clear();
        //转换用户信息为chatsession
        List<ChatSession> chatlist= new LinkedList<>();
        //将群组加入到list中
        List<Group> grouplist= currentUser.getGroupList();
        //判断是否为Null
        if(grouplist!=null&&grouplist.size()>0){
            for (Group g: grouplist) {
                chatlist.add( ChatSession.builder().iconUrl(g.getIconurl()).name(g.getGroupname()).type(ChatType.Group).build());
            }
        }
        //将用户加入到list中
        if(userlist!=null&&userlist.size()>0) {
            for (User u : userlist) {
                chatlist.add(ChatSession.builder().iconUrl(u.getIconurl()).name(u.getUsername()).type(ChatType.Single).build());
            }
        }
        //批量缓存用户信息
        chatSession.addAll(chatlist);
        //批量缓存初始化用户对应的聊天记录缓存
        chatlist.stream().forEach(
                (u)->{
                    if(!msgMap.containsKey(u.getName())){
                        msgMap.put(u.getName(), FXCollections.observableList(new LinkedList<Message>()));
                    }
                }
        );

        chatlist.stream().forEach(
                (u)->{
                    if(!chatmap.containsKey(u.getName())){
                        chatmap.put(u.getName(),u);
                    }
                }
        );
    }

    //添加单个缓存
    public void addSession(ChatSession chat){
        //添加某个用户信息到缓存
        chatSession.add(chat);
        //初始化某个用户对应的聊天记录缓存
        if(!msgMap.containsKey(chat.getName())){
            msgMap.put(chat.getName(), FXCollections.observableList(new LinkedList<Message>()));
        }
        if(!chatmap.containsKey(chat.getName())){
            chatmap.put(chat.getName(),chat);
        }
    }

    //移除某个缓存
    public void removeSession(ChatSession chat){
        //移除某个用户缓存
        chatSession.remove(chat);
        //移除某个用户对应的聊天记录缓存
        msgMap.remove(chat.getName());
        chatmap.remove(chat.getName());
    }

    //解析用户为Chatsession并加入到Session中
    public void parseAndAddSession(User u){
        ChatSession chat= ChatSession.builder().iconUrl(u.getIconurl()).name(u.getUsername()).type(ChatType.Single).build();
        addSession(chat);
    }
    public void parseAndRemoveSession(User u){
        ChatSession chat= ChatSession.builder().iconUrl(u.getIconurl()).name(u.getUsername()).type(ChatType.Single).build();
        removeSession(chat);
    }





}
