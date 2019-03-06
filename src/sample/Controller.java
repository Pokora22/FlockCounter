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
    private CheckMenuItem bnwMenuItem, redChannelMenuItem, greenChannelMenuItem, blueChannelMenuItem;
    @FXML
    private ScrollPane imageScrollPane;
    @FXML
    private FlowPane imagePane;
    @FXML
    private ImageView mainImageView;
    @FXML
    private BorderPane mainBorderPane;


    private SimpleDoubleProperty greenChannel = new SimpleDoubleProperty(100);
    private SimpleDoubleProperty blueChannel = new SimpleDoubleProperty(100);

    private Stage mainStage;
    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private Image imageLoaded;
    private File selectedFile;
    private ImageProcessor imgProc;
    private int setIndex;
    private PixelReader pixelReader;
    int[] sets;

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
        imgProc = new ImageProcessor(imageLoaded);
        sets = new int[(int)imageLoaded.getHeight()*(int)imageLoaded.getWidth()];
        setIndex = 0; //?
        mainImageView.setImage(imageLoaded);
        pixelReader = imageLoaded.getPixelReader();
    }

    @FXML
    private void closeApp(ActionEvent actionEvent) {

    }

    //////////////////////////////////////////////// Sets stuff
    private int getNumberOfSets(){ //TODO: foreach in sets where int < 0, count++; return count
        findBirds();

        int setCount = 0;
        for(int r : sets) {
            if(r < 0) setCount++;
//            System.out.println(r);
        }

        return setCount;
    }


    private void findBirds(){
        for (int y = 0; y < imageLoaded.getHeight(); y++){
            for (int x = 0; x < imageLoaded.getWidth(); x++){ //TODO: Would left, left-up, up suffice? Check for set - edge cases ?
                if (imgProc.isColorBelowThreshold(pixelReader.getColor(x, y))) sets[(y)*(int)imageLoaded.getWidth()+x] = getSetIndex(x, y);

            }
        }
    }

    private int getSetIndex(int x, int y) {
        if(x > 0 && imgProc.isColorBelowThreshold(pixelReader.getColor(x-1, y))){
//            System.out.println("Found black left");
            return y * (int)imageLoaded.getWidth() + x - 1; //Offset 0s ?
        }
        if(x > 0 && y > 0 && imgProc.isColorBelowThreshold(pixelReader.getColor(x-1, y-1))){
//            System.out.println("Found black left-top");
            return (y-1)*(int)imageLoaded.getHeight() + x - 1;
        }
        if(y > 0 && imgProc.isColorBelowThreshold(pixelReader.getColor(x, y-1))){
//            System.out.println("Found black top");
            return (y-1)*(int)imageLoaded.getHeight() + x;
        }
        if(y > 0 && x < imageLoaded.getWidth()-1 && imgProc.isColorBelowThreshold(pixelReader.getColor(x+1,y-1))) {
//            System.out.println("Found black top-left");
            return (y-1)*(int)imageLoaded.getHeight() + x + 1; //offset width in relation to array
        }

//        System.out.println("Didn't find black");
        return --setIndex;
    }

    private int clamp(int integer, int min, int max){
        max--; //Quick and dirty fix
        return integer < min ? 0 : integer > max ? max : integer;
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
        bnwMenuItem.setSelected(false);
    }

    public void setModifiedImage(ActionEvent actionEvent) {
        Image imageToShow = bnwMenuItem.isSelected() ? imgProc.getBnWImage() : imageLoaded;
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

        imgProc.bindBrightnessSlider(controller.getBrightnessThresholdSlider().valueProperty());
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

    public void test(ActionEvent actionEvent) {
        System.out.println(getNumberOfSets());
//        mainImageView.setImage(imgProc.drawBounds(sets));
    }
}
