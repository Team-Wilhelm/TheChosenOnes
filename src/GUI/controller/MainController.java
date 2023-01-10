package GUI.controller;

import BE.Movie;
import GUI.controller.cellFactory.MovieListCell;
import GUI.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final Model model = new Model();
    @FXML
    private ListView<Movie> moviesList;

    private void refreshItems(){
        moviesList.setItems(model.getMovieList());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moviesList.setCellFactory(param -> new MovieListCell());
        refreshItems();
    }

    public void btnAddMovieAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = openNewWindow();
        NewMovieController newMovieController = fxmlLoader.getController();
        newMovieController.setModel(model);
    }

    public void btnEditMovieAction(ActionEvent actionEvent) throws IOException {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null)
            new Alert(Alert.AlertType.ERROR, "Please, select a movie to edit").showAndWait();
        else{
            model.setMovieToEdit(movie);
            FXMLLoader fxmlLoader = openNewWindow();
            NewMovieController newMovieController = fxmlLoader.getController();
            newMovieController.setModel(model);
            newMovieController.setIsEditing();
        }
    }

    public void btnDeleteMovieAction(ActionEvent actionEvent) {
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
        return fxmlLoader;
    }
}
