package BLL;

import BE.Genre;
import BE.Movie;
import DAL.GenreDAO;
import DAL.MovieDAO;
import java.util.List;

public class LogicManager implements ILogicManager {

    private MovieDAO movieDAO = new MovieDAO();
    private GenreDAO genreDAO = new GenreDAO();

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

}
