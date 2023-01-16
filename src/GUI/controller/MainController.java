package GUI.controller;

import BE.Genre;
import BE.Movie;
import BLL.AlertManager;
import GUI.controller.cellFactory.MovieListCell;
import GUI.model.Model;
import com.google.common.collect.Comparators;
import com.google.common.collect.Ordering;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
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
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<Genre>(){};

    private void refreshMovieItems(){
        model.getMovieList();
        moviesList.setItems(model.getAllMovies());
    }
    private void refreshGenresItems(){
        model.getGenreList();
        genresDropDown.getItems().setAll(FXCollections.observableList(model.getAllGenres()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moviesList.setCellFactory(param -> new MovieListCell());

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(), sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        genresDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Genre>) observable -> {
            moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(),sliderIMDBRating.getValue(),sliderUserRating.getValue()));
        });

        sliderUserRating.valueProperty().addListener((observable, oldValue, newValue) ->
                moviesList.setItems(model.filterMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems(),sliderIMDBRating.getValue(), sliderUserRating.getValue())));

        sliderIMDBRating.valueProperty().addListener((observable, oldValue, newValue) ->
                moviesList.setItems(model.filterMovies(searchBar.getText(), genresDropDown.getCheckModel().getCheckedItems(), sliderIMDBRating.getValue(), sliderUserRating.getValue())));

        refreshMovieItems();
        refreshGenresItems();
    }

    @FXML
    private void btnAddMovieAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("../view/NewMovieView.fxml");
    }

    @FXML
    private void btnEditMovieAction(ActionEvent actionEvent) throws IOException {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null)
            new Alert(Alert.AlertType.ERROR, "Please, select a movie to edit").showAndWait();
        else{
            model.setMovieToEdit(movie);
            FXMLLoader fxmlLoader = openNewWindow("../view/NewMovieView.fxml");
            NewMovieController newMovieController = fxmlLoader.getController();
            newMovieController.setIsEditing();
        }
    }

    @FXML
    private void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null){
            alertManager.getAlert("ERROR", "Please, select a movie to delete!").showAndWait();
        }
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this movie ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                model.deleteMovie(movie);
                refreshMovieItems();
            }
        }
    }

    private FXMLLoader openNewWindow(String resource) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("BudgetFlix");
        stage.centerOnScreen();
        stage.show();
        Window window = scene.getWindow();
        window.setOnHiding(event -> {
            refreshGenresItems();
            refreshMovieItems();
        });
        return fxmlLoader; //TODO resource heavy, make return void and add class to handle fxml loader once
    }

    @FXML
    private void playMovie(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2){
            Movie movie = moviesList.getSelectionModel().getSelectedItem();
            String command = "C:\\Program Files\\Windows Media Player\\wmplayer.exe";
            String arg = movie.getFileLink();
            ProcessBuilder builder = new ProcessBuilder(command, arg);
            builder.start();
        }
    }

    @FXML
    private void btnAddGenreAction(ActionEvent actionEvent) throws IOException {
        openNewWindow("../view/NewGenreView.fxml");
    }

    @FXML
    private void btnDeleteGenreAction(ActionEvent actionEvent) {
        ArrayList<Genre> genres = new ArrayList<>(genresDropDown.getCheckModel().getCheckedItems());
        if (genres.size() == 0)
            alertManager.getAlert("ERROR", "Please, select a genre to delete!").showAndWait();
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this genre?");
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
