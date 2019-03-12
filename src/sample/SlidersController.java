package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.TextAlignment;

public class SlidersController {
    @FXML
    private Slider brightnessThresholdSlider, noiseSlider, blueSlider;
    @FXML
    private Label brightnessThresholdLabel, greenValueLabel, blueValueLabel;


    private Controller sourceController;

    public void setSourceController(Controller sourceController) {
        this.sourceController = sourceController;
    }

    public void addSliderListeners(){
        Label[] labels = {brightnessThresholdLabel, greenValueLabel, blueValueLabel};
        for(Label l: labels){
            l.setMinWidth(40);
            l.setTextAlignment(TextAlignment.RIGHT);
            l.setText("100");
        }

        brightnessThresholdSlider.valueProperty().addListener((observable, wasChanging, isChanging)-> brightnessThresholdLabel.setText(String.valueOf((brightnessThresholdSlider.valueProperty().intValue()))));
        brightnessThresholdSlider.valueChangingProperty().addListener((observableValue, wasChanging, isChanging) -> {
            if(!isChanging) sourceController.setModifiedImage(new ActionEvent());
        });

        noiseSlider.valueProperty().addListener((e-> greenValueLabel.setText(String.valueOf(noiseSlider.valueProperty().intValue()))));

        blueSlider.valueProperty().addListener((e-> blueValueLabel.setText(String.valueOf(blueSlider.valueProperty().intValue()))));
    }

    public Slider getBrightnessThresholdSlider() {
        return brightnessThresholdSlider;
    }

    public Slider getNoiseSlider() {
        return noiseSlider;
    }

    public Slider getBlueSlider() {
        return blueSlider;
    }

    public void initSliderValues(double brightness, double noise) {
        brightnessThresholdSlider.setValue(brightness);
        noiseSlider.setValue(noise);
    }
}
