
public class SelfTest {
    
    public static void main(String[] args) {
        Heap heap = new Heap(false, false);
        Heap.HeapItem[] items = new Heap.HeapItem[1000];
        for (int i = 0; i < 1000; i++) {
            items[i] = heap.insert(i, "a");
        }
        heap.deleteMin();
        heap.decreaseKey(items[100], 99);
        heap.delete(items[100]);

        Heap heap2 = new Heap(false, false);
        Heap.HeapItem[] items2 = new Heap.HeapItem[1000];
        for (int i = 1000; i < 2000; i++) {
            items[i-1000] = heap.insert(i, "a");
        }

        heap.meld(heap2);

        //System.out.println();
        System.out.println("size: " + heap.size());
        System.out.println("trees number: " + heap.numTrees());
        System.out.println("marked nodes number: " + heap.numMarkedNodes());
        System.out.println("links number: " + heap.totalLinks());
        System.out.println("cuts number: " + heap.totalCuts());
        System.out.println("heapify cost: " + heap.totalHeapifyCosts());
        System.out.println("minimum value: " + heap.findMin().key);
    }

}
