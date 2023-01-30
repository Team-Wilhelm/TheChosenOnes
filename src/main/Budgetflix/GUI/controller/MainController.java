package Budgetflix.GUI.controller;

import Budgetflix.BE.*;
import Budgetflix.BLL.AlertManager;
import Budgetflix.BudgetFlix;
import Budgetflix.GUI.controller.cellFactory.MovieListCell;
import Budgetflix.GUI.model.Model;
import com.google.common.collect.Comparators;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.awt.Desktop;

public class MainController extends BudgetMother implements Initializable {
    @FXML
    private TextField searchBar;
    @FXML
    private ListView<Movie> moviesList;
    @FXML
    private ListView<Genre> genreListView;
    @FXML
    private Slider sliderUserRating, sliderIMDBRating;
    @FXML
    private Label lblUserValue, lblIMDBValue;
    @FXML
    private ImageView moviePoster;
    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();

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

        setUpSliderColors(sliderUserRating, sliderIMDBRating); //calls a method from super class
        isOldMovieCheckTrue(); //check for any movies not watched in the past 2 years and with rating below 6
        moviesList.setItems(model.getAllMovies());
        genreListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //allows the selection of multiple genres at once
        genreListView.setItems(model.getAllGenres());
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
            moviesList.setItems(model.filterMovies(searchBar.getText(),genreListView.getSelectionModel().getSelectedItems(), sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        genreListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<? super Genre>) observable -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genreListView.getSelectionModel().getSelectedItems(),sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        sliderUserRating.setOnMouseReleased(event -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(), genreListView.getSelectionModel().getSelectedItems(),
                    sliderIMDBRating.getValue(), sliderUserRating.getValue()));
            lblUserValue.setText(String.format(Locale.US,"%.1f",sliderUserRating.getValue()));
        });

        sliderIMDBRating.setOnMouseReleased(event -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(), genreListView.getSelectionModel().getSelectedItems(),
                    sliderIMDBRating.getValue(), sliderUserRating.getValue()));
            lblIMDBValue.setText(String.format(Locale.US,"%.1f",sliderIMDBRating.getValue()));
        });
    }

    /**
     * Updates ListView with any changes made to the movies in the database.
     */
    public void refreshMovieItems(){
        model.getMovieList();
        moviesList.setItems(model.getAllMovies());
    }

    /**
     * Updates ComboCheckBox with changes made to the genres in the database.
     */
    public void refreshGenresItems(){
        model.getGenreList();
        genreListView.setItems(model.getAllGenres());
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
        FXMLLoader fxmlLoader = (FXMLLoader) openNewWindow("/Budgetflix/GUI/view/NewMovieView.fxml")[0];
        NewMovieController newMovieController = fxmlLoader.getController();
        newMovieController.setMainController(this);
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
            Object[] objects = openNewWindow("/Budgetflix/GUI/view/NewMovieView.fxml");
            FXMLLoader fxmlLoader = (FXMLLoader) objects[0];
            NewMovieController newMovieController = fxmlLoader.getController();
            newMovieController.setIsEditing();
            newMovieController.setMainController(this);
        }
    }

    /**
     * Deletes selected movie from the database and updates the ListView to reflect the change.
     * Calls deleteMovie in super class.
     */
    @FXML
    private void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        deleteMovie(actionEvent, movie);
        moviesList.setItems(model.getAllMovies());
    }

    /**
     *
     * @param resource the path of an fxml file to open
     * @return an array holding an FXML Loader and the current Window
     */
    private Object[] openNewWindow(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BudgetFlix.class.getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Budgetflix");
        stage.getIcons().add(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/budgetflixIcon.png"))));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.centerOnScreen();

        //The main window should only launch after all old movies have been reviewed and the window is closed
        if (resource.equals("/Budgetflix/GUI/view/OldMovieView.fxml"))
            stage.showAndWait();
        else
            stage.show();
        Window window = scene.getWindow();
        return new Object[]{fxmlLoader, window};
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
        Window window = (Window) openNewWindow("/Budgetflix/GUI/view/NewGenreView.fxml")[1];
        window.setOnHiding(event -> refreshGenresItems()); //when the new genre window is closed, all genre items refresh
    }

    /**
     * Deletes selected genres from database and refreshes the genreListView with the changes.
     */
    @FXML
    private void btnDeleteGenreAction(ActionEvent actionEvent) {
        ArrayList<Genre> genres = new ArrayList<>(genreListView.getSelectionModel().getSelectedItems());
        if (genres.size() == 0)
            alertManager.getAlert("ERROR", "Please, select a genre to delete!", actionEvent).showAndWait();
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this genre?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                for (Genre genre : genres){
                    model.deleteGenre(genre);
                }
                refreshGenresItems();
                refreshMovieItems();
            }
        }
    }

    /**
     * Sort data by Name
     */
    public void btnNameSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getName));
    }

    /**
     * Sort data by Genres
     */
    public void btnCategorySortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getGenresToString));
    }

    /**
     * Sort data by UserRating
     */
    public void btnUserRatingSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getUserRating));
    }

    /**
     * Sort data by ImdbRating
     */
    public void btnImdbRatingSortAction(ActionEvent actionEvent) {
        sortData(Comparator.comparing(Movie::getImdbRating));
    }

    /**
     * Sorts the data according to specified button clicked.
     *
     * @param movieComparator - A specific method of the Movie, which to use to compare the list and order it by.
     */
    public void sortData(Comparator<Movie> movieComparator)
    {
        var listOfMovies = moviesList.getItems();
        boolean sorted = Comparators.isInOrder(listOfMovies,movieComparator);

        if(!sorted) //If !sorted, sort the list
            Collections.sort(listOfMovies,movieComparator);
        else //If sorted, reverse the order of the list.
            Collections.sort(listOfMovies,movieComparator.reversed());
        moviesList.setItems(listOfMovies);
    }

    /**
     * Wishes the user a nice day :)
     * (added for symmetry purposes of our buttons)
     * (and a nice day)
     */
    public void btnClickMeAction(ActionEvent actionEvent) {
        alertManager.getAlert("INFORMATION", "Have a nice day :)", actionEvent).showAndWait();
    }
}
