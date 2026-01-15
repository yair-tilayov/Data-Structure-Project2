import java.util.*;

public class Exp1 {

    public static void main(String[] args) {
        int n = 464646;
        List<Integer> nums = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nums.add(i);
        }
        long[] runTimeArr = new long[20];
        int[] sizeArr = new int[20];
        int[] treesNumArr = new int[20];
        int[] linksNumArr = new int[20];
        int[] cutsNumArr = new int[20];
        int[] heapifyCostArr = new int[20];
        int[] maxActionArr = new int[20];
        for (int k = 0; k < 20; k++) {
            Collections.shuffle(nums);
            boolean lazyMelds = false;
            boolean lazyDecreaseKey = true;
            Heap h = new Heap(lazyMelds, lazyDecreaseKey);
            Heap.HeapItem[] items = new Heap.HeapItem[n];
            int maxActionCost = 0;
            long start = System.currentTimeMillis();
            for (int i : nums) {
                int before = h.totalLinks() + h.totalCuts() + h.totalHeapifyCosts();
                items[i] = h.insert(i, "a");
                int after = h.totalLinks() + h.totalCuts() + h.totalHeapifyCosts();
                int actionCost = after - before;
                if (actionCost > maxActionCost) {
                    maxActionCost = actionCost;
                }

            }
            int before = h.totalLinks() + h.totalCuts() + h.totalHeapifyCosts();
            h.deleteMin();
            long end = System.currentTimeMillis();
            long elapsedMs = end - start;
            int after = h.totalLinks() + h.totalCuts() + h.totalHeapifyCosts();
            int actionCost = after - before;
            if (actionCost > maxActionCost) {
                maxActionCost = actionCost;
            }
            runTimeArr[k] = elapsedMs;
            sizeArr[k] = h.size();
            treesNumArr[k] = h.numTrees();
            linksNumArr[k] = h.totalLinks();
            cutsNumArr[k] = h.totalCuts();
            heapifyCostArr[k] = h.totalHeapifyCosts();
            maxActionArr[k] = maxActionCost;
        }

        System.out.println("runTims ms: " + Exp1.average(runTimeArr));
        System.out.println("size: " + Exp1.average(sizeArr));
        System.out.println("trees number: " + Exp1.average(treesNumArr));
        System.out.println("links number: " + Exp1.average(linksNumArr));
        System.out.println("cuts number: " + Exp1.average(cutsNumArr));
        System.out.println("heapify cost: " + Exp1.average(heapifyCostArr));
        System.out.println("maximal action cost: " + Exp1.average(maxActionArr));
    }

    public static double average(int[] arr) {
        double sum = 0;
        for (int x : arr) {
            sum += x;
        }

        double average = sum / arr.length;
        return average;

    }

    public static double average(long[] arr) {
        double sum = 0;
        for (long x : arr) {
            sum += x;
        }

        double average = sum / arr.length;
        return average;

    }
    
}
