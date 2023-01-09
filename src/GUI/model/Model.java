package GUI.model;

import BE.Movie;

public class Model {
    Movie movieToEdit;
    public void editMovie(Movie movie) {
    }

    public void createMovie(Movie movie) {
    }

    public void setMovieToEdit(Movie movie){
        this.movieToEdit = movie;
    }

    public Movie getMovieToEdit() {
        return movieToEdit;
    }
}
