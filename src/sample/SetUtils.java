package sample;

import java.util.Arrays;

public class SetUtils {
    private int[] pixels;

    public SetUtils(int arrSize){
        pixels = new int[arrSize];
    }

    public int[] getSortedSets(){
        int[] sets = pixels.clone();
        Arrays.sort(pixels); //negative values?
        return sets; //2D? 1D probably enough
    }

    public int getRoot(int position){
        return pixels[position] == position? position : getRoot(pixels[position]); //??
    }

    public void add(int location, int value){
        pixels[location] = value;
    }

    public int getNumberOfSets(){ //TODO: foreach in sets where int < 0, count++; return count
        int setCount = 0;
        int compare = pixels[0];

        for(int p : pixels) {
            if(p != compare) {
                compare = p;
                setCount++;
            }
        }

        return setCount;
    }

    public void addPixel(int position, int value){

    }

    public int[] getSets() {
        return pixels;
    }
}
