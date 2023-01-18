package Budgetflix.DAL;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Budgetflix.DAL.Tools.*;

public class GenreDAO {
    List<Genre> genreList;

    /**
     * Creates a Genre based on the contents of the columns in the database and adds all these Genres to a list.
     * @return List of all genres.
     */
    public List<Genre> getAllGenres() {
        genreList = new ArrayList<>();
        try (ResultSet rs = executeSQLQueryWithResult("SELECT id, genreName FROM Genre ODER ORDER BY genreName")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("genreName");
                Genre genre = new Genre(id, name);
                genreList.add(genre);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return genreList;
    }

    /**
     * Adds a Genre to the Genre table in the database.
     * @return
     */
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

    /**
     * Deletes a Genre from all tables in the database.
     * @param genre
     */
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

    /**
     * @param genreId
     * @return Genre based on contents of a specific column in the Genre table
     */
    public Genre getGenre(int genreId) {
        String sql = "SELECT * FROM Genre WHERE id = " + genreId;
        try (ResultSet rs = executeSQLQueryWithResult(sql)){
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
