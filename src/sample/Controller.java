package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    public void setImageResizable() {

        mainImageView.fitWidthProperty().bind(imageScrollPane.widthProperty());
        mainImageView.fitHeightProperty().bind(imageScrollPane.heightProperty());
    }

    @FXML
    private void revertImgToOriginal(ActionEvent actionEvent) {
        mainImageView.setImage(new WritableImage(1, 1));
    }

    @FXML
    private void setModifiedImage(ActionEvent actionEvent) {
        double timerStart = System.currentTimeMillis();

        if(imageLoaded == null) return;
        PixelReader pixelReader = imageLoaded.getPixelReader();
        WritableImage writableImage = new WritableImage((int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        Color color;

        for (int i = 0; i < writableImage.getWidth(); i++) {
            for (int j = 0; j < writableImage.getHeight(); j++) {
                double red = redChannelMenuItem.isSelected() ? pixelReader.getColor(i, j).getRed() : 0;
                double green = greenChannelMenuItem.isSelected() ? pixelReader.getColor(i, j).getGreen() : 0;
                double blue = blueChannelMenuItem.isSelected() ? pixelReader.getColor(i, j).getBlue() : 0;
                double alpha = pixelReader.getColor(i, j).getOpacity();

                color = grayscaleMenuItem.isSelected() ? pixelReader.getColor(i, j).grayscale() :
                        (!redChannelMenuItem.isSelected() && !greenChannelMenuItem.isSelected() && !blueChannelMenuItem.isSelected()) ? pixelReader.getColor(i,j) : new Color(red, green, blue, alpha);
                pixelWriter.setColor(i, j, color);
            }
        }

        mainImageView.setImage(writableImage);

        System.out.println(System.currentTimeMillis() - timerStart);
    }
}
