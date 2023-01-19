package Budgetflix.BE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String name, fileLink, moviePoster;
    private LocalDate lastView;
    private double imdbRating, userRating;
    private List<Genre> genres;

    public Movie(String name, String fileLink, LocalDate lastView, double imdbRating, double userRating) {
        this.name = name;
        this.lastView = lastView;
        this.fileLink = fileLink;
        this.imdbRating = imdbRating;
        this.userRating = userRating;
        this.genres = new ArrayList<>();
    }

    public Movie(String name, String fileLink, String moviePoster, LocalDate lastView, double imdbRating, double userRating){
        this(name, fileLink, lastView, imdbRating, userRating);
        this.moviePoster = moviePoster;
    }

    public Movie(String name, String fileLink, String moviePoster, LocalDate lastView, double imdbRating, double userRating, List<Genre> genres){
        this(name, fileLink, moviePoster, lastView, imdbRating, userRating);
        this.genres = genres;
    }

    public Movie(String name, String fileLink, LocalDate lastView, double imdbRating, double userRating, List<Genre> genres){
        this(name, fileLink, lastView, imdbRating, userRating);
        this.genres = genres;
    }

    public Movie(int id, String name, String fileLink, String moviePoster, LocalDate lastView, double imdbRating, double userRating) {
        this(name, fileLink, moviePoster, lastView, imdbRating, userRating);
        this.id = id;
    }

    public Movie(int id, String name, String fileLink, String moviePoster, LocalDate lastView, double imdbRating, double userRating, List<Genre> genres){
        this(id, name, fileLink, moviePoster, lastView, imdbRating, userRating);
        this.genres = genres;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getFileLink() {
        return fileLink;
    }

    public LocalDate getLastView() {
        return lastView;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public List<Genre> getGenres(){
        return genres;
    }
    public String getGenresToString(){
        return genres.toString().replace("[","").replace("]","");
    }
}
