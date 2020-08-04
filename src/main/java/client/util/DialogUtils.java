package client.util;

import javafx.scene.control.Alert;

public class DialogUtils {

        public static void WarningDialog(String head,String context){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("WARNING");
            alert.setHeaderText(head);
            alert.setContentText(context);

            alert.showAndWait();
        }
}
