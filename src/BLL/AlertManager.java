package BLL;

import javafx.scene.control.Alert;

public class AlertManager {
    //TODO same filePath alert
    Alert alert;
    private static AlertManager instance=null;

    public AlertManager(){
        alert = new Alert(Alert.AlertType.ERROR);
    }

    public Alert getAlert(String type, String text){
        alert.setAlertType(Alert.AlertType.valueOf(type));
        alert.setTitle(type);
        alert.setContentText(text);
        return alert;
    }

    public static AlertManager getInstance(){
        if(instance == null){
            instance = new AlertManager();
        } return instance;
    }
}
