package Budgetflix.GUI.controller.cellFactory;

import Budgetflix.BE.Movie;
import Budgetflix.BudgetFlix;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class MovieListCell extends ListCell<Movie> {
    //TODO comments pretty pleaaaaaaaaaaaaaase
    private final Label title = new Label();

    private final Label genres = new Label();
    private final HBox genreRow = new HBox(new Label("|"), genres,  new Label("|"));

    private final Label detail = new Label();
    private final Label userRatingTitle = new Label("User Rating:");
    private final Label userRatingRating = new Label();

    private final Label imdbRatingTitle = new Label("IMDB Rating:");
    private final Label imdbRatingRating = new Label();

    private final HBox otherData = new HBox(detail, new Label("|"), userRatingTitle, userRatingRating, new Label("|"), imdbRatingTitle, imdbRatingRating);
    private final VBox textData = new VBox(title, genreRow, otherData);
    private final ImageView img = new ImageView();
    private final HBox imgData = new HBox(img);
    private final HBox layout = new HBox(imgData,textData);

    public MovieListCell() {
        super();

        var titleStyle = "-fx-font-size: 24px;";
        genreRow.setStyle("-fx-font-size: 18px");
        title.setStyle(titleStyle);
        otherData.setSpacing(10);
        genreRow.setSpacing(5);
        img.setFitHeight(99);
        img.setFitWidth(66);
        layout.setSpacing(10);

        textData.setAlignment(Pos.CENTER_LEFT);
    }

    @Override
    protected void updateItem(Movie item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);

        if (empty || item == null || item.getName() == null) {
            title.setText(null);
            detail.setText(null);
            setGraphic(null);
        } else {

            try{
                img.setImage(new Image(item.getMoviePoster()));
            }
            catch (Exception e){
                img.setImage(new Image(Objects.requireNonNull(BudgetFlix.class.getResourceAsStream("/images/bimbo.jpg"))));
            }
            genres.setText(item.getGenresToString());
            title.setText(item.getName());
            detail.setText(
                item.getLastView() != null
                    ? item.getLastView().toString()
                    : "Never Watched"
            );
            userRatingRating.setText(item.getUserRating()+" / 10");
            imdbRatingRating.setText(item.getImdbRating()+" / 10");
            setGraphic(layout);
        }
    }
}
