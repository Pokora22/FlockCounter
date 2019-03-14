package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private FlowPane paneLabels;
    @FXML
    private StackPane mainWindowStackPane;
    @FXML
    private CheckMenuItem menuCheckPreviewWindow;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private ImageView mainImageView;

    private Stage mainStage, previewStage;
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
        mainImageView.setImage(imgProc.getImage());
        viewPreviewWindow(actionEvent);
    }

    @FXML
    private void closeApp(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void setImageResizable() {
        mainImageView.fitWidthProperty().bind(imageScrollPane.widthProperty());
        mainImageView.fitHeightProperty().bind(imageScrollPane.heightProperty());
        mainWindowStackPane.prefWidthProperty().bind(imageScrollPane.widthProperty());
        mainWindowStackPane.prefHeightProperty().bind(imageScrollPane.heightProperty());
        paneLabels.prefWidthProperty().bind(imageScrollPane.widthProperty());
        paneLabels.prefHeightProperty().bind(imageScrollPane.heightProperty());
    }

    @FXML
    private void revertImgToOriginal(ActionEvent actionEvent) {
        if(imgProc == null) return;
        mainImageView.setImage(imgProc.getImage());
    }

    @FXML
    private void viewPreviewWindow(ActionEvent actionEvent) {
        if(previewStage != null) previewStage.close();
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

        previewStage = new Stage();
        previewStage.setTitle("Process Preview");
        previewStage.setX(mainStage.getX()+mainStage.getWidth());
        previewStage.setY(mainStage.getY());
        previewStage.setScene(new Scene(root, 300, 275));
        slidersController.addSliderListeners();
        slidersController.initSliderValues(50, 0);
        slidersController.setPreviewImage(imgProc);
        slidersController.setImageResizable();
        previewStage.show();
        menuCheckPreviewWindow.setSelected(true);
    }
    

    public void countBirds(ActionEvent actionEvent) {
        if(imgProc == null) return;
        System.out.println("Birds found: " + imgProc.findBirds());
        mainImageView.setImage(imgProc.drawBounds());
        addLabels();
    }

    private void addLabels(){
        int birdNo = 1;
        double xOffset, yOffset, widthOffset, heightOffset;
        paneLabels.getChildren().clear();
        xOffset = mainImageView.maxWidth(mainImageView.getFitWidth())/imgProc.getImage().getWidth();
        yOffset = mainImageView.maxHeight(mainImageView.getFitHeight())/imgProc.getImage().getHeight();
        widthOffset = (mainImageView.getFitWidth() - imgProc.getImage().getWidth() * xOffset) / 2;
        heightOffset = (mainImageView.getFitHeight() - imgProc.getImage().getHeight() * yOffset) / 2;

        //TODO: Label position is not adjusting against pane size. Try grid pane? Possible solution: https://stackoverflow.com/questions/41175632/javafx-autoresize-auto-position
        for(int i : imgProc.getLabelPositions()){
            Label label = new Label();
            label.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            label.setText(String.valueOf(birdNo++));



            label.setTranslateX((imgProc.getPixelXY(i)[0] * xOffset + widthOffset));
            System.out.println(imgProc.getPixelXY(i)[0] + ", " + (label.getTranslateX()/xOffset - widthOffset));
            label.setTranslateY(imgProc.getPixelXY(i)[1] * yOffset + heightOffset);
            paneLabels.getChildren().add(label);
        }
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
        imageScrollPane.setStyle(" -fx-background-color: red; -fx-border-width: 10;");
        mainImageView.setStyle(" -fx-border-color: #ff53bd; -fx-border-width: 10; -fx-border-style: dashed;");
        paneLabels.setStyle(" -fx-border-color: blue; -fx-border-width: 10; -fx-border-style: dotted;");
    }
}
