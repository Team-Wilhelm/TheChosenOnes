package Budgetflix.GUI.controller;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;
import Budgetflix.BLL.AlertManager;
import Budgetflix.BudgetFlix;
import Budgetflix.GUI.controller.cellFactory.MovieListCell;
import Budgetflix.GUI.model.Model;
import com.google.common.collect.Comparators;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.awt.Desktop;

public class MainController extends BudgetMother implements Initializable {
    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();
    @FXML
    public Button nameSortButton, categorySortButton, userRatingSortButton, imdbRatingSortButton;
    @FXML
    private TextField searchBar;
    @FXML
    private ListView<Movie> moviesList;
    @FXML
    private Slider sliderUserRating, sliderIMDBRating;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<>(){};
    @FXML
    private Label lblUserValue, lblIMDBValue;
    @FXML
    private ImageView moviePoster;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moviePoster.setImage(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/bimbo.jpg"))));
        moviesList.setCellFactory(param -> new MovieListCell());
        setUpListeners();

        moviesList.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER)
                if(!moviesList.getSelectionModel().isEmpty())
                {
                    Movie movie = moviesList.getSelectionModel().getSelectedItem();
                    File mediaFile = new File(movie.getFileLink());
                    try {
                        Desktop.getDesktop().open(mediaFile);
                    } catch (Exception ex){
                        alertManager.getAlert("ERROR", "File not found!\nCannot play the selected movie.", event).showAndWait();
                    }
                }
        });

        setUpSliderColors(sliderUserRating, sliderIMDBRating);
        isOldMovieCheckTrue();
        refreshMovieItems();
        refreshGenresItems();
    }

    private void setUpListeners(){
        moviesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try{
                moviePoster.setImage(new Image(observable.getValue().getMoviePoster()));
            } catch (Exception e){
                moviePoster.setImage(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/bimbo.jpg"))));
            }
        });

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(), sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        genresDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Genre>) observable -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(),sliderIMDBRating.getValue(),sliderUserRating.getValue()));
            if(!genresDropDown.getCheckModel().getCheckedItems().isEmpty())
                genresDropDown.setTitle(genresDropDown.getCheckModel().getCheckedItems().toString().replace('[',' ').replace(']', ' ').trim());
            else genresDropDown.setTitle("Select Genre");
        });

        sliderUserRating.valueProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(), genresDropDown.getCheckModel().getCheckedItems(),
                    sliderIMDBRating.getValue(), sliderUserRating.getValue()));
            lblUserValue.setText(String.format(Locale.US,"%.1f",sliderUserRating.getValue()));
        });

        sliderIMDBRating.valueProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(), genresDropDown.getCheckModel().getCheckedItems(),
                    sliderIMDBRating.getValue(), sliderUserRating.getValue()));
            lblIMDBValue.setText(String.format(Locale.US,"%.1f",sliderIMDBRating.getValue()));
        });
    }

    /**
     * Updates ListView with any changes made to the movies in the database.
     */
    private void refreshMovieItems(){
        model.getMovieList();
        moviesList.setItems(model.getAllMovies());
    }

    /**
     * Updates ComboCheckBox with changes made to the genres in the database.
     */
    private void refreshGenresItems(){
        model.getGenreList();
        genresDropDown.getItems().setAll(FXCollections.observableList(model.getAllGenres()));
    }

    /**
     * Returns true if there are any movies in the oldMovieList.
     */
    private void isOldMovieCheckTrue(){
        if(!model.getOldMovies().isEmpty()){
            try {
                openNewWindow("/Budgetflix/GUI/view/OldMovieView.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Opens NewMovieView as a new window.
     */
    @FXML
    private void btnAddMovieAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("/Budgetflix/GUI/view/NewMovieView.fxml");

    }

    /**
     * Opens NewMovieView as a new window and fills the fields with movie data from the selected Movie.
     */
    @FXML
    private void btnEditMovieAction(ActionEvent actionEvent) throws IOException {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null)
            new Alert(Alert.AlertType.ERROR, "Please, select a movie to edit").showAndWait();
        else{
            model.setMovieToEdit(movie);
            FXMLLoader fxmlLoader = openNewWindow("/Budgetflix/GUI/view/NewMovieView.fxml");
            NewMovieController newMovieController = fxmlLoader.getController();
            newMovieController.setIsEditing();
        }
    }

    /**
     * Deletes selected movie from the database and updates the ListView to reflect the change.
     */
    @FXML
    private void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        deleteMovie(actionEvent, movie);
        refreshMovieItems();
    }

    private FXMLLoader openNewWindow(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BudgetFlix.class.getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Budgetflix");
        stage.getIcons().add(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/budgetflixIcon.png"))));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();

        if (resource.equals("/Budgetflix/GUI/view/OldMovieView.fxml"))
            stage.showAndWait();
        else
            stage.show();

        //TODO i am speed
        Window window = scene.getWindow();
        window.setOnHiding(event -> {
            refreshGenresItems();
            refreshMovieItems();
        });

        return fxmlLoader;
    }

    /**
     * Prompts the user to select an application to play the chosen file,
     */
    @FXML
    private void playMovie(MouseEvent mouseEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2) {
            File mediaFile = new File(movie.getFileLink());
            try {
                Desktop.getDesktop().open(mediaFile);
            } catch (Exception ex){
                alertManager.getAlert("ERROR", "File not found!\nCannot play the selected movie.", mouseEvent).showAndWait();
            }
        }
        mouseEvent.consume();
    }

    /**
     * Opens NewGenreView as a new window.
     */
    @FXML
    private void btnAddGenreAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("/Budgetflix/GUI/view/NewGenreView.fxml");
    }

    /**
     * Deletes selected genres from database and refreshes the ComboChoiceBox with the changes.
     */
    @FXML
    private void btnDeleteGenreAction(ActionEvent actionEvent) {
        ArrayList<Genre> genres = new ArrayList<>(genresDropDown.getCheckModel().getCheckedItems());
        if (genres.size() == 0)
            alertManager.getAlert("ERROR", "Please, select a genre to delete!", actionEvent).showAndWait();
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this genre?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (Genre genre : genres){
                    model.deleteGenre(genre);
                    genresDropDown.getItems().clear();
                }
                refreshGenresItems();
            }
        }
    }

    public void btnNameSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getName));
    }

    public void btnCategorySortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getGenresToString));
    }

    public void btnUserRatingSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getUserRating));
    }

    public void btnImdbRatingSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getImdbRating));
    }

    public void sortData(Comparator<Movie> movieComparator)
    {
        var listOfMovies = moviesList.getItems();
        boolean sorted = Comparators.isInOrder(listOfMovies,movieComparator);
        if(!sorted)
            Collections.sort(listOfMovies,movieComparator);
        else
            Collections.sort(listOfMovies,movieComparator.reversed());
        moviesList.setItems(listOfMovies);
    }
}
