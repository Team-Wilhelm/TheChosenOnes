package Budgetflix.GUI.controller.cellFactory;

import Budgetflix.BE.Movie;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MovieListCell extends ListCell<Movie> {
    private final Label title = new Label();

    private final Label genres = new Label();
    private final HBox genreRow = new HBox(new Label("|"), genres,  new Label("|"));

    private final Label detail = new Label();
    private final Label userRatingTitle = new Label("User Rating:");
    private final Label userRatingRating = new Label();

    private final Label imdbRatingTitle = new Label("IMDB Rating:");
    private final Label imdbRatingRating = new Label();

    private final HBox otherData = new HBox(detail, new Label("|"), userRatingTitle, userRatingRating, new Label("|"), imdbRatingTitle, imdbRatingRating);
    private final VBox layout = new VBox(title, genreRow, otherData);

    public MovieListCell() {
        super();

        var titleStyle = "-fx-font-size: 24px;";
        genreRow.setStyle("-fx-font-size: 18px");
        title.setStyle(titleStyle);
        otherData.setSpacing(10);
        genreRow.setSpacing(5);
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
