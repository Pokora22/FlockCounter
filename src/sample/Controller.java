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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private CheckMenuItem bnwMenuItem, redChannelMenuItem, greenChannelMenuItem, blueChannelMenuItem;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private FlowPane imagePane;
    @FXML
    private ImageView mainImageView;
    @FXML
    private BorderPane mainBorderPane;

    private SimpleDoubleProperty brightnessThreshold = new SimpleDoubleProperty(100);
    private SimpleDoubleProperty greenChannel = new SimpleDoubleProperty(100);
    private SimpleDoubleProperty blueChannel = new SimpleDoubleProperty(100);

    private Stage mainStage;
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private Image imageLoaded;
    private File selectedFile;

    @FXML
    private void openImageFile(ActionEvent actionEvent) {


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp"));

        selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            try {
                System.out.println(selectedFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageLoaded = new Image(selectedFile.toURI().toString());
        }

        resetMenuTicks();
        mainImageView.setImage(imageLoaded);
    }

    @FXML
    private void closeApp(ActionEvent actionEvent) {

    }

    private void getNearbySet(Image image){
        boolean previousNeighbour = false;
        PixelReader r = image.getPixelReader();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) { //TODO: Would left, left-up, up suffice? Check for set - edge cases ?
                boolean foundNeighbour = (r.getColor(clamp(i-1, 0, (int)image.getWidth()), clamp(j, 0 , (int)image.getHeight())).equals(Color.BLACK) || //LEFT
                        r.getColor(clamp(i-1, 0, (int)image.getWidth()), clamp(j-1, 0 , (int)image.getHeight())).equals(Color.BLACK) || //LEFT-UP
                        r.getColor(clamp(i, 0, (int)image.getWidth()), clamp(j-1, 0 , (int)image.getHeight())).equals(Color.BLACK) || //UP
                        r.getColor(clamp(i+1, 0, (int)image.getWidth()), clamp(j-1, 0 , (int)image.getHeight())).equals(Color.BLACK) || // UP-RIGHT
                        r.getColor(clamp(i+1, 0, (int)image.getWidth()), clamp(j, 0 , (int)image.getHeight())).equals(Color.BLACK) || //RIGHT
                        r.getColor(clamp(i+1, 0, (int)image.getWidth()), clamp(j+1, 0 , (int)image.getHeight())).equals(Color.BLACK) || //DOWN-RIGHT
                        r.getColor(clamp(i, 0, (int)image.getWidth()), clamp(j+1, 0 , (int)image.getHeight())).equals(Color.BLACK) || //DOWN
                        r.getColor(clamp(i-1, 0, (int)image.getWidth()), clamp(j+1, 0 , (int)image.getHeight())).equals(Color.BLACK)); //LEFT-DOWN
            }
        }
    }

    private int clamp(int integer, int min, int max){
        return integer < min ? 0 : integer > max ? max : integer;
    }

    private Image getBnWImage(){
        if(imageLoaded == null) return null;
        PixelReader pixelReader = imageLoaded.getPixelReader();
        WritableImage writableImage = new WritableImage((int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for(int i = 0; i < writableImage.getWidth(); i++){
            for (int j = 0; j < writableImage.getHeight(); j++){
                pixelWriter.setColor(i, j, isColorOverThreshold(pixelReader.getColor(i,j)) ? Color.WHITE : Color.BLACK);
            }
        }

        return writableImage;
    }

    private boolean isColorOverThreshold(Color color){
//        System.out.println(((color.getBlue() + color.getGreen() + color.getRed())/3));
        return ((color.getBlue() + color.getGreen() + color.getRed())/3) > brightnessThreshold.getValue()/100f;
    }

    public void setImageResizable() {
        mainImageView.fitWidthProperty().bind(imageScrollPane.widthProperty());
        mainImageView.fitHeightProperty().bind(imageScrollPane.heightProperty());
    }

    @FXML
    private void revertImgToOriginal(ActionEvent actionEvent) {
        mainImageView.setImage(imageLoaded);
        resetMenuTicks();
    }

    private void resetMenuTicks() {
        redChannelMenuItem.setSelected(false);
        greenChannelMenuItem.setSelected(false);
        blueChannelMenuItem.setSelected(false);
    }

    public void setModifiedImage(ActionEvent actionEvent) {
        Image imageToShow = bnwMenuItem.isSelected() ? getBnWImage() : imageLoaded;
        if(imageToShow != null) mainImageView.setImage(imageToShow);
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

        brightnessThreshold.bind(controller.getBrightnessThresholdSlider().valueProperty());
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
}
