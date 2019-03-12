package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

public class SlidersController {
    @FXML
    private ImageView bnwImgView;
    @FXML
    private Slider brightnessThresholdSlider, noiseSlider, blueSlider;
    @FXML
    private Label brightnessThresholdLabel, noiseFilterLevel;

    private Image previewImage;
    private ImageProcessor imgProc;
    private Controller sourceController;

    public void setSourceController(Controller sourceController) {
        this.sourceController = sourceController;
    }

    public void addSliderListeners(){
        Label[] labels = {brightnessThresholdLabel, noiseFilterLevel};
        for(Label l: labels){
            l.setMinWidth(40);
            l.setTextAlignment(TextAlignment.RIGHT);
            l.setText("100");
        }

        brightnessThresholdSlider.valueProperty().addListener((observable, wasChanging, isChanging)-> brightnessThresholdLabel.setText(String.valueOf((brightnessThresholdSlider.valueProperty().intValue()))));
        brightnessThresholdSlider.valueChangingProperty().addListener((observableValue, wasChanging, isChanging) -> {
//            if(!isChanging) sourceController.setModifiedImage(new ActionEvent());
            if(!isChanging) bnwImgView.setImage(imgProc.getBnWImage());
        });

        noiseSlider.valueProperty().addListener((e-> noiseFilterLevel.setText(String.valueOf(noiseSlider.valueProperty().intValue()))));
    }

    public Slider getBrightnessThresholdSlider() {
        return brightnessThresholdSlider;
    }

    public Slider getNoiseSlider() {
        return noiseSlider;
    }

    public void initSliderValues(double brightness, double noise) {
        brightnessThresholdSlider.setValue(brightness);
        noiseSlider.setValue(noise);
    }

    public void setPreviewImage(ImageProcessor imgProc){
        this.imgProc = imgProc;
        bnwImgView.setImage(imgProc.getBnWImage());
    }
}
