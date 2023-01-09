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
    //TODO RATING
    BudgetConnection budgetConnection = new BudgetConnection();
    public List<Movie> getAllMovies(){
        ArrayList<Movie> allMovies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (Connection connection = budgetConnection.getConnection()){
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            while(rs.next()){
                int id = rs.getInt("id");
                String movieName = rs.getString("movieName");
                double rating = rs.getDouble("rating");
                String fileLink = rs.getString("fileLink");
                LocalDate lastView = rs.getDate("lastView").toLocalDate();
                allMovies.add(new Movie(id, movieName, fileLink, lastView));
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
                double rating = rs.getDouble("rating");
                String fileLink = rs.getString("fileLink");
                LocalDate lastView = rs.getDate("lastView").toLocalDate();
                movie = new Movie(id, movieName, fileLink, lastView);
            }
            return movie;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void addMovie(Movie movie){
        Date lastView = java.sql.Date.valueOf(movie.getLastView());
        String sql = "INSERT INTO Movies (movieName, fileLink, lastView) " +
                "VALUES ('" + validateStringForSQL(movie.getName()) + "' , '"
                + validateStringForSQL(movie.getFileLink()) + "' , + '"
                + lastView + "' )";
        try (Connection connection = budgetConnection.getConnection()){
            connection.createStatement().execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editMovie(Movie movie){
        String sql = "UPDATE Movies SET movieName = '" + validateStringForSQL(movie.getName()) + "', "
                + "fileLink = '" + validateStringForSQL(movie.getFileLink()) + "', "
                + "lastView = '" + java.sql.Date.valueOf(movie.getLastView()) + "' "
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
}
