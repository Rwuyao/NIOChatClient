package client;

import client.model.Message;
import client.model.Result;
import client.model.User;
import client.util.msgUtils;
import client.util.propertiesUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import javafx.application.Platform;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;

public class Reactor implements  Runnable{

    private volatile  boolean isShutdown=false;
    private  int PORT ;
    private  String HOSTNAME ;
    private int TIMEOUT ;
    private int BufferSize;
    private AppSession session;
    private SocketChannel sc = null;
    private App app;
    private HertBeator hertBeator;
    private ByteBuffer buffer;
    private Selector selector;


    public Reactor(App app, AppSession session) throws IOException {
        loadProperties();
        selector=Selector.open();
        buffer=ByteBuffer.allocate(BufferSize);
        this.app=app;
        this.session=session;
        connect();
    }

    public void loadProperties(){
        this.PORT= propertiesUtils.getInt("config/reactor","PORT");
        this.HOSTNAME= propertiesUtils.getString("config/reactor","HOSTNAME");
        this.TIMEOUT = propertiesUtils.getInt("config/reactor","TIMEOUT");
        this.BufferSize= propertiesUtils.getInt("config/reactor","BufferSize");
    }

    public void connect()  {
        try {
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_CONNECT);
            sc.connect(new InetSocketAddress(HOSTNAME, PORT));
        } catch (IOException e) {
            System.out.println("无法连接到服务器");
        }
    }

    public void run() {
        selector();
    }


    public void selector() {

        while(!isShutdown&&!Thread.interrupted()) {
            try{
                if (selector.select(TIMEOUT) == 0) {
                    continue;
                }
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    handle(key);
                    iter.remove();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

    }

    public void handle(SelectionKey key) throws InterruptedException {
        if (key.isValid()) {
            if(key.isReadable()){
                handleRead(key);
            }
            else if(key.isConnectable()){
                if(!handleConnect(key)){
                    reconnect();
                } ;
            }
        } else {
            key.cancel();
        }
    }

    public boolean handleConnect(SelectionKey key) throws InterruptedException  {
        SocketChannel client = (SocketChannel) key.channel();
        if (client.isConnectionPending()) {
            try {
                client.finishConnect();
                // 注册可读事件
                client.register(selector, SelectionKey.OP_READ);
                // 如果重连成功，将连接失败的提示清除
                Platform.runLater(()->{
                    app.loginController.tipLabel3.setText("");
                });
                System.out.println("连接成功！");
            } catch (Exception e) {
                Platform.runLater(()->{
                    app.loginController.tipLabel3.setText("无法连接到服务器");
                });
                System.out.println("连接失败！");
                Thread.sleep(1000);
                return false;
            }
        }
        return true;
    }


    public void handleRead(SelectionKey key)  {
        //更新心跳连接的操作时间
        if(hertBeator!=null)
            hertBeator.updateActionTime();

        SocketChannel sc = (SocketChannel)key.channel();
        //将数据写入到缓存区域
        ByteArrayOutputStream bArray = new ByteArrayOutputStream();

        long bytesRead=0;
        try{
            //开始循环读取数据
            bytesRead= sc.read(buffer);
            while(bytesRead>0){
                //调整
                buffer.flip();
                //获取发送过来的byte数组
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                bArray.write(bytes);
                //清空
                buffer.clear();
                bytesRead = sc.read(buffer);
            }
        }catch(SocketException e){
            System.out.println("连接中断");
            //尝试重连
            reconnect();
            return ;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将字符串转换为Message
        Message msg=null;
        try{
            msg= JSONObject.parseObject(bArray.toString(), Message.class);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        handleMsg(msg,sc);
        //返回-1代表中断连接
        if(bytesRead == -1){
            try {
                sc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMsg(Message msg, SocketChannel sc) {

        switch (msg.getType()){
            case Login:{
                Result result  =msg.getResult();
                int code =result.getCode();
                if(code==200){
                    //第一次登录会附带用户信息
                    JSONArray jsonArray= (JSONArray)result.getData();
                    List<User> users= JSON.parseArray(jsonArray.toJSONString(), User.class);
                    //使用刚获取的用户信息，初始化session
                    session.parseAndinitSession(users);
                    //跳转到主界面
                    Platform.runLater(()->{
                        app.HomeWindow();
                    });
                    // 开启心跳连接
                    if(hertBeator==null){
                        hertBeator=new HertBeator(this,session.getCurrentUsername());
                        (new Thread(hertBeator)).start();
                    }
                }else{
                    //提示登录失败
                    Platform.runLater(()->{
                        app.loginController.tipLabel3.setText((result.getMsg()));
                    });
                }
                break;
            }
            case Logout:{
                Result result  =msg.getResult();
                int code =result.getCode();
                if(code==200){
                    //移除缓存信息
                    session.clearSession();
                    //跳转到登录界面
                    app.loginWindow();
                }else{
                    //提示退出失败
                }
                break;
            }
            case AUserLogin:{
                Result result=msg.getResult();
                JSONObject jsonObject= (JSONObject) result.getData();
                User user= JSON.parseObject(jsonObject.toJSONString(), User.class);
                //当有某个用户登录时，将其加入到session中
                session.parseAndAddSession(user);
                break;
            }
            case AUserLogout: {
                Result result = msg.getResult();
                JSONObject jsonObject= (JSONObject) result.getData();
                User user= JSON.parseObject(jsonObject.toJSONString(), User.class);
                //当有某个用户下线时，将session中的信息删除
                session.parseAndRemoveSession(user);
                break;
            }
            case SingleMsg: {
                //判断当前接收到的消息是否与当前正在聊天的人相同
                if(session.checkMsgIsFromCurrentChat(msg.getFrom())){
                    session.refreshNotRead(msg.getFrom(),msg.getMsg(),msg.getTime());
                }else{
                    session.increaseNotRead(msg.getFrom(),msg.getMsg(),msg.getTime());
                }
                app.homeController.chatlist.refresh();
                //单对单聊天时，收信人是我们自己，所以我们的缓存目标是对方的频道，From
                session.cacheFromMessage(msg);
                break;
            }
            case GroupMsg:
            {
                //判断当前接收到的消息是否与当前正在聊天的人相同
                if(session.checkMsgIsFromCurrentChat(msg.getTo())){
                    session.refreshNotRead(msg.getTo(),msg.getMsg(),msg.getTime());
                }else{
                    session.increaseNotRead(msg.getTo(),msg.getMsg(),msg.getTime());
                }
                app.homeController.chatlist.refresh();
                //群发的时候，收信人是所有人，所以我们的缓存目标应该群组频道，To
                session.cacheToMessage(msg);
                break;
            }
            case HeartBeat:{
                break;
            }
        }
    }


    public boolean hertBeatorConnect() throws InterruptedException {
        //重连
        reconnect();
        //判断是否重连成功，不成功则休眠
       while (!sc.isConnected()) {
           Thread.sleep(500);
       }
        //发送登录信息
        app.sendMsg(msgUtils.getloginMsg(session.getCurrentUsername()));
        return true;
    }

    public void reconnect() {
        //先关闭连接
        try{
            if(sc!=null){
                sc.close();
            }
        }catch(IOException e1){
            e1.printStackTrace();
        }
        //重连
        if(!sc.isConnected()) {
            try {
                sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_CONNECT);
                sc.connect(new InetSocketAddress(HOSTNAME, PORT));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendMsg(ByteBuffer msg) {
        try {
            if((!sc.isConnected())){
                return false;
            }

            sc.write(msg);
            //更新心跳连接的操作时间
            if(hertBeator!=null)
            hertBeator.updateActionTime();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void stop(){
        isShutdown=true;
        if(hertBeator!=null) hertBeator.stop();
    }







}
