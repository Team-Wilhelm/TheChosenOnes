package GUI.model;

import BE.Genre;
import BE.Movie;
import BLL.LogicManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class Model {
    private static Model instance=null;
    LogicManager logicManager = new LogicManager();
    Movie movieToEdit;

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        } return instance;
    }

    public ObservableList<Movie> filterMovies(String query, ObservableList<Genre> genres) //TODO add personal & IMDB rating
    {
        ObservableList<Movie> movies = getMovieList();
        List<Movie> filtered = new ArrayList<>();

        if (!query.isEmpty()) {
            for (Movie m : movies) {
                if (m.getName().toLowerCase().contains(query.toLowerCase()))
                    filtered.add(m);
            }
        }

        if (!genres.equals(getGenreList())) {
            for (Genre genre : genres) {
                    for (Movie movie : logicManager.getMoviesInGenre(genre)){
                        if(!filtered.contains(movie))
                            filtered.add(movie);
                    }
            }
        }


        else { //is this else statement needed?
            filtered.addAll(movies);
        }

        return FXCollections.observableArrayList(filtered);
    }

    //Movie methods

    public String createMovie(Movie movie) {
        return logicManager.addMovie(movie);
    }

    public String editMovie(Movie movie) {
        return logicManager.editMovie(new Movie(movieToEdit.getId(), movie.getName(), movie.getFileLink(), movie.getLastView(), movie.getImdbRating(), movie.getUserRating(), movie.getGenres()));
    }

    public void deleteMovie(Movie movie){
        logicManager.deleteMovie(movie);
    }

    public void setMovieToEdit(Movie movie){
        this.movieToEdit = movie;
    }

    public Movie getMovieToEdit() {
        return movieToEdit;
    }

    public ObservableList<Movie> getMovieList(){
        return logicManager.getMovieList();
    }

    public List<Movie> getMoviesInGenre(Genre genre){
        return logicManager.getMoviesInGenre(genre);
    }

    //Genre methods
    public List<Genre> getAllGenresFromMovie(Movie movie){
        return logicManager.getAllGenresFromMovie(movie);
    }

    public void addGenre(String genre){
        logicManager.addGenre(genre);
    }

    public void deleteGenre(Genre genre){
        logicManager.deleteGenre(genre);
    }

    public ObservableList<Genre> getGenreList(){
        return logicManager.getGenreList();
    }
}
