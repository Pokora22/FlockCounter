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
    private PixelReader pReader;
    private SimpleDoubleProperty brightnessThreshold = new SimpleDoubleProperty(50);
    private SetUtils sutil;
    private int[] pixels;


    public ImageProcessor(Image imageLoaded){
        this.imageLoaded = imageLoaded;
        pReader = imageLoaded.getPixelReader();
        sutil = new SetUtils();
        pixels = new int[(int)imageLoaded.getHeight() * (int)imageLoaded.getWidth()];
    }

    public Image drawBounds(int[] sets){
        int minX = (int)imageLoaded.getWidth();
        int maxX = 0;
        int minY = (int)imageLoaded.getHeight();
        int maxY = 0;
        WritableImage writableImage = new WritableImage(getBnWImage().getPixelReader(),(int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for(int root : sutil.getRoots(pixels)){
            pixelWriter.setColor(getPixelXY(root)[0], getPixelXY(root)[1], Color.RED);
        }

        return writableImage;
    }

    public int[] getPixelXY(int pixel){ //Where to put it ?
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
            for (int x = 0; x < imageLoaded.getWidth(); x++){ //TODO: Would left, left-up, up suffice? Check for set - edge cases ?
                if (isColorBelowThreshold(pReader.getColor(x, y))){
                    int pos = y * (int)imageLoaded.getWidth() + x;
                    pixels[pos] = pos; //Set to itself first?
                    pixels[pos] = checkNeighbourPixels(x, y, pos);
                }
                else pixels[(y)*(int)imageLoaded.getWidth()+x] = -1;
            }
        }

        return sutil.getRoots(pixels).size();
    }

    private int checkNeighbourPixels(int x, int y, int root) { //FIXME: Doing this out of order. Root of this pixel(x,y) is not yet set (it's 0) so it will point to wrong space. And if position 0 is white (-1) it will oob. Need to set root first, then check for neighbours!

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

    public void bindBrightnessSlider(DoubleProperty prop){
        brightnessThreshold.bind(prop);
    }

    public Image getImage() {
        return imageLoaded;
    }
}
