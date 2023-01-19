package Budgetflix.GUI.model;

import Budgetflix.BE.*;
import javafx.collections.ObservableList;


public interface IModel {
    public ObservableList<Movie> filterMovies(String query, ObservableList<Genre> genres, double IMDBrating, double userRating);

    //Movie methods
    public String createMovie(Movie movie);

    public String editMovie(Movie movie);

    public void deleteMovie(Movie movie);

    public void getMovieList();

    public ObservableList<Movie> getAllMovies();

    public void setMovieToEdit(Movie movie);

    public Movie getMovieToEdit();

    public ObservableList<Genre> getAllGenres();


    public String addGenre(String genre);

    public void deleteGenre(Genre genre);

    public void getGenreList();
}
