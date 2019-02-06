package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;

public class SlidersController {
    @FXML
    private Slider brightnessThresholdSlider, greenSlider, blueSlider;
    @FXML
    private Label brightnessThresholdLabel, greenValueLabel, blueValueLabel;


    private Controller sourceController;

    public void setSourceController(Controller sourceController) {
        this.sourceController = sourceController;
    }

    public void addSliderListeners(){
        Label[] labels = {brightnessThresholdLabel, greenValueLabel, blueValueLabel}; //, brightnessValueLabel, saturationValueLabel, contrastValueLabel
        for(Label l: labels){
            l.setMinWidth(40);
            l.setTextAlignment(TextAlignment.RIGHT);
            l.setText("100");
        }

        brightnessThresholdSlider.valueProperty().addListener((observable, wasChanging, isChanging)-> brightnessThresholdLabel.setText(String.valueOf((brightnessThresholdSlider.valueProperty().intValue()))));
        brightnessThresholdSlider.valueChangingProperty().addListener((observableValue, wasChanging, isChanging) -> {
            if(!isChanging) sourceController.setModifiedImage(new ActionEvent());
        });

        greenSlider.valueProperty().addListener((e-> greenValueLabel.setText(String.valueOf(greenSlider.valueProperty().intValue()))));

        blueSlider.valueProperty().addListener((e-> blueValueLabel.setText(String.valueOf(blueSlider.valueProperty().intValue()))));
    }

    public Slider getBrightnessThresholdSlider() {
        return brightnessThresholdSlider;
    }

    public Slider getGreenSlider() {
        return greenSlider;
    }

    public Slider getBlueSlider() {
        return blueSlider;
    }
}
