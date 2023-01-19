package Budgetflix.GUI.controller;

import Budgetflix.BE.Movie;
import Budgetflix.BLL.AlertManager;
import Budgetflix.GUI.controller.cellFactory.MovieListCell;
import Budgetflix.GUI.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class OldMovieViewController extends BudgetMother implements Initializable {
    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();
    @FXML
    private ListView<Movie> moviesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Populates ListView
        moviesList.setCellFactory(param -> new MovieListCell());
        moviesList.setItems(model.getOldMovies());
    }

    /**
     * Deletes a movie from the list and the database.
     * Calls a method in super class.
     */
    public void btnDeleteMovieAction(ActionEvent actionEvent) {
        Movie movie = moviesList.getSelectionModel().getSelectedItem();
        deleteMovie(actionEvent, movie);
        refreshMovieItems();
    }


    /**
     * Deletes all movies from a list and from the database.
     */
    public void btnDeleteAllMovies(ActionEvent actionEvent) {
        List<Movie> moviesToDelete = moviesList.getItems();
        Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete all movies?", actionEvent);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            model.deleteMovies(moviesToDelete);
            moviesToDelete.clear();
        }
        refreshMovieItems();
    }

    /**
     * Updates the ListView to reflect any changes made.
     */
    private void refreshMovieItems() {
        moviesList.setItems(model.getOldMovies());
    }
}
