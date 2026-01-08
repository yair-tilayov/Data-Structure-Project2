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
        //throw Exception if different ranks?
        if (root1.item.key > root2.item.key) {
            HeapNode tmp = root1;
            root1 = root2;
            root2 = tmp;
        }
        if (root1.child == null) {
            root1.child = root2;
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
        node.parent = null;
        node.isMarked = false;
        markedNodesCount--;
        parentNode.rank--;
        if (node.next == node) {
            parentNode.child = null;
        }
        else {
            parentNode.child = node.next;
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        treesCount++;
    }


    private void cascadingCut(HeapNode node, HeapNode parentNode) {
        if (parentNode != null) {
            cut(node, parentNode);
            cutsCount++;
            if (lazyDecreaseKeys == true) {
                Heap newHeap = new Heap(lazyMelds, lazyDecreaseKeys);
                newHeap.min = node.item;
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
        min.node.prev.next = min.node;
        return buckets;

    }

    /**
     * 
     * @param bucketsList an array of buckets ehich contains binomial trees or null
     * @return a min node of a unifies heap created from the buckets
     */
    private HeapNode fromBuckets(HeapNode[] bucketsList) {
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
        HeapNode[] bucketsList = toBuckets(node);
        min = fromBuckets(bucketsList).item;

    }


    /**
     * replaces the node's key and info with its parent recuresively
     * @param node node to start heapify from
     */
    private void heapifyUp(HeapNode node) {
        if (node.item.key <= node.parent.item.key) {
            return;
        }

        HeapItem nodeItem = node.item;
        HeapItem parentNodeItem = node.parent.item;
        node.item = parentNodeItem;
        node.parent.item = nodeItem;

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
    {    

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
        size--;

        min.node.prev.next = min.node.next;
        min.node.next.prev = min.node.prev;
        HeapNode node = min.node.next;
        HeapNode child = min.node.child;
        HeapNode currChild = child;

        //add minimum childs to the heap
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

        min.node.child = null;
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
            return;
        }
        if (heap2.min == null) {
            return;
        }

        //connect root lists
        min.node.next.prev = heap2.min.node.prev;
        heap2.min.node.prev.next = min.node.next;
        min.node.next = heap2.min.node;
        heap2.min.node.prev = min.node;

        //find new minimum
        if (heap2.min.key < min.key) {
            min = heap2.min;
        }

        //successive linking if required
        if (lazyMelds == false) {
            consolidate(min.node);
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
        return size;
    }


    /**
     * 
     * Return the number of trees in the heap.
     * 
     */
    public int numTrees()
    {
        return treesCount;
    }
    
    
    /**
     * 
     * Return the number of marked nodes in the heap.
     * 
     */
    public int numMarkedNodes()
    {
        return markedNodesCount;
    }
    
    
    /**
     * 
     * Return the total number of links.
     * 
     */
    public int totalLinks()
    {
        return linksCount;
    }
    
    
    /**
     * 
     * Return the total number of cuts.
     * 
     */
    public int totalCuts()
    {
        return cutsCount;
    }
    

    /**
     * 
     * Return the total heapify costs.
     * 
     */
    public int totalHeapifyCosts()
    {
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
