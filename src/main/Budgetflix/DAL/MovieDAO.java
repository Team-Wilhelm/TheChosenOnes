package Budgetflix.DAL;

import Budgetflix.BE.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static Budgetflix.DAL.Tools.*;

public class MovieDAO {
    /**
     * Creates a Movie based on the contents of the columns in the database and adds all these Movies to a list.
     * Simultaneously calls the getAllGenresFromMovie method, which populates the list of Genres in a Movie BE
     * @return List of all movies.
     */
    public List<Movie> getAllMovies(){
        ArrayList<Movie> allMovies = new ArrayList<>();
        List<Integer> movieIds = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        try (ResultSet rs = executeSQLQueryWithResult(sql)){
            while(rs.next()){
                int movieID = rs.getInt("id");
                movieIds.add(movieID);
                allMovies.add(createMovieFromDatabase(rs, movieID));
            }
            getAllGenresFromMovie(allMovies);
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
     * Adds a movie to the database and calls the addGenresToMovie method,
     * which links the movie with its genres in the linking table
     * @return An empty string, if no exception has been thrown,
     * otherwise, return an exception message if the movie filepath is already in the database
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
     * @return An empty string, if no exception has been thrown,
     * otherwise, return an exception message if the movie filepath is already in the database
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
     */
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
     */
    public void getAllGenresFromMovie(List<Movie> movies){
        String sql = "SELECT * FROM MovieGenreLink;";
        GenreDAO genreDAO = new GenreDAO();
        List<Genre> genres = genreDAO.getAllGenres();

        // Looping through all the rows in connection table to add the Genres to each Movie
        try (ResultSet rs = executeSQLQueryWithResult(sql)){
            while (rs.next()){
                //Get the movie from already loaded movies.
                Movie selMovie = movies.stream()
                        .filter(movie -> { //Selects the movie where the current row movieId equals to Id of a movie
                            try {
                                return movie.getId() == rs.getInt("movieId"); //compare the current movie's id to the id in the database
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst() //Even though the movieId is unique we need to select the first Movie, because the current output is list and not a single element.
                        .get();
                //Get the genre from already loaded genres.
                Genre selGenre = genres.stream()
                        .filter(genre -> {
                            try {
                                return genre.getId() == rs.getInt("genreId"); //compare the current genre's id to the id in the database
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .findFirst() //Even though the genreId is unique we need to select the first Genre, because the current output is list and not a single element.
                        .get();

                //add a genre to the list of genres in Movie BE
                selMovie.getGenres().add(selGenre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a link between a genre and a specific movie in the MoviesGenreLink table.
     */
    public void addGenresToMovie(Movie movie){
        int movieId;
        List<Integer> genreIds = new ArrayList<>();
        for (Genre genre: movie.getGenres()){
            genreIds.add(genre.getId());
        }

        //Gets the movie id from the database, otherwise, an issue with FOREIGN KEY CONSTRAINT arises
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
     */
    private Movie createMovieFromDatabase(ResultSet rs, int id) throws SQLException {
        String movieName = rs.getString("movieName");
        String fileLink = rs.getString("fileLink");
        String moviePoster = rs.getString("moviePoster");
        LocalDate lastView = rs.getDate("lastView").toLocalDate();
        double imdbRating = rs.getDouble("IMDBrating");
        double userRating = rs.getDouble("userRating");
        return new Movie(id, movieName, fileLink, moviePoster, lastView, imdbRating, userRating);
    }
}
