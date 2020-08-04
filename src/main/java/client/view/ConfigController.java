package client.view;

import client.App;
import client.AppSession;
import client.Main;
import client.model.User;
import client.util.DateUtil;
import client.util.propertiesUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ConfigController implements Initializable {

    @FXML
    private StackPane imgStack;
    @FXML
    private Button chooseBtn;

    private App app;
    private AppSession session;
    private boolean changeimg = false;
    private String uploadPath;

    public void setMain(App app, AppSession s) {
        this.app = app;
        this.session=s;
        //初始化
        drawimg();
    }

    @FXML
    private void chooseBtnClick() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser.ExtensionFilter extFilterJPEG = new FileChooser.ExtensionFilter("JPEG Files (*.jpeg)", "*.JPEG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG,extFilterPNG,extFilterJPEG);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(app.primaryStage);

        if (file != null) {
           //保存新图片
            String fileName=file.getName();
            String suffix =  fileName.substring(fileName.lastIndexOf("."));
            String fName = fileName.substring(0,fileName.lastIndexOf("."));

            File newFile= new File(uploadPath+"\\"+fName+"_"+ DateUtil.formatNow()+suffix);
            try {
                copyFile(file,newFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 更新用户信息
            User u =session.getCurrentUser();
            u.setIconurl("file:"+uploadPath+"\\"+fName+"_"+ DateUtil.formatNow()+suffix);
            app.updateUser(u);
           // 刷新显示
            drawimg();
            changeimg=true;
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadProperties();
    }

    public void loadProperties(){
        this.uploadPath= propertiesUtils.getString("config/filepath","uploadPath");
    }

    public void drawimg(){
        imgStack.setPadding( new Insets(0,0,0,0));
        ImageView imageView = new ImageView(session.getCurrentUser().getIconurl());
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imgStack.getChildren().add(imageView);
        imgStack.setAlignment(Pos.CENTER);
    }

    public  void copyFile(File resource, File target) throws Exception {
        // 输入流 --> 从一个目标读取数据
        // 输出流 --> 向一个目标写入数据

        long start = System.currentTimeMillis();

        // 文件输入流并进行缓冲
        FileInputStream inputStream = new FileInputStream(resource);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        // 文件输出流并进行缓冲
        FileOutputStream outputStream = new FileOutputStream(target);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

        // 缓冲数组
        // 大文件 可将 1024 * 2 改大一些，但是 并不是越大就越快
        byte[] bytes = new byte[1024 * 2];
        int len = 0;
        while ((len = inputStream.read(bytes)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
        }
        // 刷新输出缓冲流
        bufferedOutputStream.flush();
        //关闭流
        bufferedInputStream.close();
        bufferedOutputStream.close();
        inputStream.close();
        outputStream.close();

        long end = System.currentTimeMillis();

        System.out.println("耗时：" + (end - start) / 1000 + " s");

    }

    public boolean isChangeimg(){
        return changeimg;
    }
}
