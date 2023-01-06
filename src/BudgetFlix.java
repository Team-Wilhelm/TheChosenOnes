import BE.Movie;
import DAL.MovieDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author The Chosen Ones
 */
public class BudgetFlix extends Application {

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("GUI/view/MainView.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("BudgetFlix");
        stage.centerOnScreen();
        stage.show();
    }


    public static void main (String[] args) {
        //launch();
        MovieDAO movieDAO = new MovieDAO();
        movieDAO.editMovie(new Movie(1, "Cats", "Nope", LocalDate.of(1477, 12, 31)));
        for (Movie m : movieDAO.getAllMovies()) {
            System.out.println(m.getName());
        }

    }
}
