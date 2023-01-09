package GUI.controller.cellFactory;

import BE.Movie;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MovieListCell extends ListCell<Movie> {
    private final Label title = new Label();
    private final Label detail = new Label();
    private final VBox movieName = new VBox(title, detail);

    private final Label userRatingTitle = new Label();
    private final Label userRatingRating = new Label();
    private final VBox userRating = new VBox(userRatingRating, userRatingTitle);
    private final Label imdbRatingTitle = new Label();
    private final Label imdbRatingRating = new Label();
    private final VBox imdbRating = new VBox(imdbRatingRating, imdbRatingTitle);
    private final HBox layout = new HBox(movieName, userRating, imdbRating);

    public MovieListCell() {
        super();

        layout.setSpacing(30);

        var titleStyle = "-fx-font-size: 20px;";
        var ratingStyle = "-fx-font-size: 18px;";
        title.setStyle(titleStyle);
        imdbRatingRating.setStyle(ratingStyle);
        userRatingRating.setStyle(ratingStyle);

        var padding = "-fx-padding: 3,40,0,40;";
        userRating.setStyle(padding);
        imdbRating.setStyle(padding);
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
            title.setText(item.getName());
            detail.setText(
                item.getLastView() != null
                    ? item.getLastView().toString()
                    : "Never Watched"
            );
            userRatingTitle.setText("User Rating:");
            userRatingRating.setText(item.getUserRating()+" / 10");
            imdbRatingTitle.setText("IMDB Rating:");
            imdbRatingRating.setText(item.getImdbRating()+" / 10");
            setGraphic(layout);
        }
    }
}
