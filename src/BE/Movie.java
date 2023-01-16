package BE;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class Movie {
    private int id;
    private String name, fileLink;
    private LocalDate lastView;
    private double imdbRating;
    private double userRating;
    private List<Genre> genres;

    public Movie(String name, String fileLink, LocalDate lastView, double imdbRating, double userRating) {
        this.name = name;
        this.lastView = lastView;
        this.fileLink = fileLink;
        this.imdbRating = imdbRating;
        this.userRating = userRating;
    }

    public Movie(String name, String fileLink, LocalDate lastView, double imdbRating, double userRating, List<Genre> genres){
        this(name, fileLink, lastView, imdbRating, userRating);
        this.genres = genres;
    }

    public Movie(int id, String name, String fileLink, LocalDate lastView, double imdbRating, double userRating) {
        this(name, fileLink, lastView, imdbRating, userRating);
        this.id = id;
    }

    public Movie(int id, String name, String fileLink, LocalDate lastView, double imdbRating, double userRating, List<Genre> genres){
        this(id, name, fileLink, lastView, imdbRating, userRating);
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

    public List<Genre> getGenres(){
        return genres;
    }
    public String getGenresToString(){
        return genres.toString().replace("[","").replace("]","");
    }
}
