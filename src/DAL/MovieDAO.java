package DAL;

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
                String movieName = rs.getString("movieName");
                String fileLink = rs.getString("fileLink");
                LocalDate lastView = rs.getDate("lastView").toLocalDate();
                double imdbRating = rs.getDouble("IMDBrating");
                double userRating = rs.getDouble("userRating");
                allMovies.add(new Movie(id, movieName, fileLink, lastView, imdbRating, userRating));
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
            Movie movie = null;
            while (rs.next()){
                String movieName = rs.getString("movieName");
                String fileLink = rs.getString("fileLink");
                LocalDate lastView = rs.getDate("lastView").toLocalDate();
                double imdbRating = rs.getDouble("IMDBrating");
                double userRating = rs.getDouble("userRating");
                movie = new Movie(id, movieName, fileLink, lastView, imdbRating, userRating);
            }
            return movie;

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
                + movie.getUserRating() + "' )";
        try (Connection connection = budgetConnection.getConnection()){
            connection.createStatement().execute(sql);
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

    public void getAllGenresFromMovie(int movieId){

    }
}
