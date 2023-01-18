package Budgetflix.GUI.controller;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;
import Budgetflix.BLL.AlertManager;
import Budgetflix.BudgetFlix;
import Budgetflix.GUI.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class NewMovieController {
    @FXML
    public Label imdbLabel;
    @FXML
    public Button lookUp;
    @FXML
    private TextField txtTitle, txtUserRating, txtIMDBRating, txtFilePath;
    @FXML
    private DatePicker dateLastView;
    private boolean isEditing = false;
    @FXML
    private ImageView moviePoster;
    private String moviePosterPath;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<>(){};

    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();

    @FXML
    public void initialize(){
        isEditing = false;
        //Populates CheckComboBox
        genresDropDown.getItems().addAll(FXCollections.observableList(model.getAllGenres()));
        dateLastView.setValue(LocalDate.now());

        lookUp.setOnAction(event -> {
            searchImdb();
        });
        txtTitle.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER)
            {
                searchImdb();
            }
        });
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
                if (model.editMovie(new Movie(title, filepath, moviePosterPath, lastView, imdbRating, userRating, genres)).isEmpty()){
                    Node node = (Node) actionEvent.getSource();
                    node.getScene().getWindow().hide();
                }
                else
                    alertManager.getAlert("ERROR", "File path is already used by a different movie!", actionEvent).showAndWait();
            }

            else{
                if (model.createMovie(new Movie(title, filepath, moviePosterPath, lastView, imdbRating, userRating, genres)).isEmpty()){
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
        try{
            moviePoster.setImage(new Image(movieToEdit.getMoviePoster()));
        }
        catch (Exception e){
            moviePoster.setImage(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/bimbo.jpg"))));
        }

        List<Genre> genres = movieToEdit.getGenres();
        for (Genre genre: genres){
            genresDropDown.getCheckModel().check(genre);
        }
    }

    private JSONObject getDataFromImdb(String searchData){
        HttpResponse<String> searchResponse = Unirest.get("http://www.omdbapi.com/?s="+searchData.trim()+"&apikey=b712184d")
                .asString();
        var searchDataResponse = new JSONObject(searchResponse.getBody());
        try {
            JSONObject searchResults = searchDataResponse.getJSONArray("Search").getJSONObject(0);
            String imdbID = new JSONObject(searchResponse.getBody()).getJSONArray("Search").getJSONObject(0).getString("imdbID");
            HttpResponse<String> movieResponse = Unirest.get("http://www.omdbapi.com/?i="+imdbID+"&apikey=b712184d")
                    .asString();
            return new JSONObject(movieResponse.getBody());
        }catch (Exception e)
        {
            return null;
        }

    }
    private void searchImdb(){
        JSONObject movieObject = getDataFromImdb(txtTitle.getText());
        genresDropDown.getCheckModel().clearChecks();
        if(movieObject != null)
        {
            txtIMDBRating.setText(movieObject.getString("imdbRating"));
            moviePosterPath = movieObject.getString("Poster");
            moviePoster.setImage(new Image(moviePosterPath));
            txtTitle.setText(movieObject.getString("Title"));
            var genres = movieObject.getString("Genre").split(",");
            var allGenres = genresDropDown.getItems();
            for (var genre : genres) {
                try {
                    var selectItem = allGenres.stream().filter(e -> e.getName().toLowerCase().equals(genre.trim().toLowerCase())).findFirst();
                    genresDropDown.getCheckModel().check(selectItem.get());
                }catch (Exception e){
                    model.addGenre(genre.trim());
                    model.getGenreList();
                    Genre newGenre = model.getAllGenres().stream().filter(p->p.getName().equals(genre.trim())).findFirst().get();
                    genresDropDown.getItems().add(newGenre);
                    genresDropDown.getCheckModel().check(newGenre);

                }
            }
        }else {
            //todo put here warining when there is no result.
        }
    }
}
