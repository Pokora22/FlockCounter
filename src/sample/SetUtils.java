package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SetUtils implements Runnable{
    private int[] arr;
    private ArrayList<Integer> roots;
    private HashMap<Integer, Integer> setSizes;

    public SetUtils(int[] arr){
        this.arr = arr;
        roots = new ArrayList<>();
        setSizes = new HashMap<>();
    }

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

    private void fillSets(){
        for(int i : arr){
            if(i >= 0) {
                int root = findRoot(i);
                if (!roots.contains(root)){
                    roots.add(root);
                    setSizes.put(root, 1);
                }
                else setSizes.replace(root, setSizes.get(root)+1);
            }
        }
    }

    public int findRoot(int position){
        return arr[position] == position? position : findRoot(arr[position]);
    }

    public ArrayList<Integer> getRoots(){
        return roots;
    }

    public ArrayList<Integer> getSizeFilteredRoots(int sizeLimit){
        ArrayList<Integer> filteredRoots = new ArrayList<>(roots);
//        int index = 0;
//        while(index < filteredRoots.size()){
//            int elemCount = 0;
//            for(int e : arr){
//                if(e >= 0 && findRoot(e, arr) == filteredRoots.get(index)) elemCount++;
//            }
//            if(elemCount <= sizeLimit){
//                filteredRoots.remove((Integer)filteredRoots.get(index));
//                continue;
//            }
//            index++;
//        }

        for(Integer root : roots) if(setSizes.get(root) > sizeLimit) filteredRoots.add(root);

        return filteredRoots;
    }

    public void join(int childPosition, int parentPosition) {
        arr[findRoot(childPosition)] = findRoot(parentPosition);
    }

    public int[] getArr(){
        return arr;
    }

    @Override
    public void run() {
        fillSets();
    }
}
