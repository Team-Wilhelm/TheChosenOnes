package GUI.controller;

import BE.Genre;
import BE.Movie;
import DAL.GenreDAO;
import GUI.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NewMovieController {
    @FXML
    private TextField txtTitle, txtUserRating, txtIMDBRating, txtFilePath;
    @FXML
    private DatePicker dateLastView;
    private Model model = null;
    private boolean isEditing = false;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<Genre>(){};

    public void setModel(Model model){
        this.model = model;
    }

    @FXML
    public void initialize(){
        isEditing = false;
        //genresDropDown.getItems().addAll(FXCollections.observableList((new GenreDAO().getAllGenres()).stream().map(e -> e.getName()).collect(Collectors.toList())));
        genresDropDown.getItems().addAll(FXCollections.observableList(new GenreDAO().getAllGenres()));
        dateLastView.setValue(LocalDate.now());
    }

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
            showAlert("ERROR", "Invalid rating! \nPlease, put in a number between 0.0 and 10.0");
        //Opens an alert if the extension of the file is not .mp4 or .mpeg4
        if (!txtFilePath.getText().trim().endsWith(".mp4") && !txtFilePath.getText().trim().endsWith(".mpeg4"))
            showAlert("ERROR", "Selected file format is not supported!");
        else {
            if (isEditing)
                model.editMovie(new Movie(title, filepath, lastView, imdbRating, userRating, genres));
            else
                model.createMovie(new Movie(title, filepath, lastView, imdbRating, userRating, genres));

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mp4", "*.mp4"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mpeg4", "*.mpeg4"));
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
                showAlert("ERROR", "Invalid rating! \nPlease, put in a number between 0.0 and 10.0");
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
        List<Genre> genres = model.getAllGenresFromMovie(movieToEdit);
        List<Integer> genreIndices = new ArrayList<>();
        for (Genre genre: genres){
            genreIndices.add(genresDropDown.getCheckModel().getItemIndex(genre));
        }
        for (int i = 0; i < genreIndices.size(); i++){
            genresDropDown.getCheckModel().check(i);
        }
    }

    private void showAlert(String type, String text){
        Alert alert = new Alert(Alert.AlertType.valueOf(type));
        alert.setTitle("Error");
        alert.setContentText(text);
        alert.showAndWait();
    }
}
