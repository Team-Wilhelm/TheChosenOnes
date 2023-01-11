import BE.Genre;
import BE.Movie;
import BLL.LogicManager;
import DAL.GenreDAO;
import DAL.MovieDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 *
 * @author The Chosen Ones
 */
public class BudgetFlix extends Application {

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(BudgetFlix.class.getResource("GUI/view/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("BudgetFlix");
        stage.centerOnScreen();
        stage.show();
    }

    public static void main (String[] args) {
        launch();
        //test();
    }

    private static void test(){
        MovieDAO movieDAO = new MovieDAO();
        GenreDAO genreDAO = new GenreDAO();
        for (Movie m : genreDAO.getMoviesInGenre(genreDAO.getGenre(2))){
            System.out.println(m.getGenres());
        }
        System.out.println(movieDAO.getMovie(59).getName());
        System.out.println(genreDAO.getGenre(1).getName());
    }
}
