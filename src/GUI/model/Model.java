package GUI.model;

import BE.Movie;
import BLL.LogicManager;
import javafx.collections.ObservableList;

public class Model {

    LogicManager logicManager = new LogicManager();

    public ObservableList<Movie> getMovieList(){
        return logicManager.getMovieList();
    }

    public void addMovie(Movie movie){
        logicManager.addMovie(movie);
    }

    public void deleteMovie(Movie movie){
        logicManager.deleteMovie(movie);
    }

    public void editMovie(Movie movie){
        logicManager.editMovie(movie);
    }
}
