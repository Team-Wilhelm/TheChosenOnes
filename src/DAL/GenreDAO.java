package DAL;

import BE.Genre;
import BE.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {
    BudgetConnection bc = new BudgetConnection();
    List<Genre> genreList;
    List<Movie> moviesInGenre;

    public List<Genre> getAllGenres() {
        genreList = new ArrayList<>();

        try (Connection con = bc.getConnection();) {
            ResultSet rs = con.createStatement().executeQuery("SELECT id, genreName FROM Genre");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("genreName");
                genreList.add(new Genre(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return genreList;
    }

    public void addGenreToDatabase(String name) {
        String sql = "INSERT INTO Genre (genreName) VALUES (?)";

        try (Connection con = bc.getConnection();) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeGenreFromDatabase(Genre genre) {
        int id = genre.getId();
        String sql = "DELETE FROM Genre WHERE id='" + id + "';"
                + "DELETE FROM MovieGenreLink WHERE id='" + id + "';";

        try (Connection con = bc.getConnection();) {
            con.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Movie> getMoviesInGenre(Genre genre) {
        int genreId = genre.getId();
        moviesInGenre = new ArrayList<>();
        String sql = "SELECT movieID FROM MovieGenreLink WHERE genreId='" + genreId + "';";
        try (Connection con = bc.getConnection();) {
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("movieId");
                MovieDAO movieDAO = new MovieDAO();
                moviesInGenre.add(movieDAO.getMovie(id));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return moviesInGenre;
    }

    public Genre getGenre(int genreId) {
        String sql = "SELECT * FROM Genre WHERE id = " + genreId;
        try (Connection connection = bc.getConnection()) {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            rs.next();
            String name = rs.getString("genreName");
            return new Genre(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
