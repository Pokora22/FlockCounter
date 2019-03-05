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

    public Image drawBounds(ArrayList<ArrayList<Pixel>> sets){
        int minX = (int)imageLoaded.getWidth();
        int maxX = 0;
        int minY = (int)imageLoaded.getHeight();
        int maxY = 0;
        WritableImage writableImage = new WritableImage(imageLoaded.getPixelReader(),(int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        System.out.println("org img: " + imageLoaded.getWidth() + "x" + imageLoaded.getHeight());

        for (ArrayList<Pixel> set : sets) {
            for(Pixel p : set){
                minX = p.getWidth() < minX ? p.getWidth() : minX;
                maxX = p.getWidth() > maxX ? p.getWidth() : maxX;

                minY = p.getHeight() < minY ? p.getHeight() : minY;
                maxY = p.getHeight() > maxY ? p.getHeight() : maxY;
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

        for(ArrayList<Pixel> set : sets){
            int x = set.get(0).getWidth();
            int y = set.get(0).getHeight();
            int width = set.get(set.size()-1).getWidth() - x;
            int height = set.get(set.size()-1).getHeight() - y;
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
