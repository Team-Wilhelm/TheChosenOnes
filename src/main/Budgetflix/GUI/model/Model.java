package Budgetflix.GUI.model;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;
import Budgetflix.BLL.LogicManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Model implements IModel {
    private static Model instance=null;
    LogicManager logicManager = new LogicManager();
    Movie movieToEdit;
    private static ObservableList<Movie> allMovies;
    private static ObservableList<Genre> allGenres;
    private ObservableList<Movie> oldMovieList;

    /**
     * Makes Model a singleton class.
     * @return an instance of the Model.
     */
    public static Model getInstance(){
        if(instance == null){
            instance = new Model();
        } return instance;
    }

    private Model(){
        allMovies = FXCollections.observableArrayList();
        allGenres = FXCollections.observableArrayList();
        oldMovieList = FXCollections.observableArrayList();
        getMovieList();
        getGenreList();
        oldMovieCheck();
    }

    /**
     * Enables the list of movies to be filtered based on different parameters and combine the results of these checks.
     * @param query
     * @param genres
     * @param IMDBrating
     * @param userRating
     * @return list of Movie after filtering.
     */
    public ObservableList<Movie> filterMovies(String query, ObservableList<Genre> genres, double IMDBrating, double userRating)
    {
        List<Movie> filtered = new ArrayList<>();

        for (Movie m : allMovies) {
            var check1 = true;
            if(!query.isEmpty())
                if(!m.getName().toLowerCase().contains(query.toLowerCase()))
                    check1 = false;

            var check2 = true;
            if(genres.size() != 0)
                if(!m.getGenres().containsAll(genres))
                    check2 = false;

            var check3 = true;
            if (m.getImdbRating() < IMDBrating)
                check3 = false;

            var check4 = true;
            if (m.getUserRating() < userRating)
                check4 = false;

            if(check1 && check2 && check3 && check4)
                filtered.add(m);
        }

        return FXCollections.observableArrayList(filtered);
    }

    //Movie methods
    public String createMovie(Movie movie) {
        return logicManager.addMovie(movie);
    }

    public String editMovie(Movie movie) {
        return logicManager.editMovie(new Movie(movieToEdit.getId(), movie.getName(), movie.getFileLink(), movie.getMoviePoster(), movie.getLastView(), movie.getImdbRating(), movie.getUserRating(), movie.getGenres()));
    }

    public void deleteMovie(Movie movie){
        logicManager.deleteMovie(movie);
        allMovies.remove(movie);
        oldMovieList.remove(movie);
    }

    public void getMovieList(){
        allMovies.setAll(logicManager.getAllMovies());
    }

    public ObservableList<Movie> getAllMovies(){
        return allMovies;
    }

    public void setMovieToEdit(Movie movie){
        this.movieToEdit = movie;
    }

    public Movie getMovieToEdit() {
        return movieToEdit;
    }

    /**
     * Checks for movies that haven't been opened in over 2 years and also have a personal rating below 6, and adds them to a list.
     */
    public void oldMovieCheck(){
        for(Movie m: allMovies){
            if(m.getUserRating()<6 && m.getLastView().isBefore(LocalDate.now().minusYears(2))) {
                oldMovieList.add(m);
            }
        }
    }

    public ObservableList<Movie> getOldMovies(){
        return oldMovieList;
    }

    public ObservableList<Genre> getAllGenres(){
        return allGenres;
    }

    public String addGenre(String genre){
        return logicManager.addGenre(genre);
    }

    public void deleteGenre(Genre genre){
        logicManager.deleteGenre(genre);
        allGenres.remove(genre);
    }

    public void getGenreList(){
        allGenres.setAll(logicManager.getAllGenres());
    }

    public void deleteMovies(List<Movie> moviesToDelete) {
        logicManager.deleteMovies(moviesToDelete);
    }
}
