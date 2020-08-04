package client;

import client.emojis.EmojiListController;
import client.model.User;
import client.service.userService;
import client.view.ConfigController;
import client.view.HomeController;
import client.view.LoginController;
import client.view.RegisterController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * App 做为窗口初始化及相关业务处理类
 * App 作为Reactor与各个view的中介类，提供各种相关功能，例如切换页面，发送消息
 */

public class App  extends Application {

    private static final String HomeViewRes= "/view/Home.fxml";
    private static final String LoginViewRes= "/view/Login.fxml";
    private static final String RegisterViewRes= "/view/Register.fxml";
    private static final String EmojiViewRes= "/view/EmojiList.fxml";
    private static final String  ConfigViewRes= "/view/Config.fxml";
    private static final AppSession session=new AppSession();

    public Stage primaryStage;
    public Stage emojiStage;
    private Reactor reactor;
    public HomeController homeController;
    public LoginController loginController;
    public RegisterController registerController;
    public App(){}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage=primaryStage;
        primaryStage.setTitle("聊天室");
        loginWindow();
        primaryStage.show();
        primaryStage.setOnCloseRequest((event)->{
            reactor.stop();
        });
        primaryStage.getIcons().add(new Image("/view/emoji.png"));
        reactor=new Reactor(this,session);
        (new Thread(reactor)).start();

    }

    /**
     * 通过Main类发送消息
     * @param msg
     */
    public void sendMsg(ByteBuffer msg){
        reactor.sendMsg(msg);
    }


    /**
     * 缓存用户信息到session中
     * @param u
     */
    public void  CacheUserInfo(User u){
        session.CacheUserInfo(u);
    }


    /**
     * 数据库操作
     * @param username
     * @return
     */
    public  boolean isExitsUserName(String username) {
        return  userService.isExitsUserName(username);
    }

    public  void saveUser(User user) {
        userService.saveUser(user);
    }

    public User getUserByUserName(String username){
        return  userService.getUserByUsername(username);
    }

    public void updateUser(User user){
        userService.updateUser(user);
    }

    /**
     * 切换窗口
     */

    public void HomeWindow(){
        try {
            primaryStage.setWidth(676);
            primaryStage.setHeight(605);
            homeController = (HomeController) replaceSceneContent(HomeViewRes);
            homeController.setapp(this,session);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void loginWindow(){
        try {
            primaryStage.setWidth(560);
            primaryStage.setHeight(350);
            loginController = (LoginController) replaceSceneContent(LoginViewRes);
            loginController.setapp(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void registerWindows(){
        try {
            primaryStage.setWidth(560);
            primaryStage.setHeight(350);
            registerController = (RegisterController) replaceSceneContent(RegisterViewRes);
            registerController.setapp(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void EmojiWindow(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(EmojiViewRes));
        try {
            Parent page =  loader.load();
            Scene scene = new Scene(page);
            EmojiListController controller =  loader.getController();
            controller.setapp(this);

            emojiStage = new Stage();
            emojiStage.initOwner(primaryStage);
            emojiStage.initStyle(StageStyle.UNDECORATED);
            emojiStage.setScene(scene);
            emojiStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean configWindow(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(ConfigViewRes));
        try {
            Parent page =  loader.load();
            Scene scene = new Scene(page);
            ConfigController controller =  loader.getController();
            controller.setMain(this,session);

            emojiStage = new Stage();
            emojiStage.initOwner(primaryStage);
            emojiStage.initStyle(StageStyle.UTILITY);
            emojiStage.setScene(scene);
            emojiStage.showAndWait();

            return controller.isChangeimg();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void emojiToText(String s){
        homeController.getTextarea().appendText(s);
    }

    public Initializable replaceSceneContent(String fxml) throws Exception {
        // Load the fxml file and create a new stage for the popup.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxml));
        Parent page = loader.load();
        Platform.runLater(() -> {
            primaryStage.setScene(new Scene(page));
            primaryStage.sizeToScene();
        });
        return (Initializable) loader.getController();
    }




}
