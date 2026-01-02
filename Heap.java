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
    public int treesCount = 0;
    public int markedNodesCount = 0;
    public int linksCount = 0;
    public int cutsCount = 0;
    public int heapifyCost = 0;
    
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
    public HeapNode link(HeapNode root1, HeapNode root2) {
        //throw Exception if different ranks?
        if (root1.key > root2.key) {
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

        return root1;
    }


    public void cut(HeapNode node, HeapNode parentNode) {
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
    }

    public void cascadingCut(HeapNode node, HeapNode parentNode) {
        if (parentNode != null) {
            cut(node, parentNode);
            cutsCount++;
            if (lazyDecreaseKeys == true) {
                Heap newHeap = new Heap(lazyMelds, lazyDecreaseKeys);
                newHeap.min = node;
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
    public HeapNode[] toBuckets() {
        int numBuckets = Integer.SIZE - Integer.numberOfLeadingZeros(size);
        HeapNode[] buckets = new HeapNode[numBuckets];
        
        HeapNode node1 = min;
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
        min.prev.next = min;
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

    /**
     * preforms successive linking on a heap and unifies it to a legal binomial heap
     */
    public void consolidate() {
        HeapNode[] bucketsList = toBuckets();
        min = fromBuckets(bucketsList);
    }

    /**
     * replaces the node's key and info with its parent recuresively
     * @param node node to start heapify from
     */
    public void heapifyUp(HeapNode node) {
        if (node.key <= node.parent.key) {
            return;
        }

        int node_key = node.key;
        String node_info = node.info;
        int parent_key = node.parent.key;
        String parent_info = node.parent.info;

        node.key = parent_key;
        node.info = parent_info;
        node.parent.key = node_key;
        node.parent.info = node_info;

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
    public HeapNode insert(int key, String info) 
    {    
        size++;

        //insert to an empty heap
        HeapNode node = new HeapNode();
        node.key = key;
        node.info = info;

        if (min == null) {
            min = node;
            min.next = min;
            min.prev = min;
            treesCount = 1;
            return node;
        }

        Heap heap2 = new Heap(lazyMelds, lazyDecreaseKeys);
        HeapNode newNode = heap2.insert(key, info);
        meld(heap2);
        return newNode;
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
        size--;

        min.prev.next = min.next;
        min.next.prev = min.prev;
        HeapNode node = min.next;
        HeapNode child = min.child;
        HeapNode currChild = child;

        //add minimum childs to the heap
        if (child != null){
            int min_rank = min.rank;
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
        min.child = null;
        min = node;
        consolidate();
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
        x.key -= diff;
        if (x.key < min.key) {
            min = x;
        }
        if (x.parent != null) {
            if (x.key >= x.parent.key) {
                return;
            }

            if (lazyDecreaseKeys == true) {
                cascadingCut(x, x.parent);
            }
            else {
                heapifyUp(x);
            }
        }
        return; 
    }

    /**
     * 
     * Delete the x from the heap.
     *
     */
    public void delete(HeapNode x) 
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
        public boolean isMarked;
    }
}
