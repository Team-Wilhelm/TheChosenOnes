package GUI.controller;

import BE.Movie;
import BLL.LogicManager;
import GUI.controller.cellFactory.MovieListCell;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public ListView<Movie> moviesList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moviesList.setCellFactory(param -> new MovieListCell());
        moviesList.setItems(new LogicManager().getMovieList());
    }
}
