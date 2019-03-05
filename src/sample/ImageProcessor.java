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

    public Image drawBounds(int[][][] sets){
        int minX = (int)imageLoaded.getWidth();
        int maxX = 0;
        int minY = (int)imageLoaded.getHeight();
        int maxY = 0;
        WritableImage writableImage = new WritableImage(imageLoaded.getPixelReader(),(int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

//        System.out.println("org img: " + imageLoaded.getWidth() + "x" + imageLoaded.getHeight());

        for (int[][] set: sets) {
            for (int[] pixel : set) {
                minX = pixel[1] < minX ? pixel[1] : minX;
                maxX = pixel[1] > maxX ? pixel[1] : maxX;

                minY = pixel[2] < minY ? pixel[2] : minY;
                maxY = pixel[2] > maxY ? pixel[2] : maxY;
            }

            for(int x = minX; x < maxX; x++) {
                pixelWriter.setColor(x, minY, Color.RED);
                pixelWriter.setColor(x, maxY, Color.RED);
            }
            for(int y = minY; y < maxY; y++){
                pixelWriter.setColor(y, minX, Color.RED);
                pixelWriter.setColor(y, maxX, Color.RED);
            }
        }

        return writableImage;
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
//        System.out.println(brightnessThreshold.getValue()/100f);
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
