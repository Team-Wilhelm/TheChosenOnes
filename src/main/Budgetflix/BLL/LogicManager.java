package Budgetflix.BLL;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;
import Budgetflix.DAL.GenreDAO;
import Budgetflix.DAL.MovieDAO;

import java.util.List;

public class LogicManager implements ILogicManager {
    private final MovieDAO movieDAO = new MovieDAO();
    private final GenreDAO genreDAO = new GenreDAO();

    //Pass-through layer between Model and DAOs
    public String addMovie(Movie movie){
        return movieDAO.addMovie(movie);
    }

    public String editMovie(Movie movie){
        return movieDAO.editMovie(movie);
    }

    public void deleteMovie(Movie movie){
        movieDAO.deleteMovie(movie);
    }

    public List<Movie> getAllMovies(){
        return movieDAO.getAllMovies();
    }

    public String addGenre(String genre){
        return genreDAO.addGenreToDatabase(genre);
    }

    public void deleteGenre(Genre genre){
        genreDAO.deleteGenre(genre);
    }

    public List<Genre> getAllGenres(){
        return genreDAO.getAllGenres();
    }

    public void deleteMovies(List<Movie> moviesToDelete) {
        movieDAO.deleteMovies(moviesToDelete);
    }
}
