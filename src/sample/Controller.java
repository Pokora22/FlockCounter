package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private CheckMenuItem grayscaleMenuItem, redChannelMenuItem, greenChannelMenuItem, blueChannelMenuItem;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private FlowPane imagePane;
    @FXML
    private ImageView mainImageView;
    @FXML
    private BorderPane mainBorderPane;

    private SimpleDoubleProperty redChannel = new SimpleDoubleProperty(100);
    private SimpleDoubleProperty greenChannel = new SimpleDoubleProperty(100);
    private SimpleDoubleProperty blueChannel = new SimpleDoubleProperty(100);

    private Stage mainStage;
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private Image imageLoaded;

    public void openImageFile(ActionEvent actionEvent) {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp"));

        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            imageLoaded = new Image(selectedFile.toURI().toString());
        }

        mainImageView.setImage(imageLoaded);
    }

    public void closeApp(ActionEvent actionEvent) {

    }

    @FXML
    private void printAbout(ActionEvent actionEvent) {
        File fileQuestioned = imageLoaded != null? new File(imageLoaded.getUrl()) : null;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Image Information");
        if (fileQuestioned == null){
            alert.setHeaderText("");
            alert.setContentText("No Image Loaded!");
        }
        else{
            alert.setHeaderText(fileQuestioned.getName());
            alert.setContentText("Resolution " + imageLoaded.getWidth() + " x " + imageLoaded.getHeight());
        }

        alert.showAndWait();
    }

    private Image getModifiedImage(boolean red, boolean green, boolean blue, boolean gray){
        if(imageLoaded == null) return null;
        PixelReader pixelReader = imageLoaded.getPixelReader();
        WritableImage writableImage = new WritableImage((int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        Color color;

        for (int i = 0; i < writableImage.getWidth(); i++) {
            for (int j = 0; j < writableImage.getHeight(); j++) {
                double r = red ? pixelReader.getColor(i, j).getRed() : 0;
                double g = green ? pixelReader.getColor(i, j).getGreen() : 0;
                double b = blue ? pixelReader.getColor(i, j).getBlue() : 0;
                double a = pixelReader.getColor(i, j).getOpacity();

                color = gray ? pixelReader.getColor(i, j).grayscale() :
                        (!red && !green && !blue) ? pixelReader.getColor(i,j) : new Color(r, g, b, a);
                pixelWriter.setColor(i, j, color);
            }
        }

        return writableImage;
    }

    public void setImageResizable() {

        mainImageView.fitWidthProperty().bind(imageScrollPane.widthProperty());
        mainImageView.fitHeightProperty().bind(imageScrollPane.heightProperty());
    }

    @FXML
    private void revertImgToOriginal(ActionEvent actionEvent) {
        mainImageView.setImage(imageLoaded);
        redChannelMenuItem.setSelected(false);
        greenChannelMenuItem.setSelected(false);
        blueChannelMenuItem.setSelected(false);
    }

    public void sliderChanged(){
        if (imageLoaded == null) return;
        PixelReader pixelReader = imageLoaded.getPixelReader();
        WritableImage writableImage = new WritableImage((int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < writableImage.getWidth(); i++) {
            for (int j = 0; j < writableImage.getHeight(); j++) {
                double red = pixelReader.getColor(i, j).getRed()*(redChannel.getValue()/100);
                double green = pixelReader.getColor(i, j).getGreen()*(greenChannel.getValue()/100);
                double blue = pixelReader.getColor(i, j).getBlue()*(blueChannel.getValue()/100);

                pixelWriter.setColor(i, j, new Color(red, green, blue, 1));
            }
        }
        mainImageView.setImage(writableImage);
    }

    @FXML
    private void setModifiedImage(ActionEvent actionEvent) {
        double timerStart = System.nanoTime();
        Image imageToShow = getModifiedImage(redChannelMenuItem.isSelected(), greenChannelMenuItem.isSelected(), blueChannelMenuItem.isSelected(), grayscaleMenuItem.isSelected());
        if(imageToShow != null) mainImageView.setImage(imageToShow);
        System.out.println(System.nanoTime() - timerStart);
    }

    @FXML
    private void viewSliders(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sliders.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        SlidersController controller = loader.getController();

        redChannel.bind(controller.getRedSlider().valueProperty());
        greenChannel.bind(controller.getGreenSlider().valueProperty());
        blueChannel.bind(controller.getBlueSlider().valueProperty());

        Stage sliderStage = new Stage();
        sliderStage.setTitle("Sliders?");
        sliderStage.setX(mainStage.getX()+mainStage.getWidth());
        sliderStage.setY(mainStage.getY());
        sliderStage.setScene(new Scene(root, 300, 75));
        controller.addSliderListeners();
        controller.setSourceController(this);
        sliderStage.show();
    }

    @FXML
    private void viewChannels(ActionEvent actionEvent) {
    }
}
