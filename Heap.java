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
    private HeapItem min;
    private HeapItem root;
    private int size = 0;
    private int treesCount = 0;
    private int markedNodesCount = 0;
    private int linksCount = 0;
    private int cutsCount = 0;
    private int heapifyCost = 0;
    
    /**
     *
     * Constructor to initialize an empty heap.
     *
     */
    public Heap(boolean lazyMelds, boolean lazyDecreaseKeys)
    {
        this.lazyMelds = lazyMelds;
        this.lazyDecreaseKeys = lazyDecreaseKeys;
    }


/**
     * 
     * @param root1 root of the first tree
     * @param root2 root of the second tree
     * 
     * link both trees of the same degree, returns the root of the new tree
     */
    private HeapNode link(HeapNode root1, HeapNode root2) {
        // O(1) time
        //throw Exception if different ranks?
        if (root1.item.key > root2.item.key) {
            HeapNode tmp = root1;
            root1 = root2;
            root2 = tmp;
        }
        if (root1.child == null) {
            root1.child = root2;
            root2.next = root2;
            root2.prev = root2;
            root2.parent = root1;
        }
        else {
            root1.child.prev.next = root2;
            root2.prev = root1.child.prev;
            root2.next = root1.child;
            root1.child.prev = root2;
            root1.child = root2;
            root2.parent = root1;
        }
        

        root1.rank++;
        linksCount++;
        treesCount--;

        return root1;
    }


    private void cut(HeapNode node, HeapNode parentNode) {
        // O(1) time
        node.parent = null;
        if (node.isMarked){
            node.isMarked = false;
            markedNodesCount--;
        }
        parentNode.rank--;
        if (node.next == node) {
            parentNode.child = null;
        }
        else {
            parentNode.child = node.next;
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        node.next = node;
        node.prev = node;   
        treesCount++;
    }


    private void cascadingCut(HeapNode node, HeapNode parentNode) {
        //O(1) time
        if (parentNode != null) {
            cut(node, parentNode);
            cutsCount++;
            if (lazyDecreaseKeys == true) {
                Heap newHeap = new Heap(lazyMelds, lazyDecreaseKeys);
                newHeap.min = node.item;
                newHeap.root = node.item;
                meld(newHeap);
            }
            if (parentNode.parent != null) {
                if (parentNode.isMarked == false) {
                    parentNode.isMarked = true;
                    markedNodesCount++;
                }
                else {
                    cascadingCut(parentNode, parentNode.parent);
                }
            }
        }
    }


    /**
     * preforms successive linking on the list of trees
     * @return an array of logn buckets, each one contains a tree of different rank or null
     */
    private HeapNode[] toBuckets(HeapNode node1) {
        //O(logn) time
        int numBuckets = Integer.SIZE - Integer.numberOfLeadingZeros(size);
        HeapNode[] buckets = new HeapNode[numBuckets];
        
        node1.prev.next = null;
        while (node1 != null) {
            HeapNode node2 = node1;
            node1 = node1.next;
            while (buckets[node2.rank] != null) {
                node2 = link(node2, buckets[node2.rank]);
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
    private HeapNode fromBuckets(HeapNode[] bucketsList) {
        //O(logn) time
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
                    if (bucketNode.item.key < min.item.key) {
                        min = bucketNode;
                    }
                }
            }
        }
        return min;
    }


    /**
     * preforms successive linking on a heap and unifies it to a legal binomial heap
     */
    private void consolidate(HeapNode node) {
        //O(logn) time
        if (node == null) {
            min = null;
            root = null;
            treesCount = 0;
            return;
        }
        HeapNode[] bucketsList = toBuckets(node);
        min = fromBuckets(bucketsList).item;
        root = min;
    }


    /**
     * replaces the node's key and info with its parent recuresively
     * @param node node to start heapify from
     */
    private void heapifyUp(HeapNode node) {
        //O(logn) time
        if(node.parent == null) {
            return;
        }   
        if(node.item.key >= node.parent.item.key) {
            return;
        }

        HeapItem tmp= node.item;
        node.item = node.parent.item;
        node.parent.item = tmp;

        node.item.node = node;
        node.parent.item.node = node.parent;

        heapifyCost++;
        heapifyUp(node.parent);
    }

    /**
     * 
     * pre: key > 0
     *
     * Insert (key,info) into the heap and return the newly generated HeapNode.
     *
     */
    public HeapItem insert(int key, String info) 
    {    //O(1) time

        //insert to an empty heap
        HeapItem item = new HeapItem();
        item.key = key;
        item.info = info;

        if (min == null) {
            min = item;
            HeapNode minNode = new HeapNode();
            minNode.next = minNode;
            minNode.prev = minNode;
            item.node = minNode;
            item.node.item = item;
            root = min;
            size = 1;
            treesCount = 1;
            return item;
        }

        Heap heap2 = new Heap(lazyMelds, lazyDecreaseKeys);
        HeapItem newItem = heap2.insert(key, info);
        meld(heap2);
        return newItem;
    }

    /**
     * 
     * Return the minimal HeapNode, null if empty.
     *
     */
    public HeapItem findMin()
    //O(1) time
    {
        return min;
    }

    /**
     * 
     * Delete the minimal item.
     *
     */
    public void deleteMin()
    //O(logn) time
    {
        size--;

        root = min.node.next.item;

        if (min.node.next == min.node) {
        HeapNode child = min.node.child;
        if (child == null) {
            min = null;
            root = null;
            treesCount = 0;
            return;
        } else {
            HeapNode currChild = child;
            int min_rank = min.node.rank;
            while (min_rank > 0) {
                currChild.parent = null;
                currChild = currChild.next;
                min_rank--;
            }
            consolidate(child);
            return;
        }
    }
        
        min.node.prev.next = min.node.next;
        min.node.next.prev = min.node.prev;

        HeapNode node = root.node;

        HeapNode child = min.node.child;
        HeapNode currChild = child;

        if (child != null){
            int min_rank = min.node.rank;
            while (min_rank > 0) {
                currChild.parent = null;
                currChild = currChild.next;
                min_rank--;
            }
            
            node.next.prev = child.prev;
            child.prev.next = node.next;
            node.next = child;
            child.prev = node;
        }

        //min.node.child = null;
        consolidate(node);
    }

    /**
     * 
     * pre: 0<=diff<=x.key
     * 
     * Decrease the key of x by diff and fix the heap.
     * 
     */
    public void decreaseKey(HeapItem x, int diff) 
    //O(logn) time
    {    
        x.key -= diff;
        if (x.key < min.key) {
            min = x;
        }
        if (x.node.parent != null) {
            if (x.key >= x.node.parent.item.key) {
                return;
            }

            if (lazyDecreaseKeys == true) {
                cascadingCut(x.node, x.node.parent);
            }
            else {
                heapifyUp(x.node);
            }
        }
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapItem x) 
    //O(logn) time
    {    
        decreaseKey(x, x.key - min.key + 1);
        deleteMin();
    }


    /**
     * 
     * Meld the heap with heap2
     * pre: heap2.lazyMelds = this.lazyMelds AND heap2.lazyDecreaseKeys = this.lazyDecreaseKeys
     *
     */
    public void meld(Heap heap2)
    {
        //O(1) time
        //add heap2 history to this (need to add cuts, heapify cost and maybe more)
        size += heap2.size;
        treesCount += heap2.treesCount;
        markedNodesCount += heap2.markedNodesCount;
        linksCount += heap2.linksCount;
        cutsCount += heap2.cutsCount;
        heapifyCost += heap2.heapifyCost;

        //deal with empty heaps
        if (min == null) {
            min = heap2.min;
            root = min;
            return;
        }
        if (heap2.min == null) {
            return;
        }

        //connect root lists
        root.node.next.prev = heap2.root.node.prev;
        heap2.root.node.prev.next = root.node.next;
        root.node.next = heap2.root.node;
        heap2.root.node.prev = root.node;

        //successive linking if required
        if (lazyMelds == false) {
            consolidate(root.node);
        }
        else {
            //find new minimum
            if (heap2.min.key < min.key) {
                min = heap2.min;
            }
        }
        

        return;          
    }
    
    
    /**
     * 
     * Return the number of elements in the heap
     *   
     */
    public int size()
    {
        //O(1) time
        return size;
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        //O(1) time
        return treesCount;
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        //O(1) time
        return markedNodesCount;
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        //O(1) time
        return linksCount;
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        //O(1) time
        return cutsCount;
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
        //O(1) time
        return heapifyCost;
    }
    
    
    /**
     * Class implementing a node in a Heap.
     *  
     */
    public static class HeapNode{
        public HeapItem item;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public int rank;
        public boolean isMarked;
    }
    
    /**
     * Class implementing an item in a Heap.
     *  
     */
    public static class HeapItem{
        public HeapNode node;
        public int key;
        public String info;
    }
}
