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
    private static ObservableList<Movie> allMovies;
    private static ObservableList<Genre> allGenres;

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

    public ObservableList<Movie> filterMovies(String query, ObservableList<Genre> genres) //TODO add personal & IMDB rating
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
            if(check1 && check2)
                filtered.add(m);
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
        allMovies.remove(movie);
    }

    public void getMovieList(){
        //allMovies.clear();
        allMovies.setAll(logicManager.getAllMovies());
        //allMovies.addAll(logicManager.getAllMovies());
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

    public ObservableList<Genre> getAllGenres(){
        return allGenres;
    }

    public List<Movie> getMoviesInGenre(Genre genre){
        return logicManager.getMoviesInGenre(genre);
    }

    public String addGenre(String genre){
        return logicManager.addGenre(genre);
    }

    public void deleteGenre(Genre genre){
        logicManager.deleteGenre(genre);
        allGenres.remove(genre);
    }

    public void getGenreList(){
        //allGenres.clear();
        //allGenres.addAll(logicManager.getAllGenres());
        allGenres.setAll(logicManager.getAllGenres());
    }

    public List<Genre> getAllGenresFromMovie(Movie movie){
        return logicManager.getAllGenresFromMovie(movie);
    }
}
