package Budgetflix;

import Budgetflix.DAL.Tools;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 *
 * @author The Chosen Ones
 */
public class BudgetFlix extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BudgetFlix.class.getResource("GUI/view/MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/budgetflixIcon.png"))));
        stage.setResizable(false);
        stage.setTitle("Budgetflix");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main (String[] args) {
        launch();
    }

    public void stop(){
        //TODO delete
        System.out.println(Tools.counter);
        Tools.closeAllConnections();
    }
}
