
public class SelfTest {
    
    public static void main(String[] args) {
        Heap heap = new Heap(false, true);
        for (int i = 0; i < 4; i++) {
            heap.insert(i, "a");
        }
        Heap.HeapNode min = heap.findMin().node;
        int tmp = 10;
        Heap.HeapNode child = min.child;
        while (tmp > 0) {
            System.out.print(child.item.key + " ");
            child = child.next;
            tmp--;
        }
        //heap.deleteMin();
        System.out.println("size: " + heap.size());
        System.out.println("trees number: " + heap.numTrees());
        System.out.println("marked nodes number: " + heap.numMarkedNodes());
        System.out.println("links number: " + heap.totalLinks());
        System.out.println("cuts number: " + heap.totalCuts());
        System.out.println("heapify cost: " + heap.totalHeapifyCosts());
        System.out.println("minimum value: " + heap.findMin().key);
        System.out.println(heap.findMin().node.rank);
    }

}
