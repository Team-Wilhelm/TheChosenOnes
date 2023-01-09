package GUI.controller;

import GUI.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class NewMovieController {
    @FXML
    private TextField txtTitle, txtUserRating, txtIMDBRating, txtFilePath;
    @FXML
    private DatePicker dateLastView;
    private Model model = null;
    private boolean isEditing = false;

    public void setModel(Model model){
        this.model = model;
    }

    @FXML
    public void initialize(){
        isEditing = false;
    }

    public void btnSaveAction(ActionEvent actionEvent) {

    }

    @FXML
    private void btnCancelAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    public void btnChooseAction(ActionEvent actionEvent) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file;
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Videos"));
        fileChooser.setTitle("Choose a movie file");
        try { //if the window is already holding a path, open the directory
            if (txtFilePath.getText() != null && !txtFilePath.getText().isEmpty()) {
                File previousFile = new File(Paths.get(txtFilePath.getText()).toUri());
                fileChooser.setInitialDirectory(new File(previousFile.getParentFile().getAbsolutePath()));
                fileChooser.setInitialFileName(previousFile.getName());
            }
            file = fileChooser.showOpenDialog(stage);
        } catch (Exception e) { //otherwise, Videos directory is opened
            file = fileChooser.showOpenDialog(stage);
        }
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());
        }
    }
}
