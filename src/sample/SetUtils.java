package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class SetUtils {
    private int[] pixels;
    private ImageProcessor imgProc;

    public SetUtils(int arrSize, ImageProcessor imgProc){
        pixels = new int[arrSize];
        this.imgProc = imgProc;
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

        return sets;
    }

    public int findRoot(int position){
        return pixels[position] == position? position : findRoot(pixels[position]);
    }

    public ArrayList<Integer> getRoots(){
        ArrayList<Integer> roots = new ArrayList<>();

        for(int p : pixels){
            if(p >= 0) {
                int root = findRoot(p);
                if (!roots.contains(root)) roots.add(root);
            }
        }

        return roots;
    }

    public int[] getSets() {
        return pixels;
    }

    public void join(int childPosition, int parentPosition) {
        pixels[findRoot(childPosition)] = findRoot(parentPosition);
    }
}
