package BLL;

import BE.Genre;
import BE.Movie;
import DAL.GenreDAO;
import DAL.MovieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LogicManager {

    private final ObservableList<Movie> allMovies;
    private final ObservableList<Genre> allGenres;
    MovieDAO movieDAO = new MovieDAO();
    GenreDAO genreDAO = new GenreDAO();

    public LogicManager() {
        allMovies = FXCollections.observableArrayList();
        allGenres = FXCollections.observableArrayList();
    }

    public void getAllMovies(){
        allMovies.clear();
        allMovies.addAll(movieDAO.getAllMovies());
    }

    public void getAllGenres(){
        allGenres.clear();
        allGenres.addAll(genreDAO.getAllGenres());
    }

    public ObservableList<Movie> getMovieList(){
        getAllMovies();
        return allMovies;
    }


    public void addMovie(Movie movie){
        movieDAO.addMovie(movie);
    }

    public void deleteMovie(Movie movie){
        movieDAO.deleteMovie(movie);
    }

    public void editMovie(Movie movie){
        movieDAO.editMovie(movie);
    }

    public List<Movie> getMoviesInGenre(Genre genre){
        return genreDAO.getMoviesInGenre(genre);
    }

    public ObservableList<Genre> getGenreList(){
        getAllGenres();
        return allGenres;
    }

    public void addGenre(String genre){
        genreDAO.addGenreToDatabase(genre);
    }

    public void deleteGenre(Genre genre){
        genreDAO.removeGenreFromDatabase(genre);
    }

    public List<Genre> getAllGenresFromMovie(Movie movie){
        return movieDAO.getAllGenresFromMovie(movie.getId());
    }
}
