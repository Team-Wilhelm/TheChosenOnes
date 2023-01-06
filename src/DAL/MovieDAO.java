package DAL;

import BE.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {
    BudgetConnection budgetConnection = new BudgetConnection();
    public List<Movie> getAllMovies(){
        ArrayList<Movie> allMovies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (Connection connection = budgetConnection.getConnection()){
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
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


}
