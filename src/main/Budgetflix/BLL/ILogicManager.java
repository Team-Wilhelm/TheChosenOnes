package BLL;

import BE.Genre;
import BE.Movie;

import java.util.List;

public interface ILogicManager {
    public String addMovie(Movie movie);

    public String editMovie(Movie movie);

    public void deleteMovie(Movie movie);

    public List<Movie> getAllMovies();

    public String addGenre(String genre);

    public void deleteGenre(Genre genre);

    public List<Genre> getAllGenres();
}
