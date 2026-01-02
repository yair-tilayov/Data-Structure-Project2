/**
 * Heap
 *
 * An implementation of Fibonacci heap over positive integers 
 * with the possibility of not performing lazy melds and 
 * the possibility of not performing lazy decrease keys.
 *
 */
public class Heap
{
    public final boolean lazyMelds;
    public final boolean lazyDecreaseKeys;
    public HeapNode min;
    public int size = 0;
    public int linksCount = 0;
    public int treesCount = 0;
    
    /**
     *
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys)
    {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
        // student code can be added here
    }

    /**
     * 
     * @param root1 root of the first tree
     * @param root2 root of the second tree
     * 
     * link both trees of the same degree, returns the root of the new tree
     */
    public static HeapNode link(HeapNode root1, HeapNode root2) {
        //throw Exception if different ranks?
        if (root1.key > root2.key) {
            HeapNode tmp = root1;
            root1 = root2;
            root2 = tmp;
        }
        root1.child.prev.next = root2;
        root2.prev = root1.child.prev;
        root2.next = root1.child;
        root1.child.prev = root2;
        root1.child = root2;
        root2.parent = root1;

        root1.rank++;

        return root1;
    }

    /**
     * preforms successive linking on the list of trees
     * @return an array of logn buckets, each one contains a tree of different rank or null
     */
    public HeapNode[] toBuckets() {
        //works on minimum
        int numBuckets = Integer.SIZE - Integer.numberOfLeadingZeros(size);
        HeapNode[] buckets = new HeapNode[numBuckets];
        
        HeapNode node1 = min;
        node1.prev.next = null;
        while (node1 != null) {
            HeapNode node2 = node1;
            node1 = node1.next;
            while (buckets[node2.rank] != null) {
                node2 = Heap.link(node2, buckets[node2.rank]);
                linksCount++;
                buckets[node2.rank - 1] = null;
            }
            buckets[node2.rank] = node2;
        }
        return buckets;

    }

    /**
     * 
     * @param bucketsList an array of buckets ehich contains binomial trees or null
     * @return a min node of a unifies heap created from the buckets
     */
    public HeapNode fromBuckets(HeapNode[] bucketsList) {
        //calculate num trees and minimum
        treesCount = 0;
        HeapNode min = null;
        for (HeapNode bucketNode : bucketsList) {
            if (bucketNode != null) {
                treesCount++;
                if (min == null) {
                    min = bucketNode;
                    min.next = min;
                    min.prev = min;
                }
                else {
                    bucketNode.prev = min;
                    bucketNode.next = min.next;
                    min.next = bucketNode;
                    bucketNode.next.prev = bucketNode;
                    if (bucketNode.key < min.key) {
                        min = bucketNode;
                    }
                }
            }
        }
        return min;
    }

    public void consolidate() {
        HeapNode[] bucketsList = toBuckets();
        min = fromBuckets(bucketsList);
    }

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapNode insert(int key, String info) 
    {    
        return null; // should be replaced by student code
    }

    /**
     * 
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapNode findMin()
    {
        return min;
    }

    /**
     * 
     * Delete the minimal item.
     *
     */
    public void deleteMin()
    {
        return; // should be replaced by student code
    }

    /**
     * 
     * pre: 0<=diff<=x.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapNode x, int diff) 
    {    
        return; // should be replaced by student code
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapNode x) 
    {    
        return; // should be replaced by student code
    }


    /**
     * 
     * Meld the heap with heap2
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
        //add heap2 history to this (need to add cuts, heapify cost and maybe more)
        size += heap2.size;
        treesCount += heap2.treesCount;
        linksCount += heap2.linksCount;

        //connect root lists
        min.next.prev = heap2.min.prev;
        heap2.min.prev.next = min.next;
        min.next = heap2.min;
        heap2.min.prev = min;

        //find new minimum
        if (heap2.min.key < min.key) {
            min = heap2.min;
        }

        //successive linking if required
        if (lazyMelds == false) {
            consolidate();
        }



        return; // should be replaced by student code           
    }
    
    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size()
    {
        return size;
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return treesCount; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return 46; // should be replaced by student code
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        return 46; // should be replaced by student code
    }
    
    
    /**
     * Class implementing a node in a ExtendedFibonacci Heap.
     *  
     */
    public static class HeapNode{
        public int key;
        public String info;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
    }
}
