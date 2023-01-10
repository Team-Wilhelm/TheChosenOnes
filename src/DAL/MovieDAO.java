package DAL;

import BE.Genre;
import BE.Movie;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieDAO {
    BudgetConnection budgetConnection = new BudgetConnection();

    public List<Movie> getAllMovies(){
        ArrayList<Movie> allMovies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (Connection connection = budgetConnection.getConnection()){
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                allMovies.add(createMovieFromDatabase(rs, id));
            }
            return allMovies;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Movie getMovie(int id){
        String sql = "SELECT * FROM Movies WHERE id = " + id;
        try (Connection connection = budgetConnection.getConnection()){
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            rs.next();
            return createMovieFromDatabase(rs, id);

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addMovie(Movie movie){
        Date lastView;
        if (movie.getLastView() != null)
             lastView = java.sql.Date.valueOf(movie.getLastView());
        else {
            lastView = java.sql.Date.valueOf(LocalDate.of(0,1,1)); //TODO fix this shit
        }
        String sql = "INSERT INTO Movies (movieName, fileLink, lastView, IMDBrating, userRating) " +
                "VALUES ('" + validateStringForSQL(movie.getName()) + "' , '"
                + validateStringForSQL(movie.getFileLink()) + "' , + '"
                + lastView + "' , + '"
                + movie.getImdbRating() + "', '"
                + movie.getUserRating() + "' )" + ";";
        try (Connection connection = budgetConnection.getConnection()){
            connection.prepareStatement(sql).execute();
            addGenresToMovie(movie, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editMovie(Movie movie){
        String sql = "UPDATE Movies SET movieName = '" + validateStringForSQL(movie.getName()) + "', "
                + "fileLink = '" + validateStringForSQL(movie.getFileLink()) + "', "
                + "lastView = '" + java.sql.Date.valueOf(movie.getLastView()) + "', "
                + "IMDBrating = '" + movie.getImdbRating() + "', "
                + "userRating = '" + movie.getUserRating() + "' "
                + "WHERE id = " + movie.getId();
        try (Connection connection = budgetConnection.getConnection()){
            connection.createStatement().execute(sql);
            connection.createStatement().execute("DELETE FROM MovieGenreLink WHERE movieId = " + movie.getId());
            addGenresToMovie(movie, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMovie(Movie movie){
        int id = movie.getId();
        String sql = "DELETE FROM MovieGenreLink WHERE movieId = " + id + ";"
                + "DELETE FROM Movies WHERE id = " + id;
        try (Connection connection = budgetConnection.getConnection()){
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String validateStringForSQL(String string) {
        if (string == null) return null;
        string = string.replace("'", "''");
        return string;
    }

    public List<Genre> getAllGenresFromMovie(int movieId){
        String sql = "SELECT genreId FROM MovieGenreLink WHERE movieId = " + movieId;
        List<Genre> genres = new ArrayList<>();
        try (Connection connection = budgetConnection.getConnection()){
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while (rs.next()){
                int genreId = rs.getInt("genreId");
                GenreDAO genreDAO = new GenreDAO();
                genres.add(genreDAO.getGenre(genreId));
            }
            return genres;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO make it much better (preferably)
    public void addGenresToMovie(Movie movie, Connection connection){
        List<Genre> genres = movie.getGenres();
        String sql = "SELECT * FROM Movies WHERE fileLink = '" + movie.getFileLink() + "'";
        int movieId;
        try (connection){
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            resultSet.next();
            movieId = resultSet.getInt("id");

            for (Genre genre: genres){
                sql = "SELECT * FROM Genre WHERE genreName = '" + genre.getName() + "'";
                resultSet = connection.prepareStatement(sql).executeQuery();
                while (resultSet.next()){
                    int genreId = resultSet.getInt("id");
                    sql = "INSERT INTO MovieGenreLink (movieId, genreId) VALUES (" + movieId + ", " + genreId + ")";
                    connection.createStatement().execute(sql);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Movie createMovieFromDatabase(ResultSet rs, int id) throws SQLException {
        String movieName = rs.getString("movieName");
        String fileLink = rs.getString("fileLink");
        LocalDate lastView = rs.getDate("lastView").toLocalDate();
        double imdbRating = rs.getDouble("IMDBrating");
        double userRating = rs.getDouble("userRating");
        List<Genre> genres = getAllGenresFromMovie(id);
        return new Movie(id, movieName, fileLink, lastView, imdbRating, userRating, genres);
    }
}
