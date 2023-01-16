package GUI.controller;

import BE.Movie;
import BLL.AlertManager;
import GUI.controller.cellFactory.MovieListCell;
import GUI.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class OldMovieViewController implements Initializable {
    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();
    @FXML
    private ListView<Movie> moviesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moviesList.setCellFactory(param -> new MovieListCell());
        moviesList.setItems(model.getOldMovies());
    }

    public void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        if (movie == null){
            alertManager.getAlert("ERROR", "Please, select a movie to delete!").showAndWait();
        }
        else{
            Node node = (Node) actionEvent.getSource();
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this movie?");
            alert.initOwner(node.getScene().getWindow());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                model.deleteMovie(movie);
            }
        }
        refreshMovieItems();
    }


    public void btnDeleteAllMovies(ActionEvent actionEvent) {
    }

    private void refreshMovieItems() {
        moviesList.setItems(model.getOldMovies());
    }
}
