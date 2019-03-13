package sample;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ImageProcessor {

    private Image imageLoaded;
    private PixelReader pReader;
    private SimpleDoubleProperty brightnessThreshold = new SimpleDoubleProperty(50);
    private SimpleIntegerProperty noiseFactor = new SimpleIntegerProperty(0);
    private SetUtils sutil;
    private int[] pixels;


    public ImageProcessor(Image imageLoaded){
        this.imageLoaded = imageLoaded;
        pReader = imageLoaded.getPixelReader();
        sutil = new SetUtils();
        pixels = new int[(int)imageLoaded.getHeight() * (int)imageLoaded.getWidth()];
    }

    public Image drawBounds(){
        WritableImage writableImage = new WritableImage(imageLoaded.getPixelReader(),(int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for(int root : sutil.getSizeFilteredRoots(noiseFactor.get() ,pixels)){
            int minX = (int)imageLoaded.getWidth();
            int maxX = 0;
            int minY = (int)imageLoaded.getHeight();
            int maxY = 0;

            for (int i = 0; i < pixels.length; i++) {
                if(pixels[i] >= 0 && sutil.findRoot(pixels[i], pixels) == root){
                    if(getPixelXY(i)[0] < minX) minX = getPixelXY(i)[0];
                    if(getPixelXY(i)[0] > maxX) maxX = getPixelXY(i)[0];
                    if(getPixelXY(i)[1] < minY) minY = getPixelXY(i)[1];
                    if(getPixelXY(i)[1] > maxY) maxY = getPixelXY(i)[1];
                }
            }

            for (int x = minX; x < maxX; x++) {
                pixelWriter.setColor(x, minY, Color.RED);
                pixelWriter.setColor(x, maxY, Color.RED);
            }
            for (int y = minY; y < maxY; y++) {
                pixelWriter.setColor(minX, y, Color.RED);
                pixelWriter.setColor(maxX, y, Color.RED);
            }
        }

        return writableImage;
    }

    public int[] getPixelXY(int pixel){
        int[] xy = new int[2];
        xy[0] = pixel % (int)imageLoaded.getWidth();
        xy[1] = (pixel - xy[0])/ (int) imageLoaded.getWidth();
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

    private boolean isColorBelowThreshold(Color color){
        return ((color.getBlue() + color.getGreen() + color.getRed())/3) < brightnessThreshold.getValue()/100f; //True - bird; False - NOT bird
    }

    public int findBirds(){
        for (int y = 0; y < imageLoaded.getHeight(); y++){
            for (int x = 0; x < imageLoaded.getWidth(); x++){
                if (isColorBelowThreshold(pReader.getColor(x, y))){
                    int pos = y * (int)imageLoaded.getWidth() + x;
                    pixels[pos] = pos; //Set to itself first?
                    pixels[pos] = checkNeighbourPixels(x, y, pos);
                }
                else pixels[(y)*(int)imageLoaded.getWidth()+x] = -1;
            }
        }

        return sutil.getSizeFilteredRoots(noiseFactor.get() ,pixels).size();
    }

    private int checkNeighbourPixels(int x, int y, int root) {

        if(x > 0 && isColorBelowThreshold(pReader.getColor(x-1, y))){
            root = sutil.findRoot(y * (int)imageLoaded.getWidth() + x - 1, pixels); //Offset 0s ?
        }
        if(x > 0 && y > 0 && isColorBelowThreshold(pReader.getColor(x-1, y-1))){
            int checking = (y-1)*(int)imageLoaded.getWidth() + x - 1;
            if (sutil.findRoot(root, pixels) != sutil.findRoot(checking, pixels)) sutil.join(root, checking, pixels);
            root = sutil.findRoot(checking, pixels);
        }
        if(y > 0 && isColorBelowThreshold(pReader.getColor(x, y-1))){
            int checking = (y-1)*(int)imageLoaded.getWidth() + x;
            if (sutil.findRoot(root, pixels) != sutil.findRoot(checking, pixels)) sutil.join(root, checking, pixels);
            root = sutil.findRoot(checking, pixels);
        }
        if(y > 0 && x < imageLoaded.getWidth()-1 && isColorBelowThreshold(pReader.getColor(x+1,y-1))) {
            int checking = (y-1)*(int)imageLoaded.getWidth() + x + 1; //offset width in relation to array
            if (sutil.findRoot(root, pixels) != sutil.findRoot(checking, pixels)) sutil.join(root, checking, pixels);
            root = sutil.findRoot(checking, pixels);
        }

        return root;
    }

    public ArrayList<Integer> getLabelPositions(){
         return sutil.getSizeFilteredRoots(noiseFactor.get(), pixels);
    }

    public void bindBrightnessSlider(DoubleProperty prop){
        brightnessThreshold.bind(prop);
    }

    public void bindNoiseSlider(DoubleProperty prop){
        noiseFactor.bind(prop);
    }

    public Image getImage() {
        return imageLoaded;
    }
}
