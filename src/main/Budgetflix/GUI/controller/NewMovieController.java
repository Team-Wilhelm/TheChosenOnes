package Budgetflix.GUI.controller;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;
import Budgetflix.BLL.AlertManager;
import Budgetflix.DAL.GenreDAO;
import Budgetflix.GUI.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

public class NewMovieController {
    @FXML
    private TextField txtTitle, txtUserRating, txtIMDBRating, txtFilePath;
    @FXML
    private DatePicker dateLastView;
    private boolean isEditing = false;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<>(){};

    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();

    @FXML
    public void initialize(){
        isEditing = false;
        //Populates CheckComboBox
        genresDropDown.getItems().addAll(FXCollections.observableList(new GenreDAO().getAllGenres()));
        dateLastView.setValue(LocalDate.now());
    }

    /**
     * Adds Movie to database based on user input, or if Movie already exists updates any changes made to the database.
     */
    @FXML
    private void btnSaveAction(ActionEvent actionEvent) {
        String title = txtTitle.getText().trim();
        String filepath = txtFilePath.getText().trim();
        String userRatingString = txtUserRating.getText().trim();
        String imdbRatingString = txtIMDBRating.getText().trim();
        LocalDate lastView = null;
        List<Genre> genres = genresDropDown.getCheckModel().getCheckedItems();

        if (title.isEmpty() || filepath.isEmpty()) {
            //Checks if the title or filepath (obligatory fields) are filled out
            if (title.isEmpty())
                txtTitle.setPromptText("Field must not be empty!");
            if (filepath.isEmpty())
                txtFilePath.setPromptText("Field must not be empty!");
        }

        if (dateLastView.getValue() != null)
            lastView = dateLastView.getValue();

        double userRating = stringToDoubleConverter(userRatingString);
        double imdbRating = stringToDoubleConverter(imdbRatingString);

        //Opens an alert if the rating is outside the range 0 and 10
        if (userRating < 0 || userRating > 10 || imdbRating < 0 || imdbRating > 10)
            alertManager.getAlert("ERROR", "Invalid rating! \nPlease, put in a number between 0.0 and 10.0", actionEvent).showAndWait();

        //Opens an alert if the extension of the file is not .mp4 or .mpeg4
        else if (!txtFilePath.getText().trim().endsWith(".mp4") && !txtFilePath.getText().trim().endsWith(".mpeg4"))
            alertManager.getAlert("ERROR", "Selected file format is not supported!", actionEvent).showAndWait();

        else {
            if (isEditing){
                if (model.editMovie(new Movie(title, filepath, lastView, imdbRating, userRating, genres)).isEmpty()){
                    Node node = (Node) actionEvent.getSource();
                    node.getScene().getWindow().hide();
                }
                else
                    alertManager.getAlert("ERROR", "File path is already used by a different movie!", actionEvent).showAndWait();
            }

            else{
                if (model.createMovie(new Movie(title, filepath, lastView, imdbRating, userRating, genres)).isEmpty()){
                    Node node = (Node) actionEvent.getSource();
                    node.getScene().getWindow().hide();
                }
                else
                    alertManager.getAlert("ERROR", "File path is already used by a different movie!", actionEvent).showAndWait();
            }
        }
    }

    /**
     * Enables an action to be cancelled.
     */

    @FXML
    private void btnCancelAction(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        node.getScene().getWindow().hide();
    }

    /**
     * Enables the user to go to the directory where a selected file is saved, or goes to their Videos directory.
     * Files can be mp4 or mpeg4.
     */
    @FXML
    private void btnChooseAction(ActionEvent actionEvent) {
        //puts the NewMovieView momentarily behind the file chooser
        Node node = (Node) actionEvent.getSource();
        Stage parentStage = (Stage) node.getScene().getWindow();
        parentStage.setAlwaysOnTop(false);

        Stage fileStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        File file;

        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Videos"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp4", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mpeg4", "*.mpeg4"));
        fileChooser.setTitle("Choose a movie file");

        try { //if the window is already holding a path, try to open the directory
            if (txtFilePath.getText() != null && !txtFilePath.getText().isEmpty()) {
                File previousFile = new File(Paths.get(txtFilePath.getText().trim()).toUri());
                fileChooser.setInitialDirectory(new File(previousFile.getParentFile().getAbsolutePath()));
                fileChooser.setInitialFileName(previousFile.getName());
            }
            file = fileChooser.showOpenDialog(fileStage);
        } catch (Exception e) { //otherwise, Videos directory is opened
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Videos"));
            file = fileChooser.showOpenDialog(fileStage);
        }
        if (file != null) {
            txtFilePath.setText(file.getAbsolutePath());
            parentStage.setAlwaysOnTop(true);
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
                result = -1;
            }
            return result;
        }
    }

    public void setIsEditing(){
        isEditing = true;
        Movie movieToEdit = model.getMovieToEdit();
        txtTitle.setText(movieToEdit.getName());
        txtFilePath.setText(movieToEdit.getFileLink());
        txtIMDBRating.setText(String.valueOf(movieToEdit.getImdbRating()));
        txtUserRating.setText(String.valueOf(movieToEdit.getUserRating()));
        dateLastView.setValue(movieToEdit.getLastView());

        List<Genre> genres = movieToEdit.getGenres();
        for (Genre genre: genres){
            genresDropDown.getCheckModel().check(genre);
        }
    }
}
