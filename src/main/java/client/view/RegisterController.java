package client.view;

import client.App;
import client.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {



    @FXML
    private TextField UsernameFiled;
    @FXML
    private TextField PasswordField;
    @FXML
    private Label tipLabel1;
    @FXML
    private Label tipLabel2;
    @FXML
    private Label tipLabel3;

    private App app;

    public void setapp(App app) {
        this.app = app;
    }


    @FXML
    private void RegisterBtnClick() {
        //清除提示框
        ClearTips();
        //检查输入框是否符合要求
        Check();
        //清除输入框
        Clear();
    }

    @FXML
    private void RegisterKeyUp(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER){
            //清除提示框
            ClearTips();
            //检查输入框是否符合要求
            Check();
            //清除输入框
            Clear();
        }
    }

    @FXML
    private void LoginLabelClick() {
        app.loginWindow();
    }

    private boolean Check(){
        final String userName = UsernameFiled.getText();
        final String password = PasswordField.getText();
        //检查是否为空
        if(userName.equals("")){
            tipLabel1.setText("账号不能为空");
            return false;
        }
        if(password.equals("")){
            tipLabel2.setText("密码不能为空");
            return false;
        }
        //检查该用户是否已经被注册
        if(app.isExitsUserName(userName)){
            tipLabel1.setText("该账号已存在");
            return false;
        };
        //保存该用户
        User u = new User(userName,password);
        u.setCreatetime(new Date());
        app.saveUser(u);
        return true;
    }


    private void Clear(){
        UsernameFiled.setText("");
        PasswordField.setText("");
    }

    private void ClearTips(){
        tipLabel1.setText("");
        tipLabel2.setText("");
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
