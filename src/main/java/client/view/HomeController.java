package client.view;

import client.App;
import client.AppSession;
import client.Main;
import client.emojis.EmojiDisplayer;
import client.model.ChatSession;
import client.model.Message;
import client.model.User;
import client.util.DateUtil;
import client.util.msgUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    public ListView chatlist;
    @FXML
    private ListView msglist;
    @FXML
    private TextArea textarea;
    @FXML
    private Label chatTipLab;
    @FXML
    private HBox userHBox;


    private boolean emojiFlag=true;
    private AppSession session;
    private App app;


    public TextArea getTextarea() {
        return textarea;
    }

    public void setapp(App app, AppSession s) {

        this.app = app;
        this.session=s;

        //初始化用户头像栏
        drawUserInfo();
        //初始化聊天窗口
        drawMsgView();;

        //初始化聊天会话窗口
        chatlist.setItems(session.getChatSession());
        //设置单选模式
        chatlist.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //监听选择事件，等待添加处理程序
        chatlist.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object o, Object t1) {
                ChatSession newChannel=(ChatSession)t1;
                if(session.checkCurrenChat(newChannel)){
                    session.setCurrenChat(newChannel);
                    drawMsgView();
                }
            }
        });

        //聊天窗口无法点击
        msglist.setFocusTraversable(false);
        chatlist.refresh();
        msglist.refresh();
    }

    public void drawUserInfo(){
        User o=session.getCurrentUser();
        userHBox.setAlignment(Pos.CENTER_LEFT);
        userHBox.setSpacing(10);
        userHBox.setPadding(new Insets(5));
        userHBox.setStyle("-fx-background-color: #f1eeec;");

        ImageView imageHead = new ImageView( o.getIconurl());
        imageHead.setFitHeight(50);
        imageHead.setFitWidth(50);
        imageHead.setOnMouseClicked(e->{
            //左键点击
            if(e.getButton().equals(MouseButton.PRIMARY)){
                //弹出设置界面
                boolean isChange=app.configWindow();
                if(isChange){
                    imageHead.setImage(new Image(session.getCurrentUser().getIconurl()));

                }
            }
        });

        VBox vbox= new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setSpacing(10);

        AnchorPane top= new AnchorPane();
        top.setPrefWidth(180);
        Label name = new Label();
        name.setText(o.getUsername());
        name.setPrefWidth(120);
        name.setStyle("  -fx-font-size: 14pt;" +
                "    -fx-font-family: System;" +
                "    -fx-text-fill:#46b9ee;");
        Label lastTime = new Label("");
        lastTime.setStyle("  -fx-font-size: 9pt;" +
                "    -fx-font-family: \"微软雅黑\";" +
                "    -fx-text-fill: #9b9d9e;"+
                "    -fx-alignment:center-right"
        );
        top.getChildren().addAll(name,lastTime);
        AnchorPane.setLeftAnchor(name,0.0);
        AnchorPane.setRightAnchor( lastTime,0.0);


        AnchorPane bootom= new AnchorPane();
        bootom.setPrefWidth(180);
        Label lastmsg = new Label("我想做个好人，我没得选择");
        lastmsg.setStyle("  -fx-font-size: 9pt;" +
                "    -fx-font-family: System;" +
                "    -fx-text-fill: #9b9d9e;");
        lastmsg.setPrefWidth(150);

        Label unRead = new Label("");
        unRead.setStyle("  -fx-font-size: 9pt;" +
                "    -fx-font-family: \"微软雅黑\";" +
                "    -fx-text-fill: #46b9ee;"+
                "    -fx-alignment:center-right"
        );
        bootom.getChildren().addAll(lastmsg,unRead);
        AnchorPane.setLeftAnchor(lastmsg,0.0);
        AnchorPane.setRightAnchor( unRead,0.0);

        vbox.getChildren().addAll(top,bootom);
        userHBox.getChildren().addAll(imageHead, vbox);
    }

    public void drawMsgView(){
       ChatSession channel = session.getCurrenChat();
        if(channel==null){
            return;
        }
        channel.setNewMsgNum(0);
        String Name=channel.getName();
        //切换聊天对象
        chatTipLab.setText(Name);
        msglist.setItems(session.getMsgSession(Name));
    }

    //表情包窗口加载
    @FXML
    public void onEmojiBtnClcked() {
        if(emojiFlag){
            emojiFlag=false;
            app.EmojiWindow();
            double x=app.primaryStage.getX() + 240;
            double y=app.primaryStage.getY() + 320;
            app.emojiStage.setX(x);
            app.emojiStage.setY(y);
        }else{
            emojiFlag=true;
            app.emojiStage.hide();
        }
    }

    @FXML
    private void SendBtnClick() {
         String msgStr = textarea.getText();
         if(msgStr.equals("")){
             return;
         }
         if(session.getCurrenChat()==null){
             return;
         }

         //清空输入框
         textarea.setText("");
         Message msg= msgUtils.getchatMsg(session.getCurrentUsername(),session.getCurrenChat(),msgStr);
         //添加己方发送的信息到msgMap中
         session.cacheToMessage(msg);
        //发送信息
        app.sendMsg(msgUtils.MessageToByteBuffer(msg));
    }

    @FXML
    private void textAreaEnterClick() {
        String msgStr = textarea.getText();
        if(msgStr.equals("")){
            return;
        }
        if(session.getCurrenChat()==null){
            return;
        }
        //清空输入框
        textarea.setText("");
        Message msg= msgUtils.getchatMsg(session.getCurrentUsername(),session.getCurrenChat(),msgStr);
        //添加己方发送的信息到msgMap中
        session.cacheToMessage(msg);
        //发送信息
        app.sendMsg(msgUtils.MessageToByteBuffer(msg));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //自定义用户会话实现
        chatlist.setCellFactory(new Callback<ListView<ChatSession>, ListCell<ChatSession>>() {
            @Override
            public ListCell<ChatSession> call(ListView<ChatSession> listView) {
                return new ChatCell();
            }
        });

        msglist.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> listView) {
                return new MessageCell();
            }
        });

    }
    public  class ChatCell extends ListCell<ChatSession> {
        //设置用户名label，方便在其他方法中进行操控，比如hover事件中做处理
        @Override
        protected void updateItem(ChatSession o, boolean b) {
            super.updateItem(o, b);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //判断是否有元素
                    if(b==false&&o!=null){
                        //用户状态和未处理信息后续扩展
                        HBox hbox = new HBox();
                        hbox.setPrefHeight(60);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setSpacing(10);

                        ImageView imageHead = new ImageView( o.getIconUrl());
                        imageHead.setFitHeight(40);
                        imageHead.setFitWidth(40);

                        VBox vbox= new VBox();
                        vbox.setAlignment(Pos.CENTER_LEFT);
                        vbox.setSpacing(10);

                        AnchorPane top= new AnchorPane();
                        top.setPrefWidth(180);
                        Label name = new Label();
                        name.setText(o.getName());
                        name.setPrefWidth(120);
                        name.setStyle("  -fx-font-size: 10pt;" +
                                "    -fx-font-family: \"微软雅黑\";" +
                                "    -fx-text-fill: black;");
                        Label lastTime = new Label(DateUtil.format(o.getLastChatTime()));
                        lastTime.setStyle("  -fx-font-size: 9pt;" +
                                "    -fx-font-family: \"微软雅黑\";" +
                                "    -fx-text-fill: #9b9d9e;"+
                                "    -fx-alignment:center-right"
                        );
                        top.getChildren().addAll(name,lastTime);
                        AnchorPane.setLeftAnchor(name,0.0);
                        AnchorPane.setRightAnchor( lastTime,0.0);


                        AnchorPane bootom= new AnchorPane();
                        bootom.setPrefWidth(180);
                        Label lastmsg = new Label(o.getLastChatMsg());
                        lastmsg.setStyle("  -fx-font-size: 9pt;" +
                                "    -fx-font-family: \"微软雅黑\";" +
                                "    -fx-text-fill: #9b9d9e;");
                        lastmsg.setPrefWidth(150);
                        String unReadNum=o.getNewMsgNum()+"";
                        if(unReadNum.equals("0")){
                            unReadNum="";
                        }
                        Label unRead = new Label(unReadNum);
                        unRead.setStyle("  -fx-font-size: 9pt;" +
                                "    -fx-font-family: \"微软雅黑\";" +
                                "    -fx-text-fill: #46b9ee;"+
                                "    -fx-alignment:center-right"
                        );
                        bootom.getChildren().addAll(lastmsg,unRead);
                        AnchorPane.setLeftAnchor(lastmsg,0.0);
                        AnchorPane.setRightAnchor( unRead,0.0);

                        vbox.getChildren().addAll(top,bootom);
                        hbox.getChildren().addAll(imageHead, vbox);
                        setGraphic(hbox);
                    }else if(b){
                        setText(null);
                        setGraphic(null);
                    }

                }
            });
        };

    }

    public  class MessageCell extends ListCell<Message> {
        @Override
        protected void updateItem(Message o, boolean b) {
            super.updateItem(o, b);
            Platform.runLater(()->{
                //判断是否有元素
                if(b==false&&o!=null){
                    HBox hbox = new HBox();
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setSpacing(10);

                    ImageView imageHead = new ImageView( o.getIconUrl());
                    imageHead.setFitHeight(30);
                    imageHead.setFitWidth(30);

             /* Label msglabel =new Label(o.getMsg());
                msglabel.setPadding(new Insets(5));
                msglabel.setStyle("  -fx-font-size: 10pt;" +
                        "    -fx-font-family: \"微软雅黑\";" );
                TextFlow txtContent = new TextFlow(msglabel);*/

                    TextFlow txtContent = new TextFlow(EmojiDisplayer.createEmojiAndTextNode(o.getMsg()));
                    txtContent.setMaxWidth(200);
                    txtContent.setPadding(new Insets(5,0,0,0));
                    if (o.getFrom().equals(session.getCurrentUsername())) {
                        txtContent.setStyle("-fx-background-color:#63e86a;");

                        hbox.setAlignment(Pos.CENTER_RIGHT);
                        hbox.getChildren().addAll(txtContent,imageHead);
                    }else{
                        txtContent.setStyle("-fx-background-color:white;");

                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.getChildren().addAll(imageHead,txtContent);
                    }
                    setGraphic(hbox);
                }else if(b){
                    setText(null);
                    setGraphic(null);
                }

            });
        }
    }


}
