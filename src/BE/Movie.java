package BE;

import java.time.LocalDate;

public class Movie {
    int id;
    String name, fileLink;
    LocalDate lastView;
    //TODO add ratings please


    public Movie(int id, String name, String fileLink, LocalDate lastView) {
        this(name, fileLink, lastView);
        this.id = id;
    }

    public Movie(String name, String fileLink, LocalDate lastView) {
        this.name = name;
        this.lastView = lastView;
        this.fileLink = fileLink;
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
}
