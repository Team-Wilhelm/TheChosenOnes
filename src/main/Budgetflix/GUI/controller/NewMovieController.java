package Budgetflix.GUI.controller;

import Budgetflix.BE.*;
import Budgetflix.BLL.AlertManager;
import Budgetflix.BudgetFlix;
import Budgetflix.GUI.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewMovieController extends BudgetMother {
    @FXML
    private Label lblUserValue, lblIMDBValue;
    @FXML
    private Button lookUp;
    @FXML
    private TextField txtTitle, txtFilePath;
    @FXML
    private DatePicker dateLastView;
    @FXML
    private ImageView moviePoster;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<>(){};
    @FXML
    private Slider sliderUserRating, sliderIMDBRating;

    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();
    private MainController mainController;
    private String moviePosterPath;
    private boolean isEditing = false;


    @FXML
    public void initialize(){
        isEditing = false;
        //Populates CheckComboBox
        genresDropDown.getItems().addAll(FXCollections.observableList(model.getAllGenres()));
        dateLastView.setValue(LocalDate.now()); //Adds today's date as the last watched date by default
        moviePoster.setImage(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/bimbo.jpg"))));

        lookUp.setOnAction(event -> searchImdb(event));

        txtTitle.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER)
                searchImdb(event);
        });

        //Sets up listeners for rating sliders
        sliderUserRating.valueProperty().addListener((observable, oldValue, newValue) -> {
            lblUserValue.setText(String.format(Locale.US,"%.1f",sliderUserRating.getValue()));
        });

        sliderIMDBRating.valueProperty().addListener((observable, oldValue, newValue) -> {
            lblIMDBValue.setText(String.format(Locale.US,"%.1f",sliderIMDBRating.getValue()));
        });

        setUpSliderColors(sliderUserRating, sliderIMDBRating);
    }


    /**
     * Adds Movie to database based on user input, or if Movie already exists updates any changes made to the database.
     */
    @FXML
    private void btnSaveAction(ActionEvent actionEvent) {
        String title = txtTitle.getText().trim();
        String filepath = txtFilePath.getText().trim();
        LocalDate lastView = dateLastView.getValue();
        List<Genre> genres = genresDropDown.getCheckModel().getCheckedItems();
        //Rounds the input from a slider to one decimal point
        double userRating = Double.parseDouble(String.format(Locale.US,"%.1f",sliderUserRating.getValue()));
        double imdbRating = Double.parseDouble(String.format(Locale.US,"%.1f",sliderIMDBRating.getValue()));
        String moviePosterPath = moviePoster.getImage().getUrl();

        if (title.isEmpty() || filepath.isEmpty()) {
            //Checks if the title or filepath (obligatory fields) are filled out
            if (title.isEmpty())
                txtTitle.setPromptText("Field must not be empty!");
            if (filepath.isEmpty())
                txtFilePath.setPromptText("Field must not be empty!");
        }

        //Opens an alert if the extension of the file is not .mp4 or .mpeg4
        if (!txtFilePath.getText().trim().endsWith(".mp4") && !txtFilePath.getText().trim().endsWith(".mpeg4"))
            alertManager.getAlert("ERROR", "Selected file format is not supported!", actionEvent).showAndWait();

        else {
            if (isEditing){
                //Checks if an exception message has been returned, if not, closes the window
                if (model.editMovie(new Movie(title, filepath, moviePosterPath, lastView, imdbRating, userRating, genres)).isEmpty()){
                    Node node = (Node) actionEvent.getSource();
                    node.getScene().getWindow().hide();
                    //Refreshes the items in the main view
                    mainController.refreshGenresItems();
                    mainController.refreshMovieItems();
                }
                else
                    alertManager.getAlert("ERROR", "File path is already used by a different movie!", actionEvent).showAndWait();
            }

            else{
                //Checks if an exception message has been returned, if not, closes the window
                if (model.createMovie(new Movie(title, filepath, moviePosterPath, lastView, imdbRating, userRating, genres)).isEmpty()){
                    Node node = (Node) actionEvent.getSource();
                    node.getScene().getWindow().hide();
                    //Refreshes the items in the main view
                    mainController.refreshGenresItems();
                    mainController.refreshMovieItems();

                }
                else{
                    alertManager.getAlert("ERROR", "File path is already used by a different movie!", actionEvent).showAndWait();
                }
            }
        }
    }

    /**
     * Closes the window.
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
        }
    }

    /**
     * If a movie is being edited, populate the corresponding fields with the previously saved data
     */
    public void setIsEditing(){
        isEditing = true;
        Movie movieToEdit = model.getMovieToEdit();
        txtTitle.setText(movieToEdit.getName());
        txtFilePath.setText(movieToEdit.getFileLink());
        sliderUserRating.setValue(movieToEdit.getUserRating());
        sliderIMDBRating.setValue(movieToEdit.getImdbRating());
        dateLastView.setValue(movieToEdit.getLastView());
        try{
            moviePoster.setImage(new Image(movieToEdit.getMoviePoster()));
        } catch (Exception e){
            moviePoster.setImage(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/bimbo.jpg"))));
        }

        //Adds all genres to the CheckComboBox, checks the genres belonging to the movie
        List<Genre> genres = movieToEdit.getGenres();
        for (Genre genre: genres){
            genresDropDown.getCheckModel().check(genre);
        }
    }

    /**
     * Getting data(Name,Poste,Rating,Genres) from the omdbapi - open IMDB api.
     * @param searchData - Name of the movie you want to search for.
     * @return JSONObject - The all the movie data for the searched movie
     */
    private JSONObject getDataFromImdb(String searchData){
        // Search the movie
        HttpResponse<String> searchResponse = Unirest.get("http://www.omdbapi.com/?s="+searchData.trim()+"&apikey=b712184d")
                .asString();
        var searchDataResponse = new JSONObject(searchResponse.getBody());
        try { // If no movie is found throw an Exception
            // From the list of movies, get the ID of the first movie.
            String imdbID = new JSONObject(searchResponse.getBody()).getJSONArray("Search").getJSONObject(0).getString("imdbID");
            // Search the movie again, but with corresponding id, to get all the data for the corresponding movie.
            HttpResponse<String> movieResponse = Unirest.get("http://www.omdbapi.com/?i="+imdbID+"&apikey=b712184d")
                    .asString();
            return new JSONObject(movieResponse.getBody());
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * Search the Imdb database with omdbapi
     * @param event
     */
    private void searchImdb(Event event){
        // Get all the movie data for the searched MovieTitle
        JSONObject movieObject = getDataFromImdb(txtTitle.getText());
        if(movieObject != null) //Check if any movie is found
        {
            genresDropDown.getCheckModel().clearChecks();

            // Get the values from JSONObject by corresponding Key
            sliderIMDBRating.setValue(Double.parseDouble(movieObject.getString("imdbRating")));
            moviePosterPath = movieObject.getString("Poster");
            moviePoster.setImage(new Image(moviePosterPath));
            txtTitle.setText(movieObject.getString("Title"));
            var genres = movieObject.getString("Genre").split(",");
            var allGenres = genresDropDown.getItems();
            for (var genre : genres) {
                try { // Select all the genres that each movie has
                    var selectItem = allGenres.stream().filter(e -> e.getName().toLowerCase().equals(genre.trim().toLowerCase())).findFirst();
                    genresDropDown.getCheckModel().check(selectItem.get());
                }catch (Exception e){ // If the genre is not in a list, add it to the DB and select it.
                    model.addGenre(genre.trim());
                    model.getGenreList();
                    Genre newGenre = model.getAllGenres().stream().filter(p->p.getName().equals(genre.trim())).findFirst().get();
                    genresDropDown.getItems().add(newGenre);
                    genresDropDown.getCheckModel().check(newGenre);
                }
            }
        }else {
            alertManager.getAlert("WARNING", "No movie found.", event).showAndWait();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
