package GUI.controller;

import BE.Genre;
import BE.Movie;
import DAL.GenreDAO;
import GUI.controller.cellFactory.MovieListCell;
import GUI.model.Model;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckComboBox;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final Model model = Model.getInstance();
    @FXML
    private TextField searchBar;
    @FXML
    private ListView<Movie> moviesList;
    @FXML
    private CheckComboBox<Genre> genresDropDown = new CheckComboBox<Genre>(){};

    private void refreshItems(){
        moviesList.setItems(model.getMovieList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moviesList.setCellFactory(param -> new MovieListCell());

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            moviesList.getItems().clear();
            moviesList.setItems(model.searchMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems()));
        });

        genresDropDown.getCheckModel().getCheckedItems().addListener((ListChangeListener<? super Genre>) observable -> {
            moviesList.getItems().clear();
            moviesList.setItems(model.searchMovies(searchBar.getText(),genresDropDown.getCheckModel().getCheckedItems()));
        });

        refreshItems();

        //genresDropDown.getItems().addAll(FXCollections.observableList((new GenreDAO().getAllGenres()).stream().map(e -> e.getName()).collect(Collectors.toList())));
        genresDropDown.getItems().addAll(FXCollections.observableList(new GenreDAO().getAllGenres()));
    }

    @FXML
    private void btnAddMovieAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = openNewWindow();
        NewMovieController newMovieController = fxmlLoader.getController();
    }

    // make util class for alerts to avoid creating multiple alerts

    @FXML
    private void btnEditMovieAction(ActionEvent actionEvent) throws IOException {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null)
            new Alert(Alert.AlertType.ERROR, "Please, select a movie to edit").showAndWait();
        else{
            model.setMovieToEdit(movie);
            FXMLLoader fxmlLoader = openNewWindow();
            NewMovieController newMovieController = fxmlLoader.getController();
            newMovieController.setIsEditing();
        }
    }

    @FXML
    private void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("No movie selected");
            errorAlert.setContentText("Please, select a movie to delete");
            errorAlert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete a movie");
            alert.setContentText("Do you really wish to delete this movie ?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                model.deleteMovie(movie);
                refreshItems();
            }
        }
    }

    private FXMLLoader openNewWindow() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/NewMovieView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("BudgetFlix");
        stage.centerOnScreen();
        stage.show();
        Window window = scene.getWindow();
        window.setOnHiding(event -> refreshItems());
        return fxmlLoader; //resource heavy, make return void and add class to handle fxml loader once
    }

    @FXML
    private void playMovie(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2){
            Movie movie = moviesList.getSelectionModel().getSelectedItem();
            String command = "C:\\Program Files\\Windows Media Player\\wmplayer.exe";
            String arg = movie.getFileLink();
            //Building a process
            ProcessBuilder builder = new ProcessBuilder(command, arg);
            //Starting the process
            builder.start();
        }
    };
}
