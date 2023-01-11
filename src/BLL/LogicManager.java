package BLL;

import BE.Genre;
import BE.Movie;
import DAL.GenreDAO;
import DAL.MovieDAO;
import java.util.List;

public class LogicManager {

    private MovieDAO movieDAO = new MovieDAO();
    private GenreDAO genreDAO = new GenreDAO();


    public List<Movie> getAllMovies(){
        return movieDAO.getAllMovies();
    }

    public List<Genre> getAllGenres(){
        return genreDAO.getAllGenres();
    }

    public String addMovie(Movie movie){
        return movieDAO.addMovie(movie);
    }

    public void deleteMovie(Movie movie){
        movieDAO.deleteMovie(movie);
    }

    public String editMovie(Movie movie){
        return movieDAO.editMovie(movie);
    }

    public List<Movie> getMoviesInGenre(Genre genre){
        return genreDAO.getMoviesInGenre(genre);
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
