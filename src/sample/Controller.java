package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane paneLabels;
    @FXML
    private CheckMenuItem menuCheckPreviewWindow;
    @FXML
    private ImageView mainImageView;

    private Stage mainStage, previewStage;
    private File selectedFile;
    private ImageProcessor imgProc;
    Scale scale;

    @FXML
    private void openImageFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp"));

        selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile == null) return;
        imgProc = new ImageProcessor(new Image(selectedFile.toURI().toString()));
        paneLabels.getChildren().clear();
        mainImageView.setImage(imgProc.getImage());
        viewPreviewWindow(actionEvent);
    }

    @FXML
    private void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void setImageResizable() {
        mainImageView.fitWidthProperty().bind(stackPane.widthProperty());
        mainImageView.fitHeightProperty().bind(stackPane.heightProperty());
        paneLabels.prefWidthProperty().bind(stackPane.widthProperty());
        paneLabels.prefHeightProperty().bind(stackPane.heightProperty());

        scale = new Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        paneLabels.getTransforms().addAll(scale);

        stackPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateLabelsScale();
        });
        stackPane.heightProperty().addListener((obs, oldVal, newVal) ->{
            updateLabelsScale();
        });
    }

    private void updateLabelsScale() {

        scale.setX(mainImageView.maxWidth(mainImageView.getFitWidth())/imgProc.getImage().getWidth());
        scale.setY(mainImageView.maxHeight(mainImageView.getFitHeight())/imgProc.getImage().getHeight());
    }

    @FXML
    private void revertImgToOriginal(ActionEvent actionEvent) {
        if(imgProc == null) return;
        paneLabels.getChildren().clear();
        mainStage.setTitle("Flock counter waiting ...");
        mainImageView.setImage(imgProc.getImage());
    }

    @FXML
    private void viewPreviewWindow(ActionEvent actionEvent) {
        if(previewStage != null) previewStage.close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("preview.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        PreviewController previewController = loader.getController();

        imgProc.bindBrightnessSlider(previewController.getBrightnessThresholdSlider().valueProperty());
        imgProc.bindNoiseSlider(previewController.getNoiseSlider().valueProperty());

        previewStage = new Stage();
        previewStage.setTitle("Process Preview");
        previewStage.setX(mainStage.getX()+mainStage.getWidth());
        previewStage.setY(mainStage.getY());
        previewStage.setScene(new Scene(root, 400, 365));
        previewController.addSliderListeners();
        previewController.initSliderValues(50, 5);
        previewController.setPreviewImage(imgProc);
        previewController.setImageResizable();
        previewStage.show();
        menuCheckPreviewWindow.setSelected(true);
    }
    

    public void countBirds(ActionEvent actionEvent) {
        if(imgProc == null) return;
        mainStage.setTitle("Flock counter found: " + imgProc.findBirds() + " birds.");
        mainImageView.setImage(imgProc.drawBounds());
        addLabels();
    }

    private void addLabels(){
        int birdNo = 1;
        paneLabels.getChildren().clear();

        for(int i : imgProc.getLabelPositions()){
            Label label = new Label();
            label.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            label.setText(String.valueOf(birdNo++));

            label.setLayoutX(imgProc.getPixelXY(i)[0]);
            label.setLayoutY(imgProc.getPixelXY(i)[1]);
            paneLabels.getChildren().add(label);
        }

        updateLabelsScale();
    }

    @FXML
    private void togglePreview(ActionEvent actionEvent) {
        if(previewStage == null) {
            menuCheckPreviewWindow.setSelected(false);
            return;
        }

        if(menuCheckPreviewWindow.isSelected()) previewStage.show();
        else previewStage.hide();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setTestStyles(){
//        imageScrollPane.setStyle(" -fx-background-color: red; -fx-border-width: 10;");
//        mainImageView.setStyle(" -fx-border-color: #ff53bd; -fx-border-width: 10; -fx-border-style: dashed;");
//        paneLabels.setStyle(" -fx-border-color: blue; -fx-border-width: 10; -fx-border-style: dotted;");
//        groupLabels.setStyle(" -fx-background-color: red; -fx-border-width: 10;");
    }

    public void guesstimate(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("My guess on number of birds based on size differences is: " + imgProc.guesstimate() + " birds.");
        alert.showAndWait();
    }
}
