package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class UITest extends Application {
    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("聊天室");
        stage.setWidth(676);
        stage.setHeight(605);
        HBox hbox= new HBox();
        ImageView imageView = new ImageView("/view/icon.jpeg");
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        hbox.getChildren().addAll(imageView);
        Scene scene = new Scene(hbox);

        stage.setScene(scene);
        stage.show();

 /*       Stage s1 = new Stage();
        s1.setTitle("s1");
        //纯白背景和平台装饰
        s1.initStyle(StageStyle.DECORATED);
        s1.show();

        Stage s2 = new Stage();
        s2.setTitle("s2");
        //透明背景且没有装饰
        s2.initStyle(StageStyle.TRANSPARENT);
        s2.show();

        Stage s3 = new Stage();
        s3.setTitle("s3");

        //纯白背景，无装饰。
        s3.initStyle(StageStyle.UNDECORATED);
        s3.show();

        Stage s4 = new Stage();
        s4.setTitle("s4");

        //纯白背景和最少平台装饰
        s4.initStyle(StageStyle.UNIFIED);
        s4.show();

        Stage s5 = new Stage();
        s5.setTitle("s5");
        //一个统一标准的舞台
        s5.initStyle(StageStyle.UTILITY);
        s5.show();*/

        // 终止JavaFX应用程序
        //Platform.exit();


       /* stage.setTitle("聊天室");
        stage.setWidth(676);
        stage.setHeight(605);

        HBox hbox = new HBox();
        hbox.setPrefHeight(60);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);

        ImageView imageHead = new ImageView( "resource/icon.jpeg");
        imageHead.setFitHeight(40);
        imageHead.setFitWidth(40);

        VBox vbox= new VBox();
        vbox.setAlignment(Pos.CENTER_LEFT);
        vbox.setSpacing(10);

        AnchorPane apane= new AnchorPane();
        apane.setPrefWidth(180);
        Label nameLabel = new Label();
        nameLabel.setText("123456");
        nameLabel.setPrefWidth(120);
        nameLabel.setStyle("  -fx-font-size: 10pt;" +
                "    -fx-font-family: \"微软雅黑\";" +
                "    -fx-text-fill: black;");
        Label timeLabel = new Label("2019/07/12");
        timeLabel.setStyle("  -fx-font-size: 9pt;" +
                "    -fx-font-family: \"微软雅黑\";" +
                "    -fx-text-fill: #9b9d9e;");
        timeLabel.setAlignment(Pos.CENTER_RIGHT);
        apane.getChildren().addAll(nameLabel,timeLabel);

        AnchorPane.setLeftAnchor(nameLabel,0.0);
        AnchorPane.setRightAnchor( timeLabel,0.0);


        AnchorPane apane1= new AnchorPane();
        apane1.setPrefWidth(180);

        Label msglabel = new Label("你好你好你好你好你好你好你好你好你好你好你好你好");
        msglabel.setStyle("  -fx-font-size: 9pt;" +
                "    -fx-font-family: \"微软雅黑\";" +
                "    -fx-text-fill: #9b9d9e;");
        msglabel.setPrefWidth(140);

        Label newslabel = new Label("2");
        newslabel.setStyle("  -fx-font-size: 9pt;" +
                "    -fx-font-family: \"微软雅黑\";" +
                "    -fx-text-fill: #46b9ee;");
        newslabel.setAlignment(Pos.CENTER_RIGHT);
        apane1.getChildren().addAll(msglabel,newslabel);

        AnchorPane.setLeftAnchor(msglabel,0.0);
        AnchorPane.setRightAnchor( newslabel,0.0);


        vbox.getChildren().addAll(apane,apane1);


        hbox.getChildren().addAll(imageHead, vbox);

        Scene scene = new Scene(hbox);

        stage.setScene(scene);
        stage.show();
*/


    }
}
