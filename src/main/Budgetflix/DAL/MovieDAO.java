package Budgetflix.DAL;

import Budgetflix.BE.Genre;
import Budgetflix.BE.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Budgetflix.DAL.Tools.*;

public class MovieDAO {
    /**
     * Creates a Movie based on the contents of the columns in the database and adds all these Movies to a list.
     * @return List of all movies.
     */
    public List<Movie> getAllMovies(){
        ArrayList<Movie> allMovies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (ResultSet rs = executeSQLQueryWithResult(sql)){
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

    /**
     * Gets a single movie from the database based on the movie id.
     * @return Movie
     */
    public Movie getMovie(int id){
        String sql = "SELECT * FROM Movies WHERE id = " + id;
        try (ResultSet rs = executeSQLQueryWithResult(sql)){
            rs.next();
            return createMovieFromDatabase(rs, id);
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a movie to the database and calls the addGenresToMovie method
     * @return
     */
    public String addMovie(Movie movie){
        Date lastView = java.sql.Date.valueOf(movie.getLastView());
        String sql = "INSERT INTO Movies (movieName, fileLink, moviePoster, lastView, IMDBrating, userRating) " +
                "VALUES ('" + validateStringForSQL(movie.getName()) + "' , '"
                + validateStringForSQL(movie.getFileLink()) + "' , + '"
                + validateStringForSQL(movie.getMoviePoster()) + "' , + '"
                + lastView + "' , + '"
                + movie.getImdbRating() + "', '"
                + movie.getUserRating() + "' )" + ";";
        try {
            executeSQLQuery(sql);
            addGenresToMovie(movie);
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
     * Updates the contents of a Movies column in the database as well as updating the genres attributed to the movie
     * @param movie
     * @return
     */
    public String editMovie(Movie movie){
        String sql = "UPDATE Movies SET movieName = '" + validateStringForSQL(movie.getName()) + "', "
                + "fileLink = '" + validateStringForSQL(movie.getFileLink()) + "', "
                + "moviePoster = '" + validateStringForSQL(movie.getMoviePoster()) + "', "
                + "lastView = '" + java.sql.Date.valueOf(movie.getLastView()) + "', "
                + "IMDBrating = '" + movie.getImdbRating() + "', "
                + "userRating = '" + movie.getUserRating() + "' "
                + "WHERE id = " + movie.getId() + ";"
                + "DELETE FROM MovieGenreLink WHERE movieId = " + movie.getId();
        try {
            executeSQLQuery(sql);
            addGenresToMovie(movie);
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
     * Deletes a Movie from both the MoviesGenreLink and the Movies tables.
     * @param movie
     */
    public void deleteMovie(Movie movie){
        int id = movie.getId();
        String sql = "DELETE FROM MovieGenreLink WHERE movieId = " + id + ";"
                + "DELETE FROM Movies WHERE id = " + id;
        try {
            executeSQLQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes multiple Movies from both the MoviesGenreLink and the Movies tables.
     * @param movies
     *
    public void deleteMovies(List<Movie> movies){
        for (Movie movie: movies){
            int id = movie.getId();
            String sql = String.join(",","DELETE FROM MovieGenreLink WHERE movieId = " + id + ";")
                    + String.join(",","DELETE FROM Movies WHERE id = " + id);
            try {
                executeSQLQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }*/
    //TODO make this work with a single query
    public void deleteMovies(List<Movie> movies){
        List<Integer> movieIds = new ArrayList<>();

        for (Movie movie: movies) {
            movieIds.add(movie.getId());
        }

        String idList = movieIds.toString().substring(1, movieIds.toString().length()-1);

        String sql = "DELETE FROM MovieGenreLink WHERE movieID IN (" + idList + ");" +
                   "DELETE FROM Movies WHERE id IN (" + idList + ");";
            try {
                executeSQLQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }


    /**
     * Gets a list of all genres linked to a specific Movie.
     * @param movieId
     * @return
     */
    public List<Genre> getAllGenresFromMovie(int movieId){
        String sql = "SELECT * FROM MovieGenreLink WHERE movieId = " + movieId;
        List<Genre> genres = new ArrayList<>();
        GenreDAO genreDAO = new GenreDAO();
        try (ResultSet rs = executeSQLQueryWithResult(sql)){
            while (rs.next()){
                int genreId = rs.getInt("genreId");
                genres.add(genreDAO.getGenre(genreId));
            }
            return genres;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds a link between a genre and a specific movie in the MoviesGenreLink table.
     * @param movie
     */
    public void addGenresToMovie(Movie movie){
        List<Integer> genreIds = new ArrayList<>();
        for (Genre genre: movie.getGenres()){
            genreIds.add(genre.getId());
        }

        int movieId;
        String sql = "SELECT * FROM Movies WHERE fileLink = '" + movie.getFileLink() + "'";
        try (ResultSet resultSet = executeSQLQueryWithResult(sql)){
            resultSet.next();
            movieId = resultSet.getInt("id");

            //Creates a string of all values to be inserted
            if (!genreIds.isEmpty()) {
                StringBuilder genreValues = new StringBuilder("(");
                for (Integer genreId : genreIds) {
                    genreValues.append(movieId).append(", ").append(genreId).append(")").append(", (");
                }
                genreValues = new StringBuilder(genreValues.substring(0, genreValues.length() - 3));
                sql = "INSERT INTO MovieGenreLink (movieId, genreId) VALUES " + genreValues;
                executeSQLQuery(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a Movie from the contents of the columns in the Movie table
     * @return Movie
     * @throws SQLException
     */
    private Movie createMovieFromDatabase(ResultSet rs, int id) throws SQLException {
        String movieName = rs.getString("movieName");
        String fileLink = rs.getString("fileLink");
        String moviePoster = rs.getString("moviePoster");
        LocalDate lastView = rs.getDate("lastView").toLocalDate();
        double imdbRating = rs.getDouble("IMDBrating");
        double userRating = rs.getDouble("userRating");
        List<Genre> genres = getAllGenresFromMovie(id);
        return new Movie(id, movieName, fileLink, moviePoster, lastView, imdbRating, userRating, genres);
    }
}
