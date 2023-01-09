package BLL;

import BE.Movie;
import DAL.MovieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LogicManager {

    private final ObservableList<Movie> allMovies;
    MovieDAO movieDAO = new MovieDAO();


    public LogicManager() {
        allMovies = FXCollections.observableArrayList();
    }

    public void getAllMovies(){
        allMovies.clear();
        allMovies.addAll(movieDAO.getAllMovies());
    }

    /**
     * @return an observable list with all movies added.
     */
    public ObservableList<Movie> getMovieList(){
        getAllMovies();
        return allMovies;
    }

}
