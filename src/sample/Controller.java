package sample;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private CheckMenuItem bnwMenuItem;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private FlowPane imagePane;
    @FXML
    private ImageView mainImageView;
    @FXML
    private BorderPane mainBorderPane;

    private Stage mainStage;
    private File selectedFile;
    private ImageProcessor imgProc;

    @FXML
    private void openImageFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp"));

        selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile == null) return;
        imgProc = new ImageProcessor(new Image(selectedFile.toURI().toString()));
        resetMenuTicks();
        mainImageView.setImage(imgProc.getImage());
    }

    @FXML
    private void closeApp(ActionEvent actionEvent) {

    }

    public void setImageResizable() {
        mainImageView.fitWidthProperty().bind(imageScrollPane.widthProperty());
        mainImageView.fitHeightProperty().bind(imageScrollPane.heightProperty());
    }

    @FXML
    private void revertImgToOriginal(ActionEvent actionEvent) {
        if(imgProc == null) return;
        mainImageView.setImage(imgProc.getImage());
        resetMenuTicks();
    }

    private void resetMenuTicks() {
        bnwMenuItem.setSelected(false);
    }

    public void setModifiedImage(ActionEvent actionEvent) {
        if(imgProc == null) return;
        Image imageToShow = bnwMenuItem.isSelected() ? imgProc.getBnWImage() : imgProc.getImage();
        if(imageToShow != null) mainImageView.setImage(imageToShow);
    }

    @FXML
    private void viewSliders(ActionEvent actionEvent) {
        if(imgProc == null) return; //TODO: Popup warning no image loaded?
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sliders.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        SlidersController slidersController = loader.getController();

        imgProc.bindBrightnessSlider(slidersController.getBrightnessThresholdSlider().valueProperty());
        imgProc.bindNoiseSlider(slidersController.getNoiseSlider().valueProperty());

        Stage sliderStage = new Stage();
        sliderStage.setTitle("Sliders?");
        sliderStage.setX(mainStage.getX()+mainStage.getWidth());
        sliderStage.setY(mainStage.getY());
        sliderStage.setScene(new Scene(root, 300, 75));
        slidersController.addSliderListeners();
        slidersController.initSliderValues(50, 0);
        slidersController.setSourceController(this);
        sliderStage.show();
    }

    public void test(ActionEvent actionEvent) {
        if(imgProc == null) return;
        System.out.println("Birds found: " + imgProc.findBirds());
        mainImageView.setImage(imgProc.drawBounds());
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}
