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


    public ImageProcessor(Image imageLoaded){
        this.imageLoaded = imageLoaded;
        pReader = imageLoaded.getPixelReader();
        sutil = new SetUtils((int)imageLoaded.getHeight() * (int)imageLoaded.getWidth(), this);
    }

    public Image drawBounds(int[] sets){
        int minX = (int)imageLoaded.getWidth();
        int maxX = 0;
        int minY = (int)imageLoaded.getHeight();
        int maxY = 0;
        WritableImage writableImage = new WritableImage(getBnWImage().getPixelReader(),(int)imageLoaded.getWidth(), (int)imageLoaded.getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for(int root : sutil.getRoots()){
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
                    sutil.getSets()[(y)*(int)imageLoaded.getWidth()+x] = getPixelRoot(x, y);
//                    System.out.println("Adding pixel " + x + ", " + y + " at root with root " + sutil.getRoot(getPixelRoot(x, y)) + " with position: " + getPixelXY(sutil.getRoot(getPixelRoot(x, y)))[0] + ", " + getPixelXY(sutil.getRoot(getPixelRoot(x, y)))[1]);
                }
                else sutil.getSets()[(y)*(int)imageLoaded.getWidth()+x] = -1;
            }
        }

//        for(int p : sutil.getSets()) if (p >= 0) System.out.println(sutil.getRoot(p)); //Works for every non-negative pixel!

        return sutil.getRoots().size();
    }

    private int getPixelRoot(int x, int y) {
        int root = y * (int)imageLoaded.getWidth() + x;

        if(x > 0 && isColorBelowThreshold(pReader.getColor(x-1, y))){
            root = sutil.getRoot(y * (int)imageLoaded.getWidth() + x - 1); //Offset 0s ?
        }
        if(x > 0 && y > 0 && isColorBelowThreshold(pReader.getColor(x-1, y-1))){
            int checking = (y-1)*(int)imageLoaded.getWidth() + x - 1;
            if (sutil.getRoot(root) != sutil.getRoot(checking)) sutil.join(checking, root);
        }
        if(y > 0 && isColorBelowThreshold(pReader.getColor(x, y-1))){
            int checking = (y-1)*(int)imageLoaded.getWidth() + x;
            if (sutil.getRoot(root) != sutil.getRoot(checking)) sutil.join(checking, root);
        }
        if(y > 0 && x < imageLoaded.getWidth()-1 && isColorBelowThreshold(pReader.getColor(x+1,y-1))) {
            int checking = (y-1)*(int)imageLoaded.getWidth() + x + 1; //offset width in relation to array
            if (sutil.getRoot(root) != sutil.getRoot(checking)) sutil.join(checking, root);
        }

//        System.out.println(root);

        return root;
    }

    public void bindBrightnessSlider(DoubleProperty prop){
        brightnessThreshold.bind(prop);
    }

    public Image getImage() {
        return imageLoaded;
    }
}
