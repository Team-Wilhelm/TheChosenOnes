package Budgetflix.BLL;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Window;

public class AlertManager {
    Alert alert;
    private static AlertManager instance=null;

    public AlertManager(){
        alert = new Alert(Alert.AlertType.ERROR);
    }

    public Alert getAlert(String type, String text, Event actionEvent){
        alert.setAlertType(Alert.AlertType.valueOf(type));
        alert.setTitle(type);
        alert.setContentText(text);

        Node node = (Node) actionEvent.getSource();
        Window window = node.getScene().getWindow();
        if (alert.getOwner() == null)
            alert.initOwner(window);
        return alert;
    }

    public static AlertManager getInstance(){
        if(instance == null){
            instance = new AlertManager();
        } return instance;
    }
}
