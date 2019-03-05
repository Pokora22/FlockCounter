package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class ImageProcessor {

    private Image imageLoaded;
    private SimpleDoubleProperty brightnessThreshold = new SimpleDoubleProperty(50);

    public ImageProcessor(Image imageLoaded){
        this.imageLoaded = imageLoaded;
    }

    public void drawBounds(ArrayList<ArrayList<Pixel>> sets){
        for(ArrayList<Pixel> set : sets){
            int x = set.get(0).getWidth();
            int y = set.get(0).getHeight();
            int width = set.get(set.size()-1).getWidth() - x;
            int height = set.get(set.size()-1).getHeight() - y;

            Rectangle boundBox = new Rectangle(x, y, width, height);
//            System.out.println("Image pane size: " + imagePane.getWidth() + ":" + imagePane.getHeight());
//            pa.getChildren().addAll(boundBox);

            System.out.println(boundBox);
        }
    }

    public Image getBnWImage(){
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

    public boolean isColorOverThreshold(Color color){
//        System.out.println(((color.getBlue() + color.getGreen() + color.getRed())/3));
        return ((color.getBlue() + color.getGreen() + color.getRed())/3) > brightnessThreshold.getValue()/100f;
    }

    public void bindBrightnessSlider(DoubleProperty prop){
        brightnessThreshold.bind(prop);
    }

    private int clamp(int integer, int min, int max){
        max--; //Quick and dirty fix
        return integer < min ? 0 : integer > max ? max : integer;
    }
}
