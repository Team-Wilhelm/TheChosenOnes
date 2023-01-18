package Budgetflix.GUI.controller;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Slider;

import java.util.Locale;

public class BudgetMother {

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

}
