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
import java.util.ArrayList;

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

//    private ArrayList<ArrayList<Pixel>> sets = new ArrayList<>();
    int[][][] sets; //4.5 GB ? No thanks

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
        sets = new int[(int)imageLoaded.getHeight()*(int)imageLoaded.getWidth()][(int)imageLoaded.getHeight()*(int)imageLoaded.getWidth()][3];
        mainImageView.setImage(imageLoaded);
    }

    @FXML
    private void closeApp(ActionEvent actionEvent) {

    }

    //////////////////////////////////////////////// Sets stuff
    private int getNumberOfSets(Image image){
        int setIndex = 0;
        int pixelIndex = 0;
        boolean previousNeighbour = false;
        PixelReader r = image.getPixelReader();

        for (int y = 0; y < image.getHeight(); y++){
            for (int x = 0; x < image.getWidth(); x++){ //TODO: Would left, left-up, up suffice? Check for set - edge cases ?
                if (imgProc.isColorOverThreshold(r.getColor(x, y))){
                    sets[setIndex][pixelIndex][0] = 0;
                    sets[setIndex][pixelIndex][1] = x;
                    sets[setIndex][pixelIndex][2] = y;
                    pixelIndex++;
                }
                else setIndex++; //TODO: No! Not per line...
            }
            System.out.println("Line: " + y);
        }
        return setIndex;
    }

//    private ArrayList<Pixel> getSetContainingPixel(Pixel pixel){
//        for(ArrayList<Pixel> set : sets){
//            if(set.contains(pixel)){
//                return set;
//            }
//        }
//        ArrayList<Pixel> newSet = new ArrayList<>();
//        sets.add(newSet);
//        return newSet;
//    }
//
//    private ArrayList<Pixel> getNeighbourSet(Image image, PixelReader r, int i, int j) {
//        return r.getColor(clamp(i-1, 0, (int)image.getWidth()), clamp(j, 0 , (int)image.getHeight())).equals(Color.BLACK) ? getSetContainingPixel(new Pixel(i-1, j)) :
//                r.getColor(clamp(i-1, 0, (int)image.getWidth()), clamp(j-1, 0 , (int)image.getHeight())).equals(Color.BLACK) ? getSetContainingPixel(new Pixel(i-1, j-1)) :
//                        r.getColor(clamp(i, 0, (int)image.getWidth()), clamp(j-1, 0 , (int)image.getHeight())).equals(Color.BLACK) ? getSetContainingPixel(new Pixel(i, j-1)) :
//                                r.getColor(clamp(i+1, 0, (int)image.getWidth()), clamp(j-1, 0 , (int)image.getHeight())).equals(Color.BLACK) ? getSetContainingPixel(new Pixel(i+1, j-1)) :
//                                        getSetContainingPixel(new Pixel(i, j));
//
//    }

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
        System.out.println(getNumberOfSets(imageLoaded));
        mainImageView.setImage(imgProc.drawBounds(sets));
    }
}
