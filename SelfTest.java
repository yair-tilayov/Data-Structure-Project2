public class SelfTest {
    
    public static void main(String[] args) {
        Heap heap = new Heap(false, true);
        for (int i = 0; i < 1000; i++) {
            heap.insert(i, "a");
        }
        System.out.println("size: " + heap.size);
    }

}
