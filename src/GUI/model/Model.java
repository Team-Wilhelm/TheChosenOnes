package GUI.model;

import BE.Genre;
import BE.Movie;
import BLL.LogicManager;
import javafx.collections.ObservableList;

import java.util.List;

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

    public List<Movie> getMoviesInGenre(Genre genre){
        return logicManager.getMoviesInGenre(genre);
    }

    public void addGenre(String genre){
        logicManager.addGenre(genre);
    }

    public void deleteGenre(Genre genre){
        logicManager.deleteGenre(genre);
    }
}
