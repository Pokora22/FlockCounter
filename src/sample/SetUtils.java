package sample;

import java.util.ArrayList;
import java.util.Arrays;

public class SetUtils {
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
        for(int i : arr){
            if(i >= 0) {
                int root = findRoot(i, arr);
                if (!roots.contains(root)) roots.add(root);
            }
        }

        return roots;
    }

    public ArrayList<Integer> getSizeFilteredRoots(int sizeLimit, int[] arr){ //TODO: This can throw null pointer. Concurrent modification. Cannot iterate through and remove at same time.
        ArrayList<Integer> roots = getRoots(arr);
        int index = 0;
        while(index < roots.size()){
            int elemCount = 0;
            for(int e : arr){
                if(e >= 0 && findRoot(e, arr) == roots.get(index)) elemCount++;
            }
            if(elemCount <= sizeLimit){
                roots.remove((Integer)roots.get(index));
                continue;
            }
            index++;
        }
//
//        for(int r : roots){
//            int elemCount = 0;
//            for(int e : arr){
//                if(e >= 0 && findRoot(e, arr) == r) elemCount++;
//            }
//            if(elemCount <= sizeLimit) roots.remove((Integer)r);
//        }

        return roots;
    }

    public void join(int childPosition, int parentPosition, int[] arr) {
        arr[findRoot(childPosition, arr)] = findRoot(parentPosition, arr);
    }
}
