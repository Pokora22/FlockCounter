package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.TextAlignment;

public class SlidersController {
    @FXML
    private Slider redSlider, greenSlider, blueSlider, brightnessSlider, contrastSlider, saturationSlider;
    @FXML
    private Label redValueLabel, greenValueLabel, blueValueLabel, brightnessValueLabel, contrastValueLabel, saturationValueLabel;

    private Controller sourceController;

    SlidersController(Controller sourceController){
        this.sourceController = sourceController;
    }


    //TODO: Try constructor load?


//    public void setSourceController(Controller sourceController) {
//        this.sourceController = sourceController;
//    }

    public void addSliderListeners(){
        Label[] labels = {redValueLabel, greenValueLabel, blueValueLabel}; //, brightnessValueLabel, saturationValueLabel, contrastValueLabel
        for(Label l: labels){
            l.setMinWidth(40);
            l.setTextAlignment(TextAlignment.RIGHT);
            l.setText("100");
        }

        redSlider.valueProperty().addListener(e->{
            redValueLabel.setText(String.valueOf((redSlider.valueProperty().intValue())));
            sourceController.sliderChanged();
        });

        greenSlider.valueProperty().addListener((e->{
            greenValueLabel.setText(String.valueOf(greenSlider.valueProperty().intValue()));
            sourceController.sliderChanged();
        }));

        blueSlider.valueProperty().addListener((e->{
            blueValueLabel.setText(String.valueOf(blueSlider.valueProperty().intValue()));
            sourceController.sliderChanged();
        }));
    }

    public Slider getRedSlider() {
        return redSlider;
    }

    public Slider getGreenSlider() {
        return greenSlider;
    }

    public Slider getBlueSlider() {
        return blueSlider;
    }
}
