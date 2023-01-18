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

public class BudgetMother {
    AlertManager alertManager = AlertManager.getInstance();
    Model model = Model.getInstance();

    private static final String SLIDER_STYLE_FORMAT =
            "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
                    + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    protected void setUpSliderColors(Slider sliderUserRating, Slider sliderIMDBRating){
        //Slider changes colour when moved
        sliderUserRating.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = (sliderUserRating.getValue() - sliderUserRating.getMin()) / (sliderUserRating.getMax() - sliderUserRating.getMin()) * 100.0 ;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        }, sliderUserRating.valueProperty(), sliderUserRating.minProperty(), sliderUserRating.maxProperty()));

        sliderIMDBRating.styleProperty().bind(Bindings.createStringBinding(() -> {
            double percentage = ( sliderIMDBRating.getValue() -  sliderIMDBRating.getMin()) / ( sliderIMDBRating.getMax() -  sliderIMDBRating.getMin()) * 100.0 ;
            return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
        },  sliderIMDBRating.valueProperty(),  sliderIMDBRating.minProperty(),  sliderIMDBRating.maxProperty()));
    }

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
