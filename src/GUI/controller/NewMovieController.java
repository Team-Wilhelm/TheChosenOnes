package GUI.controller;

import BE.Movie;
import GUI.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;

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

    @FXML
    private void btnSaveAction(ActionEvent actionEvent) {
        String title = txtTitle.getText().trim();
        String filepath = txtFilePath.getText().trim();
        String userRatingString = txtUserRating.getText().trim();
        String imdbRatingString = txtIMDBRating.getText().trim();
        LocalDate lastView = null;
        if (isEditing)
            lastView = dateLastView.getValue();

        if (title.isEmpty() || filepath.isEmpty()){
            if (title.isEmpty())
                txtTitle.setPromptText("Field must not be empty!");
            if (filepath.isEmpty())
                txtFilePath.setPromptText("Field must not be empty!");
        }
        else{
            double userRating = stringToDoubleConverter(userRatingString);
            double imdbRating = stringToDoubleConverter(imdbRatingString);
            if (dateLastView.getValue() != null)
                lastView = dateLastView.getValue();
            if (isEditing)
                model.editMovie(new Movie(title, filepath, lastView, imdbRating, userRating));
            else
                model.createMovie(new Movie(title, filepath, lastView, imdbRating, userRating));

            Node node = (Node) actionEvent.getSource();
            node.getScene().getWindow().hide();
        }
    }

    @FXML
    private void btnCancelAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    @FXML
    private void btnChooseAction(ActionEvent actionEvent) {
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

    private double stringToDoubleConverter(String rating){
        double result = 0;
        if (rating.isEmpty())
            return 0;
        else{
            try {
                result = Double.parseDouble(rating);
            }
            catch (NumberFormatException ex){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Invalid rating! \nPlease put in a number between 0.0 and 10.0");
                alert.showAndWait();
            }
        }
        return result;
    }

    public void setIsEditing(){
        isEditing = true;
        Movie movieToEdit = model.getMovieToEdit();
        txtTitle.setText(movieToEdit.getName());
        txtFilePath.setText(movieToEdit.getFileLink());
        txtIMDBRating.setText(String.valueOf(movieToEdit.getImdbRating()));
        txtUserRating.setText(String.valueOf(movieToEdit.getUserRating()));
        dateLastView.setValue(movieToEdit.getLastView());
    }
}
