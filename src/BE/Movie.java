package BE;

import java.time.LocalDate;

public class Movie {
    private int id;
    private String name, fileLink;
    private LocalDate lastView;
    private double imdbRating;
    private double userRating;

    public Movie(String name, String fileLink, LocalDate lastView, double imdbRating, double userRating) {
        this.name = name;
        this.lastView = lastView;
        this.fileLink = fileLink;
        this.imdbRating = imdbRating;
        this.userRating = userRating;
    }

    public Movie(int id, String name, String fileLink, LocalDate lastView, double imdbRating, double userRating) {
        this(name, fileLink, lastView, imdbRating, userRating);
        this.id = id;
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
}
