package Budgetflix.DAL;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Budgetflix.DAL.Tools.*;

public class GenreDAO {
    //TODO interfaces
    List<Genre> genreList;
    List<Movie> moviesInGenre;

    public List<Genre> getAllGenres() {
        genreList = new ArrayList<>();
        try (ResultSet rs = executeSQLQueryWithResult("SELECT id, genreName FROM Genre ODER ORDER BY genreName")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("genreName");
                Genre genre = new Genre(id, name);
                genre.getMovies().addAll(getMoviesInGenre(genre));
                genreList.add(genre);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return genreList;
    }

    public String addGenreToDatabase(String name) {
        String sql = "INSERT INTO Genre (genreName) VALUES ('" + validateStringForSQL(name) + "')";
        try  {
            executeSQLQuery(sql);
        } catch (SQLException e) {
            if (e.getMessage().contains("Violation of UNIQUE KEY constraint")){
                return e.getMessage();
            }
            else
                e.printStackTrace();
        }
        return "";
    }

    public void deleteGenre(Genre genre){
        int id = genre.getId();
        String sql = "DELETE FROM MovieGenreLink WHERE genreId = " + id + ";"
                + "DELETE FROM Genre WHERE id = " + id;
        try {
            executeSQLQuery(sql);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<Movie> getMoviesInGenre(Genre genre) {
        int genreId = genre.getId();
        moviesInGenre = new ArrayList<>();
        String sql = "SELECT movieID FROM MovieGenreLink WHERE genreId='" + genreId + "';";
        try (ResultSet rs = executeSQLQueryWithResult(sql)) {
            MovieDAO movieDAO = new MovieDAO();
                while (rs.next()) {
                    int id = rs.getInt("movieId");
                    moviesInGenre.add(movieDAO.getMovie(id));
                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return moviesInGenre;
    }

    public Genre getGenre(int genreId) {
        String sql = "SELECT * FROM Genre WHERE id = " + genreId;
        try {
            ResultSet rs = executeSQLQueryWithResult(sql);
            rs.next();
            int id = rs.getInt("id");
            String name = rs.getString("genreName");
            return new Genre(id, name);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
