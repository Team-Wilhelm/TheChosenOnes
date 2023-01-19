package Budgetflix.GUI.controller;

import Budgetflix.BE.Movie;
import Budgetflix.BLL.AlertManager;
import Budgetflix.GUI.model.Model;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;

import java.util.Locale;
import java.util.Optional;

/**
 * A controller with repeatedly used methods in other subclass controllers
 */
public class BudgetMother {
    AlertManager alertManager = AlertManager.getInstance();
    Model model = Model.getInstance();

    //Formatting for filling a slider with the chosen colour based on the slider value
    private static final String SLIDER_STYLE_FORMAT =
            "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
                    + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    //Sliders change colour when the thumb is moved
    protected void setUpSliderColors(Slider sliderUserRating, Slider sliderIMDBRating){
        sliderUserRating.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = (sliderUserRating.getValue() - sliderUserRating.getMin()) / (sliderUserRating.getMax() - sliderUserRating.getMin()) * 100.0 ;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        }, sliderUserRating.valueProperty(), sliderUserRating.minProperty(), sliderUserRating.maxProperty()));

        sliderIMDBRating.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = ( sliderIMDBRating.getValue() -  sliderIMDBRating.getMin()) / ( sliderIMDBRating.getMax() -  sliderIMDBRating.getMin()) * 100.0 ;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        },  sliderIMDBRating.valueProperty(),  sliderIMDBRating.minProperty(),  sliderIMDBRating.maxProperty()));
    }

    /**
     * A method used to delete a movie from the list of all movies,
     * as well as deleting movies not opened in two years and with user rating below 6
     * @param actionEvent belonging to the window, where this method was triggered
     * @param movie to delete
     */
    protected void deleteMovie(ActionEvent actionEvent, Movie movie){
        if (movie == null){
            alertManager.getAlert("ERROR", "Please, select a movie to delete!", actionEvent).showAndWait();
        }
        else{
            Alert alert = alertManager.getAlert("CONFIRMATION", "Do you really wish to delete this movie?", actionEvent);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                model.deleteMovie(movie);
            }
        }
    }

}
