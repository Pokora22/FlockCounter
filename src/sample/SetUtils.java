package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class SetUtils {
    private int[] pixels;

    public SetUtils(int arrSize){
        pixels = new int[arrSize];
    }

    public int[] getSortedSets(){
        int[] sets = pixels.clone();
        String unsorted = "Unsorted array: ";
        String sorted = "Sorted array: ";

        for(int p : sets) unsorted += p + ", ";
        Arrays.sort(sets); //negative values?
        for(int p : sets) sorted += p + ", ";

        System.out.println(unsorted);
        System.out.println(sorted);

        return sets; //2D? 1D probably enough
    }

    public int getRoot(int position){
        return pixels[position] == position? position : getRoot(pixels[position]); //??
    }

    public void add(int location, int value){
        pixels[location] = value;
    }

    public int getNumberOfSets(){ //TODO: foreach in sets where int < 0, count++; return count
        /////CONTAINS WAY - write own sort and do it other way later? Probably better performance
        ArrayList<Integer> roots = new ArrayList<>();

        for(int p : pixels){
            int root = getRoot(p);
            if(!roots.contains(root)) roots.add(root);
        }

//        int[] sortedPixels = getSortedSets();
//        int setCount = 0;
//        int compare = sortedPixels[0];
//
//        for(int p : sortedPixels) {
//            if(p != compare) {
//                compare = p;
//                setCount++;
//            }
//        }
//        return setCount;

        System.out.println(roots);

        return roots.size();
    }

    public void addPixel(int position, int value){

    }

    public int[] getSets() {
        return pixels;
    }
}
