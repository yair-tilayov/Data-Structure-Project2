
public class SelfTest {
    
    public static void main(String[] args) {
        Heap heap = new Heap(false, true);
        for (int i = 0; i < 1000; i++) {
            heap.insert(i, "a");
        }
        //System.out.println(heap.findMin().node.child.next.item.key);
        heap.deleteMin();
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
