package BE;

import java.time.LocalDate;

public class Movie {
    private int id;
    private String name, fileLink;
    private LocalDate lastView;
    //TODO add ratings please


    public Movie(int id, String name, LocalDate lastView, String fileLink) {
        this(name, lastView, fileLink);
        this.id = id;
    }

    public Movie(String name, LocalDate lastView, String fileLink) {
        this.name = name;
        this.lastView = lastView;
        this.fileLink = fileLink;
    }

    public String getName() {
        return name;
    }


}
