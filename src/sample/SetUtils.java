package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SetUtils {
    private HashMap<Integer, Integer> sizes;

    public SetUtils(){}

    public int[] getSortedSets(int[] arr){
        int[] sets = arr.clone();
        String unsorted = "Unsorted array: ";
        String sorted = "Sorted array: ";

        for(int p : sets) unsorted += p + ", ";
        Arrays.sort(sets); //negative values?
        for(int p : sets) sorted += p + ", ";

        System.out.println(unsorted);
        System.out.println(sorted);

        return sets;
    }

    public int findRoot(int position, int[] arr){
        return arr[position] == position? position : findRoot(arr[position], arr);
    }

    public ArrayList<Integer> getRoots(int[] arr){
        ArrayList<Integer> roots = new ArrayList<>();
        sizes = new HashMap<>();

        for(int i : arr){
            if(i >= 0) {
                int root = findRoot(i, arr);
                if (!roots.contains(root)){
                    roots.add(root);
                    sizes.put(root, 1);
                }
                else sizes.replace(root, sizes.get(root) + 1);
            }
        }

        return roots;
    }

    public ArrayList<Integer> getSizeFilteredRoots(ArrayList<Integer> roots, int sizeLimit){
        int index = 0;
        while(index < roots.size()){
//            int elemCount = 0;
//            for(int e : arr){
//                if(e >= 0 && findRoot(e, arr) == roots.get(index)) elemCount++;
//            }
            if(sizes.get(roots.get(index)) < sizeLimit){
                roots.remove((Integer)roots.get(index));
                continue;
            }
            index++;
        }

        return roots;
    }

    public void join(int childPosition, int parentPosition, int[] arr) {
        arr[findRoot(childPosition, arr)] = findRoot(parentPosition, arr);
    }

    public HashMap<Integer, Integer> getSizes() {
        return sizes;
    }
}
