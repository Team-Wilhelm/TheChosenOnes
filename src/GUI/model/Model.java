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
    private ObservableList<Movie> allMovies;
    private ObservableList<Genre> allGenres;

    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        } return instance;
    }

    public Model(){
        allMovies = FXCollections.observableArrayList();
        allGenres = FXCollections.observableArrayList();
        getMovieList();
        getGenreList();
    }

    public String editMovie(Movie movie) {
        return logicManager.editMovie(new Movie(movieToEdit.getId(), movie.getName(), movie.getFileLink(), movie.getLastView(), movie.getImdbRating(), movie.getUserRating(), movie.getGenres()));
    }

    public ObservableList<Movie> filterMovies(String query, ObservableList<Genre> genres) //TODO add personal & IMDB rating
    {
        List<Movie> filtered = new ArrayList<>();

        if (!query.isEmpty()) {
            for (Movie m : allMovies) {
                if (m.getName().toLowerCase().contains(query.toLowerCase()))
                    filtered.add(m);
            }
        }

        if (!genres.equals(allGenres)) {
            for (Genre genre : genres) {
                    for (Movie movie : genre.getMovies()){
                        if(!filtered.contains(movie))
                            filtered.add(movie);
                    }
            }
        }


        else { //is this else statement needed?
            filtered.addAll(allMovies);
        }

        return FXCollections.observableArrayList(filtered);
    }


    public String createMovie(Movie movie) {
        return logicManager.addMovie(movie);
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


    public void getMovieList(){
        allMovies.clear();
        allMovies.addAll(logicManager.getAllMovies());
    }

    public ObservableList<Movie> getAllMovies(){
        return allMovies;
    }

    public ObservableList<Genre> getAllGenres(){
        return allGenres;
    }

    public List<Movie> getMoviesInGenre(Genre genre){
        return logicManager.getMoviesInGenre(genre);
    }

    public List<Genre> getAllGenresFromMovie(Movie movie){
        return logicManager.getAllGenresFromMovie(movie);
    }

    public void addGenre(String genre){
        logicManager.addGenre(genre);
    }

    public void deleteGenre(Genre genre){
        logicManager.deleteGenre(genre);
    }

    public void getGenreList(){
        allGenres.clear();
        allGenres.addAll(logicManager.getAllGenres());
    }
}
