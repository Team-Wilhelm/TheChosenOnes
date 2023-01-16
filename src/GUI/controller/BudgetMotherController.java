package GUI.controller;

import BE.Movie;
import BLL.AlertManager;
import GUI.model.Model;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.util.Optional;

public class BudgetMotherController {
    private final Model model = Model.getInstance();
    private final AlertManager alertManager = AlertManager.getInstance();

    @FXML
    protected void btnDeleteMovieAction(ActionEvent actionEvent, Movie movie) {
        if (movie == null){
            alertManager.getAlert("ERROR", "Please, select a movie to delete!").showAndWait();
        }
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this movie ?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                model.deleteMovie(movie);
            }
        }
    }
}
