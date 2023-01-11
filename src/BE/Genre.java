package BE;

import java.util.ArrayList;
import java.util.List;

public class Genre {
    private int id;
    private String name;
    private List<Movie> movies;

    public Genre(String name) {
        this.name = name;
    }

    public Genre(int id, String name) {
        this(name);
        this.id = id;
        movies = new ArrayList<>();
    }

    public Genre(List<Movie> movies){
        this.movies = movies;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Movie> getMovies(){
        return movies;
    }

    @Override
    public String toString() {
        return name;
    }
}
