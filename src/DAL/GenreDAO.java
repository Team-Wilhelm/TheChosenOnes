package DAL;

import BE.Genre;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO{
    BudgetConnection bc = new BudgetConnection();
    List<Genre> genreList;

    public List<Genre> getAllGenres()
    {
        genreList = new ArrayList<>();

        try(Connection con = bc.getConnection();){
            ResultSet rs = con.createStatement().executeQuery("SELECT id, genreName FROM Genre");
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("genreName");
                genreList.add(new Genre(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return genreList;
    }

    public void addGenreToDatabase(String name){
        String sql = "INSERT INTO Genre (genreName) VALUES (?)";

        try(Connection con = bc.getConnection();) {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,name);
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeGenreFromDatabase(Genre genre){
        int id = genre.getId();
        String sql ="DELETE FROM Genre WHERE id='" + id + "';"+"DELETE FROM MovieGenreLink WHERE id='" + id + "';";

        try(Connection con = bc.getConnection();) {
            con.createStatement().execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
