package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageProcessor {

    private Image imageLoaded;
    private SimpleDoubleProperty brightnessThreshold = new SimpleDoubleProperty(50);
    private int i = 0;

    public ImageProcessor(Image imageLoaded){
        this.imageLoaded = imageLoaded;
    }

    public Image drawBounds(int[] sets){
        int minX = (int)imageLoaded.getWidth();
        int maxX = 0;
        int minY = (int)imageLoaded.getHeight();
        int maxY = 0;
        WritableImage writableImage = new WritableImage(imageLoaded.getPixelReader(),(int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();


        //TODO: Enclose it in same set relations somehow...
        for (int pixel: sets) {
            minX = getPixelXY(pixel)[0] < minX ? getPixelXY(pixel)[0] : minX;
            maxX = getPixelXY(pixel)[0] > maxX ? getPixelXY(pixel)[0] : maxX;

            minY = getPixelXY(pixel)[1] < minY ? getPixelXY(pixel)[1] : minY;
            maxY = getPixelXY(pixel)[1] > maxY ? getPixelXY(pixel)[1] : maxY;
        }

        for(int x = minX; x < maxX; x++) {
            pixelWriter.setColor(x, minY, Color.RED);
            pixelWriter.setColor(x, maxY, Color.RED);
        }
        for(int y = minY; y < maxY; y++){
            pixelWriter.setColor(y, minX, Color.RED);
            pixelWriter.setColor(y, maxX, Color.RED);
        }

        return writableImage;
    }

    private int[] getPixelXY(int pixel){
        int[] xy = new int[2];


        return xy;
    }

    public Image getBnWImage(){
        if(imageLoaded == null) return null;
        PixelReader pixelReader = imageLoaded.getPixelReader();
        WritableImage writableImage = new WritableImage((int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for(int i = 0; i < writableImage.getWidth(); i++){
            for (int j = 0; j < writableImage.getHeight(); j++){
                pixelWriter.setColor(i, j, isColorBelowThreshold(pixelReader.getColor(i,j)) ? Color.BLACK : Color.WHITE);
            }
        }

        return writableImage;
    }

    public boolean isColorBelowThreshold(Color color){
        return ((color.getBlue() + color.getGreen() + color.getRed())/3) < brightnessThreshold.getValue()/100f; //True - bird; False - NOT bird
    }

    public void bindBrightnessSlider(DoubleProperty prop){
        brightnessThreshold.bind(prop);
    }

    private int clamp(int integer, int min, int max){
        max--; //Quick and dirty fix
        return integer < min ? 0 : integer > max ? max : integer;
    }
}
